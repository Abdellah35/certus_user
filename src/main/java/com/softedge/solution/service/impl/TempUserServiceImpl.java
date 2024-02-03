package com.softedge.solution.service.impl;

import com.softedge.solution.contractmodels.TempUserCM;
import com.softedge.solution.exceptionhandlers.custom.GenericModuleException;
import com.softedge.solution.exceptionhandlers.custom.bean.TempUserException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeCompanyEnum;
import com.softedge.solution.repomodels.Client;
import com.softedge.solution.repomodels.TempUser;
import com.softedge.solution.repomodels.UserDetails;
import com.softedge.solution.repository.impl.ClientRepositoryImpl;
import com.softedge.solution.repository.impl.TempUserRepositoryImpl;
import com.softedge.solution.repository.impl.UserDetailsRepositoryImpl;
import com.softedge.solution.security.util.SecurityUtils;
import com.softedge.solution.service.TempUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class TempUserServiceImpl implements TempUserService {

    Logger logger = LoggerFactory.getLogger(TempUserServiceImpl.class);


    @Autowired
    TempUserRepositoryImpl tempUserRepository;

    @Autowired
    SecurityUtils securityUtils;

    @Autowired
    UserDetailsRepositoryImpl userDetailsRepositoryImpl;

    @Autowired
    ClientRepositoryImpl clientRepository;


    @Override
    public ResponseEntity<?> addTempUser(TempUserCM tempUserCM, HttpServletRequest request) throws GenericModuleException, TempUserException {
        Long tempUserId = 0L;
        String username = this.securityUtils.getUsernameFromToken(request);
        UserDetails user = userDetailsRepositoryImpl.getUserByUsername(username);
        String emailId = tempUserCM.getEmailId();
        UserDetails existingUser = userDetailsRepositoryImpl.getUserByUsername(emailId);
        if (existingUser == null) {

            //Check for the user in temp table
            TempUser tempUser = tempUserRepository.getTempUserByEmailId(tempUserCM.getEmailId());
            if (tempUser != null) {
                // send the existing temp user
                return new ResponseEntity<>(tempUser, HttpStatus.OK);
            } else {
                tempUser = new TempUser();
                tempUser.setEmailId(emailId);
                tempUser.setName(tempUserCM.getName());
                tempUser.setCreatedBy(user.getEmailId());
                tempUser.setCreatedAt(new Date());
                tempUser.setRegistered(false);
                tempUserId = tempUserRepository.saveTempUser(tempUser);
            }
            if (tempUserId > 0) {
                tempUserCM.setId(tempUserId);
                return new ResponseEntity<>(tempUserCM, HttpStatus.OK);
            } else {
                throw new GenericModuleException(ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR, ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR.getName());
            }

        } else {
            TempUser tempUser = new TempUser();
            tempUser.setId(existingUser.getId());
            tempUser.setEmailId(existingUser.getEmailId());
            tempUser.setName(existingUser.getName());
            tempUser.setCreatedBy(existingUser.getCreatedBy());
            tempUser.setCreatedAt(existingUser.getCreatedDate());
            tempUser.setRegistered(true);
            return new ResponseEntity<>(tempUser, HttpStatus.OK);
        }
    }

    @Override
    public TempUser getTempUserByEmailId(String emailId, HttpServletRequest request) throws TempUserException {
        try {
            String username = this.securityUtils.getUsernameFromToken(request);
            UserDetails user = userDetailsRepositoryImpl.getUserByUsername(username);
            return tempUserRepository.getTempUserByEmailId(emailId);
        } catch (Exception e) {
            throw new TempUserException(ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR, ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR.getName());
        }
    }
}
