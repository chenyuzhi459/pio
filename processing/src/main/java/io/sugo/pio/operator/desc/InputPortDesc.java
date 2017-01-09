package io.sugo.pio.operator.desc;

public class InputPortDesc {
    public static CustomParameterDesc create(String name) {
        CustomParameterDesc desc = CustomParameterDesc.create(name, "InputPort");
        desc.addParameterDesc(ParameterDesc.createString("name", ""));
        desc.setPortType(CustomParameterDesc.CustomType.InputPort);
        return desc;
    }

    public static CustomParameterDesc create(String name, String defaultValue) {
        CustomParameterDesc desc = CustomParameterDesc.create(name, "InputPort");
        desc.setPortType(CustomParameterDesc.CustomType.InputPort);
        desc.addParameterDesc(ParameterDesc.create("name", "", ParamType.STRING, defaultValue));
        return desc;
    }
}
