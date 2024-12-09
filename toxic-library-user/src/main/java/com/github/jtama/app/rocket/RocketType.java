package com.github.jtama.app.rocket;

import com.github.jtama.toxic.FooBarUtils;

public enum RocketType {
    EXPLOSIVE("explosive"),
    FIRST_CLASS("first class"),
    LUXURY("luxury");

    private String luxury;

    RocketType(String luxury) {
        this.luxury = luxury;
    }

    public String getLuxury() {
        return luxury;
    }

    public int is(RocketType type) {
        return new FooBarUtils().compare(this, type, (o1, o2) -> o1.getLuxury().compareTo(o2.getLuxury()));
    }
}
