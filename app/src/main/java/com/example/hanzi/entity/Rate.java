package com.example.hanzi.entity;

import com.google.gson.Gson;

/**
 * Created by 赵 on 2018/5/8.
 */

public class Rate {
    private int id;
    private String name;
    private int section;
    private int errorTimes;

    public Rate(String name, int section, int errorTimes) {
        this.name = name;
        this.section = section;
        this.errorTimes = errorTimes;
    }

    public Rate(String str){
        Gson gson = new Gson();
        gson.fromJson(str,Rate.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getErrorTime() {
        return errorTimes;
    }

    public void setErrorTime(int errorTime) {
        this.errorTimes = errorTime;
    }

    @Override
    public String toString() {
        return "name=" + name + "&section=" + section + "&errorTime=" + errorTimes;
    }
}
