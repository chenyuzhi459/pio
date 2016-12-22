package io.sugo.pio.dl4j.io;

import io.sugo.pio.operator.ResultObjectAdapter;

/**
 */
public class LayerSemaphore extends ResultObjectAdapter {
    private String name = "";

    public LayerSemaphore(){
        this("");
    };

    public LayerSemaphore(String name){
        this.name = name;
    }

    public boolean equals(Object o){
        boolean b = false;
        if (o.getClass().equals(this.getClass())){
            b = true;
        }
        return b;
    }

    public String toString(){
        return "Semaphore " + name;
    }
}
