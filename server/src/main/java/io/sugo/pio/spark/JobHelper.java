package io.sugo.pio.spark;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;
import com.metamx.common.RetryUtils;
import com.metamx.common.logger.Logger;
import io.sugo.pio.spark.handler.SparkSubmissionHandler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 */
public class JobHelper {
    private static final Logger log = new Logger(JobHelper.class);

    private static final int NUM_RETRIES = 8;

    public static Path distributedClassPath(String path)
    {
        return distributedClassPath(new Path(path));
    }

    public static Path distributedClassPath(Path base)
    {
        return new Path(base, "classpath");
    }

    /**
     * Uploads jar files to hdfs and configures the classpath.
     * Snapshot jar files are uploaded to intermediateClasspath and not shared across multiple jobs.
     * Non-Snapshot jar files are uploaded to a distributedClasspath and shared across multiple jobs.
     *
     * @param distributedClassPath  classpath shared across multiple jobs
     * @param intermediateClassPath classpath exclusive for this job. used to upload SNAPSHOT jar files.
     * @param conf                   job to runAsyn
     *
     * @throws IOException
     */
    public static void setupClasspath(
            final Path distributedClassPath,
            final Path intermediateClassPath,
            final Configuration conf,
            final SparkSubmissionHandler handler
            )
            throws IOException
    {
        String classpathProperty = System.getProperty("pio.spark.internal.classpath");
        if (classpathProperty == null) {
            classpathProperty = System.getProperty("java.class.path");
        }

        String[] jarFiles = classpathProperty.split(File.pathSeparator);

        final FileSystem fs = distributedClassPath.getFileSystem(conf);

        if (fs instanceof LocalFileSystem) {
            return;
        }

        List<String> additionalJarsList = new ArrayList<>();
        for (String jarFilePath : jarFiles) {

            final File jarFile = new File(jarFilePath);
            if (jarFile.getName().endsWith(".jar")) {
                try {
                    RetryUtils.retry(
                            new Callable<Boolean>()
                            {
                                @Override
                                public Boolean call() throws Exception
                                {
                                    addJarToClassPath(jarFile, distributedClassPath, intermediateClassPath, fs, additionalJarsList);
                                    return true;
                                }
                            },
                            shouldRetryPredicate(),
                            NUM_RETRIES
                    );
                }
                catch (Exception e) {
                    throw Throwables.propagate(e);
                }
            }
        }
        handler.setAdditionalJars(additionalJarsList);
    }

    public static final Predicate<Throwable> shouldRetryPredicate()
    {
        return new Predicate<Throwable>()
        {
            @Override
            public boolean apply(Throwable input)
            {
                if (input == null) {
                    return false;
                }
                if (input instanceof IOException) {
                    return true;
                }
                return apply(input.getCause());
            }
        };
    }

    static void addJarToClassPath(
            File jarFile,
            Path distributedClassPath,
            Path intermediateClassPath,
            FileSystem fs,
            List<String> additionalJarsList
    )
            throws IOException
    {
        // Create distributed directory if it does not exist.
        // rename will always fail if destination does not exist.
        fs.mkdirs(distributedClassPath);

        // Non-snapshot jar files are uploaded to the shared classpath.
        final Path hdfsPath = new Path(distributedClassPath, jarFile.getName());
        if (!fs.exists(hdfsPath)) {
            // Muliple jobs can try to upload the jar here,
            // to avoid them from overwriting files, first upload to intermediateClassPath and then rename to the distributedClasspath.
            final Path intermediateHdfsPath = new Path(intermediateClassPath, jarFile.getName());
            uploadJar(jarFile, intermediateHdfsPath, fs);
            IOException exception = null;
            try {
                log.info("Renaming jar to path[%s]", hdfsPath);
                fs.rename(intermediateHdfsPath, hdfsPath);
                if (!fs.exists(hdfsPath)) {
                    throw new IOException(
                            String.format(
                                    "File does not exist even after moving from[%s] to [%s]",
                                    intermediateHdfsPath,
                                    hdfsPath
                            )
                    );
                }
            }
            catch (IOException e) {
                // rename failed, possibly due to race condition. check if some other job has uploaded the jar file.
                try {
                    if (!fs.exists(hdfsPath)) {
                        log.error(e, "IOException while Renaming jar file");
                        exception = e;
                    }
                }
                catch (IOException e1) {
                    e.addSuppressed(e1);
                    exception = e;
                }
            }
            finally {
                try {
                    if (fs.exists(intermediateHdfsPath)) {
                        fs.delete(intermediateHdfsPath, false);
                    }
                }
                catch (IOException e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
                if (exception != null) {
                    throw exception;
                }
            }
        }
        additionalJarsList.add(hdfsPath.toString());
    }

    static void uploadJar(File jarFile, final Path path, final FileSystem fs) throws IOException
    {
        log.info("Uploading jar to path[%s]", path);
        ByteStreams.copy(
                Files.newInputStreamSupplier(jarFile),
                new OutputSupplier<OutputStream>()
                {
                    @Override
                    public OutputStream getOutput() throws IOException
                    {
                        return fs.create(path);
                    }
                }
        );
    }
}
