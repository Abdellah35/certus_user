package com.softedge.solution.enuminfo;

public enum UserTypeEnum implements EnumCode {
    Individual("Individual"),
    Company("Company");

    private String name;

    UserTypeEnum(String name) {
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
