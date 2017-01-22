//package io.sugo.pio.ports.quickfix;
//
//
//import io.sugo.pio.operator.Operator;
//import io.sugo.pio.parameter.*;
//
//import java.util.LinkedList;
//import java.util.List;
//
//
///**
// * @author Sebastian Land
// */
//public class ParameterSettingQuickFix extends AbstractQuickFix {
//
//    private Operator operator;
//    private String parameterName;
//    private String value;
//
//    public ParameterSettingQuickFix(Operator operator, String parameterName) {
//        this(operator, parameterName, null, "set_parameter", new Object[]{parameterName.replace('_', ' ')});
//
//        ParameterType type = operator.getParameterType(parameterName);
//        if (type instanceof ParameterTypeConfiguration) {
//            seti18nKey("set_parameters_using_wizard");
//        } else if (type instanceof ParameterTypeList) {
//            seti18nKey("set_parameter_list", parameterName);
//        }
//    }
//
//    public ParameterSettingQuickFix(Operator operator, String parameterName, String value) {
//        this(operator, parameterName, value, "correct_parameter_settings_by", parameterName, value);
//
//        ParameterType type = operator.getParameterType(parameterName);
//        if (type instanceof ParameterTypeConfiguration) {
//            seti18nKey("correct_parameter_settings_with_wizard");
//        } else if (type instanceof ParameterTypeList) {
//            seti18nKey("correct_parameter_settings_list", parameterName);
//        } else {
//        }
//        if (value != null) {
//            if (type instanceof ParameterTypeBoolean) {
//                if (value.equals("true")) {
//                    seti18nKey("correct_parameter_settings_boolean_enable", parameterName);
//                } else {
//                    seti18nKey("correct_parameter_settings_boolean_disable", parameterName);
//                }
//            }
//        }
//    }
//
//    /**
//     * This constructor will build a quickfix that let's the user select an appropriate value for
//     * the given parameter.
//     */
//    public ParameterSettingQuickFix(Operator operator, String parameterName, String i18nKey, Object... i18nArgs) {
//        this(operator, parameterName, null, i18nKey, i18nArgs);
//    }
//
//    /**
//     * This constructor will build a quickfix that will automatically set the parameter to the given
//     * value without further user interaction. Use this constructor if you can comprehend the
//     * correct value.
//     */
//    public ParameterSettingQuickFix(Operator operator, String parameterName, String value, String i18nKey,
//                                    Object... i18nArgs) {
//        super(1, true, i18nKey, i18nArgs);
//        this.operator = operator;
//        this.parameterName = parameterName;
//        this.value = value;
//    }
//
//    @Override
//    public void apply() {
//        ParameterType type = operator.getParameterType(parameterName);
//        if (value != null) {
//            operator.setParameter(parameterName, value);
//        } else {
//            if (type instanceof ParameterTypeConfiguration) {
//                ParameterTypeConfiguration confType = (ParameterTypeConfiguration) type;
//            } else if (type instanceof ParameterTypeList) {
//                List<String[]> list;
//                try {
//                    list = operator.getParameterList(parameterName);
//                } catch (UndefinedParameterError e) {
//                    list = new LinkedList<String[]>();
//                }
//                operator.setListParameter(parameterName, list);
//            } else if (type instanceof ParameterTypeAttributes) {
//                String attributeListString = "";
//                operator.setParameter(parameterName, attributeListString);
//            }
//        }
//    }
//}
