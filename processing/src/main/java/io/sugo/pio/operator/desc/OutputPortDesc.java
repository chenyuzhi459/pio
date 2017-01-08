package io.sugo.pio.operator.desc;

public class OutputPortDesc {
    public static CustomParameterDesc create(String name) {
        CustomParameterDesc desc = CustomParameterDesc.create(name, "OutputPort");
        desc.addParameterDesc(ParameterDesc.createString("name", ""));
        return desc;
    }

    public static CustomParameterDesc create(String name, String defaultValue) {
        CustomParameterDesc desc = CustomParameterDesc.create(name, "OutputPort");
        desc.addParameterDesc(ParameterDesc.create("name", "", ParamType.STRING, defaultValue));
        return desc;
    }
}
