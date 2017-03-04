package io.sugo.pio.operator.clustering.clusterer;

import io.sugo.pio.i18n.I18N;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;

import java.util.List;


/**
 * Adds a parameter by which the user can choose to generate a cluster attribute. All clusterers
 * other than the Weka clusterers should extends this class rather than AbstractClusterer directly.
 *
 */
public abstract class RMAbstractClusterer extends AbstractClusterer {

    /**
     * The parameter name for &quot;Indicates if a cluster id is generated as new special
     * attribute.&quot;
     */
    public static final String PARAMETER_ADD_CLUSTER_ATTRIBUTE = "add_cluster_attribute";
    public static final String PARAMETER_REMOVE_UNLABELED = "remove_unlabeled";
    public static final String PARAMETER_ADD_AS_LABEL = "add_as_label";

    public RMAbstractClusterer() {
        super();
    }

    @Override
    protected boolean addsClusterAttribute() {
        return getParameterAsBoolean(PARAMETER_ADD_CLUSTER_ATTRIBUTE);
    }

    @Override
    protected boolean addsIdAttribute() {
        return true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterType type = new ParameterTypeBoolean(
                PARAMETER_ADD_CLUSTER_ATTRIBUTE,
                I18N.getMessage("pio.RMAbstractClusterer.add_cluster_attribute"),
                true, false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_ADD_AS_LABEL,
                I18N.getMessage("pio.RMAbstractClusterer.add_as_label"),
                false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_REMOVE_UNLABELED,
                I18N.getMessage("pio.RMAbstractClusterer.remove_unlabeled"),
                false);
        type.setExpert(false);
        types.add(type);

        return types;
    }

}
