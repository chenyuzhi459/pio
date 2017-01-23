package io.sugo.pio.server.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import io.sugo.pio.operator.OperatorGroup;

public class OperatorMeta {
    private String name;
    private String fullName;
    private String description;
    private NamedType type;
    private OperatorGroup group;
    private int xPos;
    private int yPos;

    public OperatorMeta(String name) {
        this.name = name;
    }

    public static OperatorMeta create(String name) {
        return new OperatorMeta(name);
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public String getFullName() {
        return fullName;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    public NamedType getType() {
        return type;
    }

    public OperatorMeta setName(String name) {
        this.name = name;
        return this;
    }

    public OperatorMeta setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public OperatorMeta setDescription(String description) {
        this.description = description;
        return this;
    }

    public OperatorMeta setType(NamedType type) {
        this.type = type;
        return this;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    @JsonProperty
    public OperatorGroup getGroup() {
        return group;
    }

    public OperatorMeta setGroup(OperatorGroup group) {
        this.group = group;
        return this;
    }
}