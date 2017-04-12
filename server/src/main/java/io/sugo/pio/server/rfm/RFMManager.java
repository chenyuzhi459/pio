package io.sugo.pio.server.rfm;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.utils.HttpClientUtil;
import io.sugo.pio.data.fetcher.DataFetcherConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
public class RFMManager {

    private static final Logger log = new Logger(RFMManager.class);

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private static final JavaType javaType = jsonMapper.getTypeFactory().constructParametrizedType(List.class, ArrayList.class, DruidResult.class);

    private final String queryUrl;

    @Inject
    public RFMManager(DataFetcherConfig config) {
        Preconditions.checkNotNull(config.getUrl(), "must specify parameter: pio.broker.data.fetcher.url");
        queryUrl = config.getUrl();
    }

    public QuantileModel getDefaultQuantileModel(String queryStr, int r, int f, int m) {
        List<RFMModel> rfmModelList = fetchData(queryStr);
        int dataSize = rfmModelList.size();
        if (r > dataSize) {
            log.warn("The total data size is: " + dataSize + ", but the 'R' parameter is: " +
                    r + ". 'R' must be not greater than data size.");
            return QuantileModel.emptyModel(r, f, m);
        }
        if (f > dataSize) {
            log.warn("The total data size is: " + dataSize + ", but the 'F' parameter is: " +
                    f + ". 'F' must be not greater than data size.");
            return QuantileModel.emptyModel(r, f, m);
        }
        if (m > dataSize) {
            log.warn("The total data size is: " + dataSize + ", but the 'M' parameter is: " +
                    m + ". 'M' must be not greater than data size.");
            return QuantileModel.emptyModel(r, f, m);
        }

        DefaultQuantileCalculator calculator = new DefaultQuantileCalculator(rfmModelList, r, f, m);
        QuantileModel quantileModel = calculator.calculate();

        return quantileModel;
    }

    public QuantileModel getCustomizedQuantileModel(String queryStr, double[] rq, double[] fq, double[] mq) {
        List<RFMModel> rfmModelList = fetchData(queryStr);
        int dataSize = rfmModelList.size();
        if (rq.length + 1 > dataSize) {
            log.warn("The total data size is: " + dataSize + ", but the 'R' parameter is: " +
                    rq.length + 1 + ". 'R' must be not greater than data size.");
            return QuantileModel.emptyModel(rq, fq, mq);
        }
        if (fq.length + 1 > dataSize) {
            log.warn("The total data size is: " + dataSize + ", but the 'F' parameter is: " +
                    fq.length + 1 + ". 'F' must be not greater than data size.");
            return QuantileModel.emptyModel(rq, fq, mq);
        }
        if (mq.length + 1 > dataSize) {
            log.warn("The total data size is: " + dataSize + ", but the 'M' parameter is: " +
                    mq.length + 1 + ". 'M' must be not greater than data size.");
            return QuantileModel.emptyModel(rq, fq, mq);
        }

        CustomizedQuantileCalculator calculator = new CustomizedQuantileCalculator(rfmModelList, rq, fq, mq);
        QuantileModel quantileModel = calculator.calculate();

        return quantileModel;
    }

    private List<RFMModel> fetchData(String queryStr) {
        String resultStr = "";
        try {
            log.info("Begin to fetch RFM data from url: [%s], params: [%s].", queryUrl, queryStr);
            resultStr = HttpClientUtil.post(queryUrl, queryStr);
        } catch (IOException e) {
            log.error("Query druid '%s' with parameter '%s' failed: ", queryUrl, queryStr);
        }

        final List<RFMModel> rfmModelList = new ArrayList<>();
        try {
            List<DruidResult> druidResults = jsonMapper.readValue(resultStr, javaType);
            druidResults.forEach(druidResult -> {
                rfmModelList.add(druidResult.getEvent());
            });

            log.info("Fetch %d RFM data from druid %s.", druidResults.size(), queryUrl);
        } catch (IOException e) {
            log.warn("Deserialize druid result to type [" + DruidResult.class.getName() +
                    "] list failed, details:" + e.getMessage());
        }

        return rfmModelList;
    }

    private static class DruidResult {
        String version;
        Date timestamp;
        RFMModel event;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

        public RFMModel getEvent() {
            return event;
        }

        public void setEvent(RFMModel event) {
            this.event = event;
        }
    }

}
