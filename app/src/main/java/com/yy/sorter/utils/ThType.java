package com.yy.sorter.utils;

/**
 * Created by Administrator on 2017/5/3.
 */

public class ThType {

    private int lanIndex;
    private String name;
    private int type;
    private int pos;

    @Override
    public String toString() {
        return "ThType{" +
                "lanIndex='" + lanIndex + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", pos=" + pos +
                '}';
    }

    public ThType(int lanIndex, String name, int type, int pos) {
        this.lanIndex = lanIndex;
        this.name = name;
        this.type = type;
        this.pos = pos;
    }

    public int getLanIndex() {
        return lanIndex;
    }

    public void setLanIndex(int lanIndex) {
        this.lanIndex = lanIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
