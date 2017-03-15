package io.sugo.pio.server.http.dto;

import java.io.Serializable;

/**
 */
public class OperatorMetadataDto implements Serializable {

    /**
     * The attribute name
     */
    private String attrName;

    /**
     * The attribute value type
     *
     * @see io.sugo.pio.tools.Ontology
     */
    private Integer attrType;

    /**
     * The attribute role
     *
     * @see io.sugo.pio.example.Attributes
     */
    private String role;

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public Integer getAttrType() {
        return attrType;
    }

    public void setAttrType(Integer attrType) {
        this.attrType = attrType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return attrName + ":" + attrType + ":" + role;
    }
}
