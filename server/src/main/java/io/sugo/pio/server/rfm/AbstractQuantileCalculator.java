package io.sugo.pio.server.rfm;

import com.metamx.common.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public abstract class AbstractQuantileCalculator {

    private static final Logger log = new Logger(AbstractQuantileCalculator.class);

    protected List<RFMModel> rfmModelList;

    protected QuantileModel quantileModel;

    public AbstractQuantileCalculator(List<RFMModel> rfmModelList) {
        this.rfmModelList = rfmModelList;
    }

    public QuantileModel calculate() {

        initModel();

        Map<String /* groupName */, List<String> /* userIds */> groupUserIdsMap = new HashMap<>();
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

            // Classify users to each group
            List<String> userIdList = groupUserIdsMap.get(group);
            if (userIdList == null) {
                userIdList = new ArrayList<>();
                groupUserIdsMap.put(group, userIdList);
            }
            userIdList.add(rfmModel.getUserId());
        });

        quantileModel.buildGroups(groupUserIdsMap, rfmModelList.size());

        log.info("Calculate quantile model successfully! %s", quantileModel);

        return quantileModel;
    }

    protected abstract void initModel();

}
