package com.softedge.solution.service.certusabstractservice;

import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import com.softedge.solution.service.CertusUserRegistrationService;
import com.softedge.solution.service.impl.CertusUserRegistrationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CertusAbstractUserRegistrationService implements CertusUserRegistrationService {
    private final Logger logger = LoggerFactory.getLogger(CertusUserRegistrationServiceImpl.class);

    protected UserAccountModuleException errorUserRegistrationService(String errorCode, Exception e) throws UserAccountModuleException {
        logger.error("Exception -> {},{}", e.getMessage(), e);
        return this.userRegistrationException(errorCode);
    }

    protected UserAccountModuleException errorUserRegistrationService(String errorCode) throws UserAccountModuleException {
        return this.userRegistrationException(errorCode);
    }


    private UserAccountModuleException userRegistrationException(String errorCode) throws UserAccountModuleException {
        switch (ErrorCodeUserEnum.get(errorCode)) {
            case USER_ID_EXISTS:
                return new UserAccountModuleException(ErrorCodeUserEnum.USER_ID_EXISTS, ErrorCodeUserEnum.USER_ID_EXISTS.getName());
            case USER_ACTIVATION_NULL:
                return new UserAccountModuleException(ErrorCodeUserEnum.USER_ACTIVATION_NULL, ErrorCodeUserEnum.USER_ACTIVATION_NULL.getName());
            case USER_ACTIVATION_OTP:
                return new UserAccountModuleException(ErrorCodeUserEnum.USER_ACTIVATION_OTP, ErrorCodeUserEnum.USER_ACTIVATION_OTP.getName());
            case USER_PASSWORD_EMPTY:
                return new UserAccountModuleException(ErrorCodeUserEnum.USER_PASSWORD_EMPTY, ErrorCodeUserEnum.USER_PASSWORD_EMPTY.getName());
            case USER_ID_EMPTY:
                return new UserAccountModuleException(ErrorCodeUserEnum.USER_ID_EMPTY, ErrorCodeUserEnum.USER_ID_EMPTY.getName());
            case EMAIL_ID_INVALID:
                return new UserAccountModuleException(ErrorCodeUserEnum.EMAIL_ID_INVALID, ErrorCodeUserEnum.EMAIL_ID_INVALID.getName());
            case PASSWORD_RULE_MISMATCH:
                return new UserAccountModuleException(ErrorCodeUserEnum.PASSWORD_RULE_MISMATCH, ErrorCodeUserEnum.PASSWORD_RULE_MISMATCH.getName());
            case PASSWORD_EMPTY:
                return new UserAccountModuleException(ErrorCodeUserEnum.PASSWORD_EMPTY, ErrorCodeUserEnum.PASSWORD_EMPTY.getName());
            case PASSWORD_RESET_FAILED:
                return new UserAccountModuleException(ErrorCodeUserEnum.PASSWORD_RESET_FAILED, ErrorCodeUserEnum.PASSWORD_RESET_FAILED.getName());
            default:
                return new UserAccountModuleException(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR, ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getName());

        }

    }
}
