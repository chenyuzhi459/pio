package io.sugo.pio.ports;

import io.sugo.pio.operator.Operator;

/**
 */
public class PortOwner {
    private final Operator operator;
    public PortOwner(Operator operator){
        this.operator = operator;
    }
    public Operator getOperator(){
        return operator;
    }
}
