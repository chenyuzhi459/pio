package io.sugo.pio.operator.io.csv;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 17-1-5.
 */
public class Person  implements Serializable{
    @JsonProperty
    private String name;
    @JsonProperty
    private int age;
    @JsonProperty
    private List<Double> scores;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
    }
}
