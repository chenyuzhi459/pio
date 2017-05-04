package io.sugo.engine.server.conf;

import io.sugo.pio.engine.data.output.LocalFileRepository;

/**
 * Created by penghuan on 2017/4/26.
 */
public class UserExtensionRepository extends LocalFileRepository {
    private static String eventRepo = "evnet";
    private static String featureModelRepo = "feature_model";
    private static String featureWeightRepo = "feature_weight";
    private static String featureDesignRepo = "feature_design";

    public static enum RepositoryName {
        Root(""),
        Event("event"),
        FeatureModel("feature_model"),
        FeatureWeight("feature_weight"),
        FeatureDesign("feature_design");

        private String name;
        RepositoryName(String name) {
            this.name = name;
        }

        public String getRepositoryName() {
            return name;
        }
    }

    public UserExtensionRepository (String path) {
        super(path);
    }

    public String getRepository(RepositoryName repositoryName) {
        return getPath() + "/" + repositoryName.getRepositoryName();
    }

}
