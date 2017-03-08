package io.sugo.pio.operator.preprocessing.filter.attributes;


import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.features.selection.AbstractFeatureSelection;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.tools.AttributeSubsetSelector;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * This operator filters the attributes of an exampleSet. Therefore, different conditions may be
 * selected as parameter and only attributes fulfilling this condition are kept. The rest will be
 * removed from the exampleSet The conditions may be inverted. The conditions are tested over all
 * attributes and for every attribute over all examples. For example the numeric_value_filter with
 * the parameter string &quot;&gt; 6&quot; will keep all nominal attributes and all numeric
 * attributes having a value of greater 6 in every example. A combination of conditions is possible:
 * &quot;&gt; 6 ANDAND &lt; 11&quot; or &quot;&lt;= 5 || &lt; 0&quot;. But ANDAND and || must not be
 * mixed. Please note that ANDAND has to be replaced by two ampers ands.
 * </p>
 * <p>
 * <p>
 * The attribute_name_filter keeps all attributes which names match the given regular expression.
 * The nominal_value_filter keeps all numeric attribute and all nominal attributes containing at
 * least one of specified nominal values. &quot;rainy ANDAND cloudy&quot; would keep all attributes
 * containing at least one time &quot;rainy&quot; and one time &quot;cloudy&quot;. &quot;rainy ||
 * sunny&quot; would keep all attributes containing at least one time &quot;rainy&quot; or one time
 * &quot;sunny&quot;. ANDAND and || are not allowed to be mixed. And again, ANDAND has to be
 * replaced by two ampers ands.
 * </p>
 */
public class AttributeFilter extends AbstractFeatureSelection {

    private static final Logger logger = new Logger(AttributeFilter.class);

    private final AttributeSubsetSelector attributeSelector = new AttributeSubsetSelector(this, getExampleSetInputPort());

    @Override
    protected MetaData modifyMetaData(ExampleSetMetaData metaData) {
        ExampleSetMetaData subset = attributeSelector.getMetaDataSubset(metaData, true);
        Iterator<AttributeMetaData> amdIterator = metaData.getAllAttributes().iterator();
        while (amdIterator.hasNext()) {
            AttributeMetaData amd = amdIterator.next();
            AttributeMetaData subsetAMD = subset.getAttributeByName(amd.getName());
            if (subsetAMD == null) {
                amdIterator.remove();
            }
        }
        return metaData;
    }

    @Override
    public ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
        logger.info("AttributeFilter begin to apply example set[%s]", exampleSet.getName());

        Attributes attributes = exampleSet.getAttributes();
        Set<Attribute> attributeSubset = attributeSelector.getAttributeSubset(exampleSet, true);
        Iterator<Attribute> r = attributes.allAttributes();
        while (r.hasNext()) {
            Attribute attribute = r.next();
            if (!attributeSubset.contains(attribute)) {
                r.remove();
            }
        }

        logger.info("AttributeFilter apply example set[%s] successfully!", exampleSet.getName());

        return exampleSet;
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.AttributeFilter.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.processing;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.AttributeFilter.description");
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
//		types.addAll(attributeSelector.getParameterTypes());
        types.addAll(attributeSelector.getSubsetAttributeFilterParamTypes());
        return types;
    }
}
