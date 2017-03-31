package io.sugo.pio.server.rfm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.logger.Logger;
import io.sugo.pio.jackson.DefaultObjectMapper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class RFMManager {

    private static final Logger log = new Logger(RFMManager.class);

    private static final ObjectMapper jsonMapper = new DefaultObjectMapper();

    public List<RFMModel> getRFMModelList() {
        List<RFMModel> rfmModelList = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream("E:/RFM.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            String line = reader.readLine();

            for (; ; ) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }

                String[] rfmArray = line.split(",");
                RFMModel rfmModel = new RFMModel();
                rfmModel.setUserId(rfmArray[0]);
                rfmModel.setRecency(Double.valueOf(rfmArray[1]).intValue());
                rfmModel.setFrequency(Integer.valueOf(rfmArray[2]));
                rfmModel.setMonetary(Double.valueOf(rfmArray[3]));

                rfmModelList.add(rfmModel);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return rfmModelList;
    }

    public QuantileModel generateQuantileModel(List<RFMModel> rfmModelList, int r, int f, int m) {
        // Generate quantile model
        QuantileCalculator calculator = new QuantileCalculator(rfmModelList, r, f, m);
        QuantileModel quantileModel = calculator.calculate();

        Map<String, Integer> groupMap = quantileModel.getGroupMap();
        rfmModelList.forEach(rfmModel -> {
            // Label each of the rfm model
            rfmModel.setrLabel(quantileModel.getRLabel(rfmModel.getRecency()));
            rfmModel.setfLabel(quantileModel.getFLabel(rfmModel.getFrequency()));
            rfmModel.setmLabel(quantileModel.getMLabel(rfmModel.getMonetary()));

            // Statistic members of each group
            String group = rfmModel.getGroup();
            Integer count = groupMap.get(group);
            groupMap.put(group, ++count);
        });

        return quantileModel;
    }

    public static void main(String[] args) {
        RFMManager rfmManager = new RFMManager();
        List<RFMModel> rfmModelList = rfmManager.getRFMModelList();
        QuantileModel quantileModel = rfmManager.generateQuantileModel(rfmModelList, 2, 3, 4);

        try {
            System.out.println(
                    jsonMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(quantileModel)
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
