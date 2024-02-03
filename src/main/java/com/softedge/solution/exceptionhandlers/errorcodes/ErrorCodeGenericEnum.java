package com.softedge.solution.exceptionhandlers.errorcodes;


import com.softedge.solution.exceptionhandlers.ErrorCodeEnum;
import com.softedge.solution.exceptionhandlers.ServiceEnum;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public enum ErrorCodeGenericEnum implements ErrorCodeEnum {

    EMPTY_DOCUMENTS("KYC001", "No documents found"),
    DOCUMENT_NOT_FOUNT("KYC002", "Document not found"),
    DATA_CONNECTION_LOST("KYC003", "Data connection lost... Try again later"),
    SQL_GRAMMER_ISSUE("KYC004", "Data Security issue, cannot process for safety reasons... Try again later"),
    DATA_INCORRECT("KYC005", "Data incorrect... Please try again later"),
    NOTIFICATION_FAILUER("KYC006", "Notification failed to save"),
    KYC_NOT_FOUND("KYC007", "Kyc document not found"),
    MESSAGE_IS_EMPTY("MSG008", "Message is empty"),
    MESSAGE_ID_NOT_FOUNT("MSG008", "Message id needed"),
    INTERNAL_SERVER_ERROR("USER099", "Oops... Something went wrong, please try again later"),

    UNAUTHORIZED("USER100", "UnAuthorized");

    private static final Map<String, ErrorCodeGenericEnum> LOOKUP = new HashMap();
    private static ServiceEnum serviceEnum;
    private String errorCode;
    private String name;

    ErrorCodeGenericEnum(String errorCode, String name) {
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
        return "We are not able to process your request at this time. Please contact the technical support";
    }

    public static ErrorCodeGenericEnum get(String errorCode) {
        return (ErrorCodeGenericEnum) LOOKUP.get(errorCode);
    }

    public String getEnumName() {
        return this.name();
    }

    static {
        Iterator var0 = EnumSet.allOf(ErrorCodeGenericEnum.class).iterator();

        while (var0.hasNext()) {
            ErrorCodeGenericEnum e = (ErrorCodeGenericEnum) var0.next();
            LOOKUP.put(e.getErrorCode(), e);
        }

        serviceEnum = ServiceEnum.FILEGENERATOR;
    }
}
