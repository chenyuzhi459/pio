package io.sugo.pio.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.io.ByteSource;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.metamx.common.RE;
import com.metamx.common.logger.Logger;
import com.metamx.http.client.HttpClient;
import com.metamx.http.client.Request;
import com.metamx.http.client.response.ClientResponse;
import com.metamx.http.client.response.HttpResponseHandler;
import io.sugo.pio.query.Query;
import io.sugo.pio.query.QueryInterruptedException;
import io.sugo.pio.query.QueryRunner;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponse;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 */
public class DirectPioClient<Q, R> implements QueryRunner<Q, R> {
    private static final Logger log = new Logger(DirectPioClient.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String host;

    private final AtomicInteger openConnections;

    public DirectPioClient(
            ObjectMapper objectMapper,
            HttpClient httpClient,
            String host) {
        this.httpClient = httpClient;
        this.host = host;
        this.objectMapper = objectMapper;
        this.openConnections = new AtomicInteger();
    }

    public int getNumOpenConnections()
    {
        return openConnections.get();
    }

    @Override
    public R run(Query<Q> query, Map<String, Object> context) {
        final ListenableFuture<InputStream> future;
        final String url = String.format("http://%s/pio/v2/", host);

        try {
            final HttpResponseHandler<InputStream, InputStream> responseHandler = new HttpResponseHandler<InputStream, InputStream>()
            {
                private long responseStartTime;
                private final AtomicLong byteCount = new AtomicLong(0);
                private final BlockingQueue<InputStream> queue = new LinkedBlockingQueue<>();
                private final AtomicBoolean done = new AtomicBoolean(false);

                @Override
                public ClientResponse<InputStream> handleResponse(HttpResponse response)
                {
                    responseStartTime = System.currentTimeMillis();
                    try {
                        final String responseContext = response.headers().get("X-Druid-Response-Context");
                        // context may be null in case of error or query timeout
                        if (responseContext != null) {
                            context.putAll(
                                    objectMapper.<Map<String, Object>>readValue(
                                            responseContext, new TypeReference<Map<String, Object>>()
                                            {
                                            }
                                    )
                            );
                        }
                        queue.put(new ChannelBufferInputStream(response.getContent()));
                    }
                    catch (final IOException e) {
                        log.error(e, "Error parsing response context from url [%s]", url);
                        return ClientResponse.<InputStream>finished(
                                new InputStream()
                                {
                                    @Override
                                    public int read() throws IOException
                                    {
                                        throw e;
                                    }
                                }
                        );
                    }
                    catch (InterruptedException e) {
                        log.error(e, "Queue appending interrupted");
                        Thread.currentThread().interrupt();
                        throw Throwables.propagate(e);
                    }
                    byteCount.addAndGet(response.getContent().readableBytes());
                    return ClientResponse.<InputStream>finished(
                            new SequenceInputStream(
                                    new Enumeration<InputStream>()
                                    {
                                        @Override
                                        public boolean hasMoreElements()
                                        {
                                            // Done is always true until the last stream has be put in the queue.
                                            // Then the stream should be spouting good InputStreams.
                                            synchronized (done) {
                                                return !done.get() || !queue.isEmpty();
                                            }
                                        }

                                        @Override
                                        public InputStream nextElement()
                                        {
                                            try {
                                                return queue.take();
                                            }
                                            catch (InterruptedException e) {
                                                Thread.currentThread().interrupt();
                                                throw Throwables.propagate(e);
                                            }
                                        }
                                    }
                            )
                    );
                }

                @Override
                public ClientResponse<InputStream> handleChunk(
                        ClientResponse<InputStream> clientResponse, HttpChunk chunk
                )
                {
                    final ChannelBuffer channelBuffer = chunk.getContent();
                    final int bytes = channelBuffer.readableBytes();
                    if (bytes > 0) {
                        try {
                            queue.put(new ChannelBufferInputStream(channelBuffer));
                        }
                        catch (InterruptedException e) {
                            log.error(e, "Unable to put finalizing input stream into Sequence queue for url [%s]", url);
                            Thread.currentThread().interrupt();
                            throw Throwables.propagate(e);
                        }
                        byteCount.addAndGet(bytes);
                    }
                    return clientResponse;
                }

                @Override
                public ClientResponse<InputStream> done(ClientResponse<InputStream> clientResponse)
                {
                    long stopTime = System.currentTimeMillis();
                    log.debug(
                            "Completed queryId[%s] request to url[%s] with %,d bytes returned in %,d millis [%,f b/s].",
                            query,
                            url,
                            byteCount.get(),
                            stopTime - responseStartTime,
                            byteCount.get() / (0.0001 * (stopTime - responseStartTime))
                    );
                    synchronized (done) {
                        try {
                            // An empty byte array is put at the end to give the SequenceInputStream.close() as something to close out
                            // after done is set to true, regardless of the rest of the stream's state.
                            queue.put(ByteSource.empty().openStream());
                        }
                        catch (InterruptedException e) {
                            log.error(e, "Unable to put finalizing input stream into Sequence queue for url [%s]", url);
                            Thread.currentThread().interrupt();
                            throw Throwables.propagate(e);
                        }
                        catch (IOException e) {
                            // This should never happen
                            throw Throwables.propagate(e);
                        }
                        finally {
                            done.set(true);
                        }
                    }
                    return ClientResponse.<InputStream>finished(clientResponse.getObj());
                }

                @Override
                public void exceptionCaught(final ClientResponse<InputStream> clientResponse, final Throwable e)
                {
                    // Don't wait for lock in case the lock had something to do with the error
                    synchronized (done) {
                        done.set(true);
                        // Make a best effort to put a zero length buffer into the queue in case something is waiting on the take()
                        // If nothing is waiting on take(), this will be closed out anyways.
                        queue.offer(
                                new InputStream()
                                {
                                    @Override
                                    public int read() throws IOException
                                    {
                                        throw new IOException(e);
                                    }
                                }
                        );
                    }
                }
            };
            future = httpClient.go(
                    new Request(
                            HttpMethod.POST,
                            new URL(url)
                    ).setContent(objectMapper.writeValueAsBytes(query))
                            .setHeader(
                                    HttpHeaders.Names.CONTENT_TYPE,
                                    MediaType.APPLICATION_JSON
                            ),
                    responseHandler
            );

            openConnections.getAndIncrement();
            Futures.addCallback(
                    future, new FutureCallback<InputStream>()
                    {
                        @Override
                        public void onSuccess(InputStream result)
                        {
                            openConnections.getAndDecrement();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            openConnections.getAndDecrement();
                        }
                    }
            );
        }  catch (IOException e) {
            throw Throwables.propagate(e);
        }

        try {
            InputStream inputStream = future.get();
            return objectMapper.readValue(inputStream, new TypeReference<R>(){
            });
        }
        catch (IOException | InterruptedException | ExecutionException e) {
            throw new RE(e, "Failure getting results from[%s] because of [%s]", url, e.getMessage());
        }
        catch (CancellationException e) {
            throw new QueryInterruptedException(e, host);
        }
    }
}
