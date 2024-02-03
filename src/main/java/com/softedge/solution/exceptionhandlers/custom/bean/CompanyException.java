package com.softedge.solution.exceptionhandlers.custom.bean;

import com.softedge.solution.exceptionhandlers.BaseException;
import com.softedge.solution.exceptionhandlers.ErrorCodeEnum;

import java.util.Map;

public class CompanyException extends BaseException {

    private static final long serialVersionUID = 88256725033191L;

    public CompanyException(ErrorCodeEnum errorCode) {
        this.setErrorCode(errorCode);
        this.setDebugMessage(errorCode.getName());
    }

    public CompanyException(ErrorCodeEnum errorCode, String debugMessage) {
        super(debugMessage);
        this.setErrorCode(errorCode);
        this.setDebugMessage(debugMessage);
    }

    public CompanyException(ErrorCodeEnum errorCode, String debugMessage, Throwable originalException) {
        super(originalException);
        this.setErrorCode(errorCode);
        this.setDebugMessage(debugMessage);
    }

    public CompanyException(ErrorCodeEnum errorCode, String debugMessage, Map<String, String> messageArgs) {
        this.setErrorCode(errorCode);
        this.setDebugMessage(debugMessage);
        this.messageArgs = messageArgs;
    }
}
