package com.beiying.annotation;

public enum LayoutLevel {
    VERY_LOW(500), LOW(400), NORMAL(300),HIGHT(200),VERY_HIGHT(100);

    private int value;
    LayoutLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
