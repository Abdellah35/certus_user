package com.softedge.solution.service.certusabstractservice;

import com.softedge.solution.exceptionhandlers.BaseException;
import com.softedge.solution.exceptionhandlers.custom.GenericModuleException;
import com.softedge.solution.exceptionhandlers.custom.Utilities.FileStorageException;
import com.softedge.solution.exceptionhandlers.custom.company.CompanyServiceModuleException;
import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeGenericEnum;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeGenericEnum.*;
import static com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum.*;

public class CerterGenericErrorHandlingService<T extends BaseException>  {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public T errorService(String errorCode, Exception e) throws UserAccountModuleException, FileStorageException, GenericModuleException {
        logger.error("Exception -> {},{}", e.getMessage(), e);
        return this.serviceException(errorCode);
    }

    public T errorService(String errorCode) throws UserAccountModuleException, FileStorageException, GenericModuleException {
        return this.serviceException(errorCode);
    }


    private T serviceException(String errorCode) throws UserAccountModuleException, FileStorageException, GenericModuleException {
        if (errorCode != null == USER_ID_EXISTS.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(USER_ID_EXISTS, USER_ID_EXISTS.getName());
        } else if (errorCode != null == USER_ACTIVATION_NULL.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(USER_ACTIVATION_NULL, USER_ACTIVATION_NULL.getName());
        } else if (errorCode != null == USER_ACTIVATION_OTP.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(USER_ACTIVATION_OTP, USER_ACTIVATION_OTP.getName());
        } else if (errorCode != null == ErrorCodeUserEnum.USER_PASSWORD_EMPTY.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(USER_PASSWORD_EMPTY, ErrorCodeUserEnum.USER_PASSWORD_EMPTY.getName());
        } else if (errorCode != null == USER_ID_EMPTY.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(USER_ID_EMPTY, USER_ID_EMPTY.getName());
        } else if (errorCode != null == USER_GENDER_INVALID.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(USER_GENDER_INVALID, USER_GENDER_INVALID.getName());
        } else if (errorCode != null == USER_DOB_INVALID.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(USER_DOB_INVALID, USER_DOB_INVALID.getName());
        } else if (errorCode != null == EMPTY_DOCUMENTS.getErrorCode().equals(errorCode)) {
            return (T) new GenericModuleException(EMPTY_DOCUMENTS, EMPTY_DOCUMENTS.getName());
        } else if (errorCode != null == DOCUMENT_NOT_FOUNT.getErrorCode().equals(errorCode)) {
            return (T) new GenericModuleException(DOCUMENT_NOT_FOUNT, DOCUMENT_NOT_FOUNT.getName());
        } else if (errorCode != null ==DATA_CONNECTION_LOST.getErrorCode().equals(errorCode)) {
            return (T) new GenericModuleException(DATA_CONNECTION_LOST, DATA_CONNECTION_LOST.getName());
        } else if (errorCode != null == SQL_GRAMMER_ISSUE.getErrorCode().equals(errorCode)) {
            return (T) new GenericModuleException(SQL_GRAMMER_ISSUE, SQL_GRAMMER_ISSUE.getName());
        } else if (errorCode != null == DATA_INCORRECT.getErrorCode().equals(errorCode)) {
            return (T) new GenericModuleException(DATA_INCORRECT, DATA_INCORRECT.getName());
        } else if (errorCode != null == KYC_NOT_FOUND.getErrorCode().equals(errorCode)) {
            return (T) new GenericModuleException(KYC_NOT_FOUND, KYC_NOT_FOUND.getName());
        } else if (errorCode != null == MESSAGE_IS_EMPTY.getErrorCode().equals(errorCode)) {
            return (T) new GenericModuleException(MESSAGE_IS_EMPTY, MESSAGE_IS_EMPTY.getName());
        }else if (errorCode != null == COMPANY_ID_INVALID.getErrorCode().equals(errorCode)) {
            return (T) new CompanyServiceModuleException(COMPANY_ID_INVALID, COMPANY_ID_INVALID.getName());
        }else if (errorCode != null == INVALID_FORM.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(INVALID_FORM, INVALID_FORM.getName());
        }else if (errorCode != null == INVALID_FORM.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(INVALID_FORM, INVALID_FORM.getName());
        } else if (errorCode != null == ErrorCodeGenericEnum.UNAUTHORIZED.getErrorCode().equals(errorCode)) {
            return (T) new GenericModuleException(ErrorCodeGenericEnum.UNAUTHORIZED, ErrorCodeGenericEnum.UNAUTHORIZED.getName());
        }
        return (T) new UserAccountModuleException(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR, ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getName());

    }
}
