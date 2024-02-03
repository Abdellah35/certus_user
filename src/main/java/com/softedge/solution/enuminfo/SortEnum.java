package com.softedge.solution.enuminfo;

public enum SortEnum implements EnumCode {

    ASCENDING("ASC"),
    DESCENDING("DESC");


    private String name;

    SortEnum(String name) {
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