package io.sugo.pio.server.rfm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.utils.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class RFMManager {

    private static final Logger log = new Logger(RFMManager.class);

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private static final ObjectReader reader = jsonMapper.readerFor(Result.class);

    public QuantileModel getDefaultQuantileModel(String queryStr, int r, int f, int m) {
        List<RFMModel> rfmModelList = fetchData(queryStr);
        int dataSize = rfmModelList.size();
        if (r > dataSize) {
            throw new IllegalArgumentException("The total data size is: " + dataSize + ", but the 'R' parameter is: " +
                    r + ". 'R' must be not greater than data size.");
        }
        if (f > dataSize) {
            throw new IllegalArgumentException("The total data size is: " + dataSize + ", but the 'F' parameter is: " +
                    f + ". 'F' must be not greater than data size.");
        }
        if (m > dataSize) {
            throw new IllegalArgumentException("The total data size is: " + dataSize + ", but the 'M' parameter is: " +
                    m + ". 'M' must be not greater than data size.");
        }

        DefaultQuantileCalculator calculator = new DefaultQuantileCalculator(rfmModelList, r, f, m);
        QuantileModel quantileModel = calculator.calculate();

        return quantileModel;
    }

    public QuantileModel getCustomizedQuantileModel(String queryStr, double[] rq, double[] fq, double[] mq) {
        List<RFMModel> rfmModelList = fetchData(queryStr);
        int dataSize = rfmModelList.size();
        if (rq.length+1 > dataSize) {
            throw new IllegalArgumentException("The total data size is: " + dataSize + ", but the 'R' parameter is: " +
                    rq.length+1 + ". 'R' must be not greater than data size.");
        }
        if (fq.length+1 > dataSize) {
            throw new IllegalArgumentException("The total data size is: " + dataSize + ", but the 'F' parameter is: " +
                    fq.length+1 + ". 'F' must be not greater than data size.");
        }
        if (mq.length+1 > dataSize) {
            throw new IllegalArgumentException("The total data size is: " + dataSize + ", but the 'M' parameter is: " +
                    mq.length+1 + ". 'M' must be not greater than data size.");
        }

        CustomizedQuantileCalculator calculator = new CustomizedQuantileCalculator(rfmModelList, rq, fq, mq);
        QuantileModel quantileModel = calculator.calculate();

        return quantileModel;
    }

    private List<RFMModel> fetchData(String queryStr) {
        String requestJson = buildQuery(queryStr);
        String resultStr = "";
        try {
            resultStr = HttpClientUtil.post("http://192.168.0.212:8000/api/plyql/sql", requestJson);
        } catch (IOException e) {
            log.error("Query druid '%s' with parameter '%s' failed: ", "", requestJson);
        }

        List<RFMModel> rfmModelList = new ArrayList<>();
        try {
            Result result = reader.readValue(resultStr);
            rfmModelList = result.getResult();
        } catch (IOException e) {
            log.warn("Deserialize '" + resultStr + "' to type [" + RFMModel.class.getName() +
                    "] list failed, details:" + e.getMessage());
        }

        log.info("Fetch %d RFM data.", rfmModelList.size());

        return rfmModelList;
    }

    private String buildQuery(String queryStr) {
        Query query = new Query();
        query.setQuery(queryStr);

        try {
            return jsonMapper.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            log.error("Deserialize query failed: ", e);
            return null;
        }
    }

    private static class Query {
        String query;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }

    private static class Result {
        List<RFMModel> result;
        int code;

        public List<RFMModel> getResult() {
            return result;
        }

        public void setResult(List<RFMModel> result) {
            this.result = result;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

}
