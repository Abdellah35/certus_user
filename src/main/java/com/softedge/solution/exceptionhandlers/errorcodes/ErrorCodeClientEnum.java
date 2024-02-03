package com.softedge.solution.exceptionhandlers.errorcodes;


import com.softedge.solution.exceptionhandlers.ErrorCodeEnum;
import com.softedge.solution.exceptionhandlers.ServiceEnum;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ErrorCodeClientEnum implements ErrorCodeEnum {

    EMAIL_ID_MANDATORY("COMPLIC001", "Email ID is mandatory"),
    COMPANY_ID_MANDATORY("COMPLIC002", "Company Id is mandatory"),
    NAME_IS_MANDATORY("COMPLIC003", "Name is mandatory"),
    PASSWORD_IS_MANDATORY("COMPLIC004", "Password is mandatory"),
    INTERNAL_SERVER_ERROR("COMPLIC005", "Oops... Something went wrong, please try again later"),
    UNAUTHORIZED("COMPLIC100", "Un authorised to Perform this operation");

    private static final Map<String, ErrorCodeClientEnum> LOOKUP = new HashMap<>();
    private static ServiceEnum serviceEnum;
    private String errorCode;
    private String name;

    ErrorCodeClientEnum(String errorCode, String name) {
        this.errorCode = errorCode;
        this.name = name;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getName() {
        return this.name;
    }

    public int getServiceId() {
        return serviceEnum.getServiceId();
    }

    public String getMessageKey() {
        return this.name;
    }

    public String getDefaultMessage() {
        if (this.equals(errorCode)) {
            return getName();
        } else {
            return "We are not able to process your request at this time. Please contact the technical support";
        }
    }

    public static ErrorCodeClientEnum get(String errorCode) {
        return (ErrorCodeClientEnum) LOOKUP.get(errorCode);
    }

    public String getEnumName() {
        return this.name();
    }

    static {
        for (ErrorCodeClientEnum e : EnumSet.allOf(ErrorCodeClientEnum.class)) {
            LOOKUP.put(e.getErrorCode(), e);
        }
        serviceEnum = ServiceEnum.FILEGENERATOR;
    }
}
