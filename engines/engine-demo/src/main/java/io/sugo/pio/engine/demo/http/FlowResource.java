package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
import io.sugo.pio.engine.flow.FlowModelFactory;
import io.sugo.pio.engine.flow.FlowQuery;
import io.sugo.pio.engine.flow.FlowResult;
import io.sugo.pio.engine.prediction.PredictionModel;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Path("query/flow")
public class FlowResource {
    public static final String PATH = "repositories/flow";
    private static PredictionModel<FlowResult> model;
    static {
        Repository repository = new LocalFileRepository(PATH);
        FlowModelFactory flowModelFactory = new FlowModelFactory(repository);
        model = flowModelFactory.loadModel();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPost(
            InputStream in,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) {
        try {
            ObjectMapper jsonMapper = ObjectMapperUtil.getObjectMapper();
            FlowQuery query = jsonMapper.readValue(in, FlowQuery.class);
            FlowResult flowResult = model.predict(query);
            String items = flowResult.getTotalPrice();
            String articles = flowResult.getGroups();
            Map<String, String> res = new HashMap<>();
            res.put("minPrice", items);
            res.put("bestCombin", articles);
            String str;
            if (!res.isEmpty()) {
                str = jsonMapper.writeValueAsString(res);
            } else {
                str = "items not found";
            }
            return Response.status(Response.Status.ACCEPTED).entity(str).build();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.ACCEPTED).entity("items not found").build();
    }
}
