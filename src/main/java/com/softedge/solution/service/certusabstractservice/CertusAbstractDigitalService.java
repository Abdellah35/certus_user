package com.softedge.solution.service.certusabstractservice;

import com.softedge.solution.contractmodels.DigitalIPVCM;
import com.softedge.solution.exceptionhandlers.BaseException;
import com.softedge.solution.exceptionhandlers.custom.Utilities.FileStorageException;
import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.exceptionhandlers.custom.user.UserServiceModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import com.softedge.solution.repomodels.DigitalIPV;
import com.softedge.solution.repomodels.UserRegistration;
import com.softedge.solution.repository.DigitalIPVRepository;
import com.softedge.solution.repository.UserRepository;
import com.softedge.solution.security.util.SecurityUtils;
import com.softedge.solution.service.CertusDigitalIPVService;
import com.softedge.solution.service.impl.CertusUserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUtilityEnum.*;

public abstract class CertusAbstractDigitalService<T extends BaseException> extends StorageAbstractService implements CertusDigitalIPVService {
    private final Logger logger = LoggerFactory.getLogger(CertusUserDetailsServiceImpl.class);

    @Autowired
    protected DigitalIPVRepository digitalIPVRepository;

    @Autowired
    protected UserRepository userRepository;


    @Autowired
    protected SecurityUtils securityUtils;


    @Autowired
    protected Environment env;

    protected T errorDigitalIPVService(String errorCode, Exception e) throws UserAccountModuleException, UserServiceModuleException, FileStorageException {
        logger.error("Exception -> {},{}", e.getMessage(), e);
        return this.digitalIPVServiceException(errorCode);
    }

    protected T errorDigitalIPVService(String errorCode) throws UserAccountModuleException, UserServiceModuleException, FileStorageException {
        return this.digitalIPVServiceException(errorCode);
    }


    private T digitalIPVServiceException(String errorCode) throws UserAccountModuleException, UserServiceModuleException, FileStorageException {
        if (errorCode != null == FILE_NOT_FOUND.getErrorCode().equals(errorCode)) {
            return (T) new FileStorageException(FILE_NOT_FOUND, FILE_NOT_FOUND.getName());
        } else if (errorCode != null == FILE_INVALID_PATH_SEQUENCE.getErrorCode().equals(errorCode)) {
            return (T) new FileStorageException(FILE_INVALID_PATH_SEQUENCE, FILE_INVALID_PATH_SEQUENCE.getName());
        } else if (errorCode != null == FILE_STORE_FAILED.getErrorCode().equals(errorCode)) {
            return (T) new FileStorageException(FILE_STORE_FAILED, FILE_STORE_FAILED.getName());
        } else if (errorCode != null == ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode().equals(errorCode)) {
            return (T) new UserAccountModuleException(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR, ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getName());
        }
        return (T) new UserAccountModuleException(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR, ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getName());

    }

    @Override
    public ResponseEntity<?> getIPVinfo(HttpServletRequest request) throws UserServiceModuleException, FileStorageException {
        DigitalIPVCM digitalIPVCM = new DigitalIPVCM();
        try {
//            TODO: REMOVE THE HARD CODE
            String username = securityUtils.getUsernameFromToken(request);
            UserRegistration userRegistration = userRepository.findByUsername(username);
            DigitalIPV digitalIPV = digitalIPVRepository.findByUser(userRegistration);
            digitalIPVCM.setAuditVerifiedTime(digitalIPV.getAuditVerifiedTime());
            digitalIPVCM.setIpvCode(digitalIPV.getIpvCode());
            digitalIPVCM.setUserIPVImage(digitalIPV.getUserIPVImage());
            digitalIPVCM.setUserRequestModifiedTime(digitalIPV.getUserRequestModifiedTime());
            digitalIPVCM.setUserRequestRaisedTime(digitalIPV.getUserRequestRaisedTime());
            digitalIPVCM.setVerificationStatus(digitalIPV.getVerificationStatus());
            return new ResponseEntity<>(digitalIPVCM, HttpStatus.OK);
        } catch (Exception e) {
            throw (UserServiceModuleException) this.errorDigitalIPVService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

}
