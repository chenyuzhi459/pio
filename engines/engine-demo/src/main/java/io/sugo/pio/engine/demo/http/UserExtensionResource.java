package io.sugo.pio.engine.demo.http;

import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.engine.common.data.UserExtensionRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.FileUtil;
import io.sugo.pio.engine.demo.conf.UserExtensionConfig;
import io.sugo.pio.engine.userExtension.UserExtensionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Path("/pio/userextension/")
public class UserExtensionResource {
    private final static Logger log = new Logger(UserExtensionResource.class);
    private String REPOSITORY_PATH = "repositories/user_extension";
    private UserExtensionConfig config;
    private static Integer taskId = 0;

    @Inject
    public UserExtensionResource(UserExtensionConfig config) {
        this.config = config;
        System.out.println(config.toString());
        REPOSITORY_PATH = config.getRepository();
    }

    private String getRepositoryPath(String groupId) {
        return REPOSITORY_PATH + "/" + groupId;
    }

    private Repository getRepository(String groupId) {
        String repositoryPath = getRepositoryPath(groupId);
        return new UserExtensionRepository(
                repositoryPath,
                Constants.USER_EXT_MODEL_FEATURE,
                Constants.USER_EXT_MODEL_DISTANCE,
                Constants.USER_EXT_FEATURE_FILE,
                Constants.USER_EXT_VECTOR_FILE,
                Constants.USER_EXT_DESIGN_FILE,
                Constants.USER_EXT_VALUE_NAME_FILE
        );
    }

    private Repository newRepository(String groupId) throws IOException {
        String repositoryPath = getRepositoryPath(groupId);
        FileUtil.deleteDirectory(new File(repositoryPath));
        return getRepository(groupId);
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
            Repository tmpRepository = getRepository(groupId);
            if (UserExtensionStatus.isRunning(tmpRepository)) {
                status.status = 0;
                status.message = "上一次执行该任务还没有完成";
            } else {
                Repository repository = newRepository(groupId);
                UserExtensionStatus.setRunning(repository);
                Thread userExtensionTrainer = new Thread(new UserExtensionTrainer(paramStr, repository, config));
                userExtensionTrainer.setName((++taskId).toString());
                userExtensionTrainer.start();
                status.status = 1;
                status.message = "扩群计算中";
            }
        }
        return Response.status(Response.Status.ACCEPTED).entity(JSON.toJSONString(status)).build();
    }

    @GET
    @Path("/query/{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response query(@PathParam("groupId") final String groupId) {
        log.info(String.format("Aaction:query groupId:%s", groupId));
        UserExtensionInfo userExtensionInfo = new UserExtensionInfo();
        if (!groupId.isEmpty()) {
            Repository repository = getRepository(groupId);
            if (UserExtensionStatus.isSuccess(repository)) {
                userExtensionInfo.loadUser(repository);
            }
        }
        return Response.status(Response.Status.OK).entity(JSON.toJSONString(userExtensionInfo)).build();
    }

    @DELETE
    @Path("/delete/{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("groupId") final String groupId) throws IOException {
        log.info(String.format("Aaction:delete groupId:%s", groupId));
        if (!groupId.isEmpty()) {
            Repository repository = getRepository(groupId);
            if (!UserExtensionStatus.isRunning(repository)) {
                FileUtil.deleteDirectory(new File(getRepositoryPath(groupId)));
            } else {
                Response.status(Response.Status.FORBIDDEN).entity("{}").build();
            }
        }
        return Response.status(Response.Status.OK).entity("{}").build();
    }

    @GET
    @Path("/status/{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response status(@PathParam("groupId") final String groupId) {
        log.info(String.format("Aaction:status groupId:%s", groupId));
        Status status = new Status();
        if (groupId.isEmpty()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } else {
            Repository repository = getRepository(groupId);
            if (UserExtensionStatus.isFailed(repository)) {
                status.status = 3;
                UserExtensionStatus.Info info = UserExtensionStatus.getInfo(repository);
                status.code = info.getCode();
                status.message = info.getMessage();
            } else if (UserExtensionStatus.isRunning(repository)) {
                status.status = 1;
            } else if (UserExtensionStatus.isSuccess(repository)) {
                UserExtensionInfo userExtensionInfo = new UserExtensionInfo();
                userExtensionInfo.getUserNum(repository);
                status.count = userExtensionInfo.count;
                status.status = 2;
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        }
        return Response.status(Response.Status.OK).entity(JSON.toJSONString(status)).build();
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

    public static class Status {
        public String message = "";
        public Integer status = 0;
        public Integer code = 0;
        public Integer count = 0;
    }

    public static class UserExtensionInfo {
        public List<String> ids = new ArrayList<String>();
        public Integer count = 0;

        public void loadUser(Repository repository) {
            try {
                String line = null;
                String file = repository.getPath() + "/" + Constants.USER_EXT_VECTOR_FILE;
                BufferedReader br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) {
                        continue;
                    } else {
                        ids.add(line.split(":")[0]);
                        count = count + 1;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public int getUserNum(Repository repository) {
            String userFile = repository.getPath() + "/" + Constants.USER_EXT_VECTOR_FILE;
            try {
                BufferedReader br = new BufferedReader(new FileReader(userFile));
                while (br.readLine() != null) {
                    count = count + 1;
                }
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return count;
        }
    }
}
