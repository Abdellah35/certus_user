package com.softedge.solution.service.certusabstractservice;

import com.softedge.solution.contractmodels.CompanyCM;
import com.softedge.solution.exceptionhandlers.BaseException;
import com.softedge.solution.exceptionhandlers.custom.company.CompanyServiceModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import com.softedge.solution.repomodels.Company;
import com.softedge.solution.service.impl.CertusUserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeCompanyEnum.*;

public abstract class CertusAbstractCompanyService<T extends BaseException>{

    protected final Logger logger = LoggerFactory.getLogger(CertusUserDetailsServiceImpl.class);

    protected Company companyValidationService(CompanyCM companyCM, Company company) throws CompanyServiceModuleException {

        if(companyCM.getWebsite()==null||companyCM.getWebsite().isEmpty()){
            throw this.errorCompanyService(COMPANY_URL_MANDATORY.getErrorCode());
        }
        else{
            company.setWebsite(companyCM.getWebsite());
        }


        if(companyCM.getCountry()==null||companyCM.getCountry().isEmpty()){
            throw this.errorCompanyService(COUNTRY_NAME_MANDATORY.getErrorCode());
        }


        if(companyCM.getCompanyName()==null||companyCM.getCompanyName().isEmpty()){
            throw this.errorCompanyService(COMPANY_NAME_MANDATORY.getErrorCode());
        }
        else{
            company.setCompanyName(companyCM.getCompanyName());
        }
        return company;
    }

    protected T errorCompanyService(String errorCode, Exception e) throws CompanyServiceModuleException {
        logger.error("Exception -> {},{}", e.getMessage(), e);
        return this.companyServiceException(errorCode);
    }

    protected T errorCompanyService(String errorCode) throws CompanyServiceModuleException {
        return this.companyServiceException(errorCode);
    }

    private T companyServiceException (String errorCode) throws CompanyServiceModuleException {
        if (errorCode != null == COMPANY_ID_INVALID.getErrorCode().equals(errorCode)) {
            return (T) new CompanyServiceModuleException(COMPANY_ID_INVALID, COMPANY_ID_INVALID.getName());
        } else if (errorCode != null == COMPANY_URL_MANDATORY.getErrorCode().equals(errorCode)) {
            return (T) new CompanyServiceModuleException(COMPANY_URL_MANDATORY, COMPANY_URL_MANDATORY.getName());
        }

        else if (errorCode != null == COMPANY_NAME_MANDATORY.getErrorCode().equals(errorCode)) {
            return (T) new CompanyServiceModuleException(COMPANY_NAME_MANDATORY, COMPANY_NAME_MANDATORY.getName());
        }
        return (T) new CompanyServiceModuleException(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR, ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getName());

    }




}
