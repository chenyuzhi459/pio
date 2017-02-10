package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.demo.MovieInfoUtil;
import io.sugo.pio.engine.demo.ObjectMapperUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 */
@Path("query/movie")
public class MovieInfoResource {
    @GET
    @Path("/infoByMovId/{movieId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfoByMovId(
            InputStream in,
            @PathParam("movieId") String movieId,
            @Context final HttpServletRequest req
    ) {
        try {
            Map<String, Object> movieInfo = MovieInfoUtil.getMovieInfoByMovId(movieId);
            String posterAddress = (String)movieInfo.get("Poster");
            String picName = "/movie_images/" + posterAddress.substring(posterAddress.lastIndexOf("/")+1);
            movieInfo.put("Poster", picName);
            ObjectMapper jsonMapper = ObjectMapperUtil.getObjectMapper();
            String str = jsonMapper.writeValueAsString(movieInfo);
            return Response.status(Response.Status.ACCEPTED).entity(str).build();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return Response.status(Response.Status.ACCEPTED).entity("items not found, title="+ movieId).build();
    }
}
