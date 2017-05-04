package io.sugo.engine.server.http;

import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.engine.server.FileUtil;
import io.sugo.engine.server.conf.UserExtensionConfig;
import io.sugo.engine.server.conf.UserExtensionRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.userExtension.UserExtensionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by penghuan on 2017/4/25.
 */
@Path("/pio/userextension/")
public class UserExtensionResource {
    private final static Logger log = new Logger(UserExtensionResource.class);
    private UserExtensionConfig config;
    private static Integer taskId = 0;

    @Inject
    public UserExtensionResource(UserExtensionConfig config) {
        this.config = config;
        System.out.println(config.toString());
    }

    private Repository getRepository(String groupId) {
        return new UserExtensionRepository(config.getRepository() + "/" + groupId);
    }

    private void reCreateRepository(Repository repository) throws IOException {
        FileUtil.cleanDirectory(new File(repository.getPath()));
    }

    @POST
    @Path("/create/{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            InputStream in,
            @PathParam("groupId") final String groupId,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) throws IOException {
        log.info(String.format("Aaction:create groupId:%s", groupId));
        Status status = new Status();
        String paramStr = readStream(in);
        log.info(String.format("args:%s", paramStr));
        if (groupId.isEmpty()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } else {
            Repository repository = getRepository(groupId);
            if (UserExtensionStatus.isRunning(repository)) {
                status.status = 0;
                status.message = "上一次执行该任务还没有完成";
            } else {
                reCreateRepository(repository);
                UserExtensionStatus.setRunning(repository);
//                Thread userExtensionTrainer = new Thread(new UserExtensionEngineStarter(paramStr, (UserExtensionRepository) repository, config));
                Thread userExtensionTrainer = new Thread(new UserExtensionEngineServer(paramStr, (UserExtensionRepository) repository, config));
                userExtensionTrainer.setName((++taskId).toString());
                userExtensionTrainer.start();
                status.status = 1;
                status.message = "扩群计算中";
            }
        }
        return Response.status(Response.Status.ACCEPTED).entity(JSON.toJSONString(status)).build();
    }

    public static class Status {
        public String message = "";
        public Integer status = 0;
        public Integer code = 0;
        public Integer count = 0;
    }

    private static String readStream(InputStream inputStream) throws IOException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        inputStream.close();
        return outputStream.toString();
    }

}
