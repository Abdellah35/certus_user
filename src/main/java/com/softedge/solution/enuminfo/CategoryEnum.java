package com.softedge.solution.enuminfo;

public enum CategoryEnum implements EnumCode {
    CLIENT("CLIENT"),
    USER("USER"),
    ADMIN("ADMIN");

    private String name;

    CategoryEnum(String name) {
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
