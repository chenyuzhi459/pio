package io.sugo.pio.engine.common.data;

import io.sugo.pio.engine.data.output.LocalFileRepository;

/**
 * Created by penghuan on 2017/4/8.
 */
public class UserExtensionRepository extends LocalFileRepository {
    private String modelFeature;
    private String modelDistance;
    private String predictFeature;
    private String predictDistance;
    private String predictDesign;
    private String recordValueName;
    private Integer maxUser = 0;

    public UserExtensionRepository(
            String path,
            String modelFeature,
            String modelDistance,
            String predictFeature,
            String predictDistance,
            String predictDesign,
            String recordValueName
    ) {
        super(path);
        this.modelFeature = modelFeature;
        this.modelDistance = modelDistance;
        this.predictFeature = predictFeature;
        this.predictDistance = predictDistance;
        this.predictDesign = predictDesign;
        this.recordValueName = recordValueName;
    }

    public String getModelFeaturePath() {
        return getPath() + "/" + modelFeature;
    }

    public String getModelDistancePath() {
        return getPath() + "/" + modelDistance;
    }

    public String getPredictFeatureFile() {
        return getPath() + "/" + predictFeature;
    }

    public String getPredictFeatureFileName() {
        return predictFeature;
    }

    public String getPredictDistanceFile() {
        return getPath() + "/" + predictDistance;
    }

    public String getPredictDistanceFileName() {
        return predictDistance;
    }

    public String getPredictDesignFile() {
        return getPath() + "/" + predictDesign;
    }

    public String getPredictDesignFileName() {
        return predictDesign;
    }

    public void createPredictFeatureFile() {
        create(predictFeature);
    }

    public void createPredictDistanceFile() {
        create(predictDistance);
    }

    public String getRecordValueNameFile() {
        return getPath() + "/" + recordValueName;
    }

    public String getRecordValueNameFileName() {
        return recordValueName;
    }

    public void setMaxUser(Integer num) {
        maxUser = num;
    }

    public Integer getMaxUser() {
        return maxUser;
    }

}
