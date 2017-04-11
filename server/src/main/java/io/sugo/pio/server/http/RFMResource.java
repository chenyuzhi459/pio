package io.sugo.pio.server.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.server.http.dto.CustomizedRFMDto;
import io.sugo.pio.server.http.dto.DefaultRFMDto;
import io.sugo.pio.server.rfm.QuantileModel;
import io.sugo.pio.server.rfm.RFMManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pio/process/rfm/")
public class RFMResource {

    private final RFMManager rfmManager;

    private final ObjectMapper jsonMapper;

    @Inject
    public RFMResource(@Json ObjectMapper jsonMapper, RFMManager rfmManager) {
        this.jsonMapper = jsonMapper;
        this.rfmManager = rfmManager;
    }

    @POST
    @Path("/slice/default")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response slice(DefaultRFMDto rfmDto) {
        check(rfmDto);
        try {
            String queryStr = rfmDto.buildQuery();
            QuantileModel quantileModel = rfmManager.getDefaultQuantileModel(rfmDto.getHost(), queryStr,
                    rfmDto.getR(), rfmDto.getF(), rfmDto.getM());
            return Response.ok(quantileModel).header("Access-Control-Allow-Origin", "*").build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    @POST
    @Path("/slice/customized")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response sliceCustomized(CustomizedRFMDto rfmDto) {
        check(rfmDto);
        try {
            String queryStr = rfmDto.getQuery();
            QuantileModel quantileModel = rfmManager.getCustomizedQuantileModel(rfmDto.getHost(), queryStr,
                    rfmDto.getRq(), rfmDto.getFq(), rfmDto.getMq());

            return Response.ok(quantileModel).header("Access-Control-Allow-Origin", "*").build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    private void check(DefaultRFMDto rfmDto) {
        Preconditions.checkNotNull(rfmDto.getHost(), "Host can not be null.");
        Preconditions.checkNotNull(rfmDto.getDatasource(), "Data source can not be null.");
        if (rfmDto.getR() <= 0) {
            throw new IllegalArgumentException("'R' must be greater than 0.");
        }
        if (rfmDto.getF() <= 0) {
            throw new IllegalArgumentException("'F' must be greater than 0.");
        }
        if (rfmDto.getM() <= 0) {
            throw new IllegalArgumentException("'M' must be greater than 0.");
        }
    }

    private void check(CustomizedRFMDto rfmDto) {
        Preconditions.checkNotNull(rfmDto.getHost(), "Host can not be null.");
        Preconditions.checkNotNull(rfmDto.getDatasource(), "Data source can not be null.");
        if (rfmDto.getRq().length <= 0) {
            throw new IllegalArgumentException("'RQ' must be at least contains one element.");
        }
        if (rfmDto.getFq().length <= 0) {
            throw new IllegalArgumentException("'FQ' must be at least contains one element.");
        }
        if (rfmDto.getMq().length <= 0) {
            throw new IllegalArgumentException("'MQ' must be at least contains one element.");
        }
    }

}
