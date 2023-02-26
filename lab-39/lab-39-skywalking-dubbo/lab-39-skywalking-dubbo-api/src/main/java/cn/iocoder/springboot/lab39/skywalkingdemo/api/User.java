package cn.iocoder.springboot.lab39.skywalkingdemo.api;


import java.io.Serializable;

public class User implements Serializable {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
