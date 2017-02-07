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
    @Path("/infoByTitle/{movieTitle}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfoByTitle(
            InputStream in,
            @PathParam("movieTitle") String title,
            @Context final HttpServletRequest req
    ) {
        try {
            Map<String, Object> movieInfo = MovieInfoUtil.getMovieInfoByTitle(title);
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
        return Response.status(Response.Status.ACCEPTED).entity("items not found, title="+ title).build();
    }

    @GET
    @Path("/infoById/{imdbId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfoById(
            InputStream in,
            @PathParam("imdbId") String id,
            @Context final HttpServletRequest req
    ) {
        try {
            Map<String, Object> movieInfo = MovieInfoUtil.getMovieInfoById(id);
            String posterAddress = (String)movieInfo.get("Poster");
            String picName = "/movie_images/" + posterAddress.substring(posterAddress.lastIndexOf("/")+1);
            movieInfo.put("Poster", picName);
            ObjectMapper jsonMapper = ObjectMapperUtil.getObjectMapper();
            String str = jsonMapper.writeValueAsString(movieInfo);
            return Response.status(Response.Status.ACCEPTED).entity(str).build();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.ACCEPTED).entity("items not found").build();
    }
}
