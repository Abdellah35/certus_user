package com.softedge.solution.service.certusabstractservice;

import com.softedge.solution.contractmodels.ClientCM;
import com.softedge.solution.exceptionhandlers.BaseException;
import com.softedge.solution.exceptionhandlers.client.ClientServiceModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeClientEnum;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import com.softedge.solution.repomodels.Client;
import com.softedge.solution.service.impl.CertusUserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CertusAbstractClientService<T extends BaseException>{

    protected final Logger logger = LoggerFactory.getLogger(CertusUserDetailsServiceImpl.class);

    protected Client clientValidationService(ClientCM clientCM, Client client) throws ClientServiceModuleException {

        if(clientCM.getEmailId()==null||clientCM.getEmailId().isEmpty()){
            throw this.errorClientService(ErrorCodeClientEnum.EMAIL_ID_MANDATORY.getErrorCode());
        }

        if(clientCM.getCompanyId()==null||clientCM.getCompanyId().toString().isEmpty()){
            throw this.errorClientService(ErrorCodeClientEnum.COMPANY_ID_MANDATORY.getErrorCode());
        }else{
            client.setCompanyId(clientCM.getCompanyId());
        }

        if(clientCM.getName()==null||clientCM.getName().isEmpty()){
            throw this.errorClientService(ErrorCodeClientEnum.NAME_IS_MANDATORY.getErrorCode());
        }
        if(clientCM.getPassword()==null||clientCM.getPassword().isEmpty()){
            throw this.errorClientService(ErrorCodeClientEnum.PASSWORD_IS_MANDATORY.getErrorCode());
        }

        return client;
    }

    protected T errorClientService(String errorCode, Exception e) throws ClientServiceModuleException {
        logger.error("Exception -> {},{}", e.getMessage(), e);
        return this.clientServiceException(errorCode);
    }

    protected T errorClientService(String errorCode) throws ClientServiceModuleException {
        return this.clientServiceException(errorCode);
    }

    private T clientServiceException (String errorCode) throws ClientServiceModuleException {
        if (errorCode != null == ErrorCodeClientEnum.EMAIL_ID_MANDATORY.getErrorCode().equals(errorCode)) {
            return (T) new ClientServiceModuleException(ErrorCodeClientEnum.EMAIL_ID_MANDATORY, ErrorCodeClientEnum.EMAIL_ID_MANDATORY.getName());
        }
        if (errorCode != null == ErrorCodeClientEnum.COMPANY_ID_MANDATORY.getErrorCode().equals(errorCode)) {
            return (T) new ClientServiceModuleException(ErrorCodeClientEnum.COMPANY_ID_MANDATORY, ErrorCodeClientEnum.COMPANY_ID_MANDATORY.getName());
        }
        if (errorCode != null == ErrorCodeClientEnum.NAME_IS_MANDATORY.getErrorCode().equals(errorCode)) {
            return (T) new ClientServiceModuleException(ErrorCodeClientEnum.NAME_IS_MANDATORY, ErrorCodeClientEnum.NAME_IS_MANDATORY.getName());
        }
        if (errorCode != null == ErrorCodeClientEnum.PASSWORD_IS_MANDATORY.getErrorCode().equals(errorCode)) {
            return (T) new ClientServiceModuleException(ErrorCodeClientEnum.PASSWORD_IS_MANDATORY, ErrorCodeClientEnum.PASSWORD_IS_MANDATORY.getName());
        }
        if (errorCode != null == ErrorCodeClientEnum.UNAUTHORIZED.getErrorCode().equals(errorCode)) {
            return (T) new ClientServiceModuleException(ErrorCodeClientEnum.UNAUTHORIZED, ErrorCodeClientEnum.UNAUTHORIZED.getName());
        }
        return (T) new ClientServiceModuleException(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR, ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getName());

    }




}
