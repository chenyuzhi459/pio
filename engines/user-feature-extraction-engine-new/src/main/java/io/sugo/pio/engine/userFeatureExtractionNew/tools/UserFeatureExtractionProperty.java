package io.sugo.pio.engine.userFeatureExtractionNew.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by penghuan on 2017/4/25.
 */
public class UserFeatureExtractionProperty {

    public static enum Index {
        Input("Input"),
        OutputModel("OutputModel"),
        OutputFeatureWeight("OutputFeatureWeight"),
        OutputFeatureDesign("OutputFeatureDesign");

        public String name;
        private Index(String name) {
            this.name = name;
        }
    }

    public static Map<Enum, String> keys = new HashMap<Enum, String>() {
        {
            put(Index.Input, "pio.user.feature.hdfs.input");
            put(Index.OutputModel, "pio.user.feature.hdfs.output.model");
            put(Index.OutputFeatureWeight, "pio.user.feature.hdfs.output.weight");
            put(Index.OutputFeatureDesign, "pio.user.feature.hdfs.output.design");
        }
    };
}
