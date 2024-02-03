package com.softedge.solution.service.certusabstractservice;

import com.softedge.solution.contractmodels.UserAddressCM;
import com.softedge.solution.contractmodels.UserCM;
import com.softedge.solution.enuminfo.GenderEnum;
import com.softedge.solution.enuminfo.UserTypeEnum;
import com.softedge.solution.exceptionhandlers.BaseException;
import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import com.softedge.solution.repomodels.UserDetails;
import com.softedge.solution.repository.NotificationRepository;
import com.softedge.solution.repository.UserRepository;
import com.softedge.solution.repository.impl.UserDetailsRepositoryImpl;
import com.softedge.solution.security.util.SecurityUtils;
import com.softedge.solution.service.CertusUserDetailsService;
import com.softedge.solution.service.CountryService;
import com.softedge.solution.service.StateService;
import com.softedge.solution.service.impl.CertusUserDetailsServiceImpl;
import com.softedge.solution.service.impl.LocationServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Date;

import static com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum.*;

public abstract class CertusAbstractUserDetailsService<T extends BaseException> extends StorageAbstractService implements CertusUserDetailsService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CountryService countryService;

    @Autowired
    protected StateService stateService;

    @Autowired
    protected LocationServiceImpl locationServiceImpl;

    @Autowired
    protected SecurityUtils securityUtils;

    @Autowired
    protected UserDetailsRepositoryImpl userDetailsRepositoryImpl;

    @Autowired
    protected NotificationRepository notificationRepository;

    protected final Logger logger = LoggerFactory.getLogger(CertusUserDetailsServiceImpl.class);

    protected UserDetails userValidationService(UserCM userCM, UserDetails userDetails) throws UserAccountModuleException {

        if(!userCM.getUserType().equalsIgnoreCase(UserTypeEnum.Company.getValue())){

            //user gender
            if (userCM.getGender() != null) {
                if (userCM.getGender().equals(GenderEnum.FEMALE.getValue())) {
                    userDetails.setGender(GenderEnum.FEMALE.getValue());
                } else if (userCM.getGender().equals(GenderEnum.MALE.getValue())) {
                    userDetails.setGender(GenderEnum.MALE.getValue());
                } else if (userCM.getGender().equals(GenderEnum.OTHERS.getValue())) {
                    userDetails.setGender(GenderEnum.OTHERS.getValue());
                } else {
                    throw this.errorUserService(USER_GENDER_INVALID.getErrorCode());
                }
            } else {
                throw this.errorUserService(USER_GENDER_INVALID.getErrorCode());
            }
            //User dob
            if (userCM.getDob() != null) {
                if (userCM.getDob().getTime() < new Date().getTime()) {
                    userDetails.setDob(new Timestamp(userCM.getDob().getTime()));
                } else {
                    throw this.errorUserService(USER_DOB_INVALID.getErrorCode());
                }
            } else {
                throw this.errorUserService(USER_DOB_INVALID.getErrorCode());
            }

        }

//        user name
        if (userCM.getName() != null) {
            userDetails.setName(userCM.getName());
        } else {
            throw this.errorUserService(USER_NAME_MANDATORY.getErrorCode());
        }
//        user location
        if (userCM.getUserAddressCMList().size() > 0) {
            for (UserAddressCM addressCM : userCM.getUserAddressCMList()) {
                if (StringUtils.isEmpty(addressCM.getCountryCode())) {
                    throw this.errorUserService(USER_LOCATION_MANDATORY.getErrorCode());
                }
                if (addressCM.getState() == null) {
                    throw this.errorUserService(USER_LOCATION_MANDATORY.getErrorCode());
                }
                if (StringUtils.isEmpty(addressCM.getAddress())) {
                    throw this.errorUserService(USER_LOCATION_MANDATORY.getErrorCode());
                }
            }
        } else {
            throw this.errorUserService(USER_LOCATION_MANDATORY.getErrorCode());
        }

        return userDetails;

    }


    protected T errorUserService(String errorCode, Exception e) throws UserAccountModuleException {
        logger.error("Exception -> {},{}", e.getMessage(), e);
        return this.userServiceException(errorCode);
    }

    protected T errorUserService(String errorCode) throws UserAccountModuleException {
        return this.userServiceException(errorCode);
    }


    private T userServiceException(String errorCode) throws UserAccountModuleException {
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
        } else if (errorCode != null == UNAUTHORIZED.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(UNAUTHORIZED, UNAUTHORIZED.getName());
        } else if (errorCode != null == USER_DOB_INVALID.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(USER_DOB_INVALID, USER_DOB_INVALID.getName());
        }
        return (T) new UserAccountModuleException(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR, ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getName());

    }
}
