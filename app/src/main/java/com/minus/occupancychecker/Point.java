package com.minus.occupancychecker;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Point {
    @JsonProperty
    int x;
    @JsonProperty
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
