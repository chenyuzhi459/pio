package io.sugo.pio.server.rfm;

import java.util.List;
import java.util.Map;

/**
 */
public abstract class AbstractQuantileCalculator {

    protected List<RFMModel> rfmModelList;

    protected QuantileModel quantileModel;

    public AbstractQuantileCalculator(List<RFMModel> rfmModelList) {
        this.rfmModelList = rfmModelList;
    }

    public QuantileModel calculate() {

        initModel();

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

        quantileModel.buildGroups(rfmModelList.size());

        return quantileModel;
    }

    protected abstract void initModel();

}
