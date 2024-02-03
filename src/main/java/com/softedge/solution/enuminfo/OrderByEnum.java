package com.softedge.solution.enuminfo;

public enum OrderByEnum implements EnumCode {

    EMAILID("EMAIL_ID"),
    NAME("NAME");


    private String name;

    OrderByEnum(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return this.name;
    }

    public String getEnumName() {
        return this.name();
    }
}
