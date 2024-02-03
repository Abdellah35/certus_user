package com.softedge.solution.service;

import com.softedge.solution.contractmodels.TempUserCM;
import com.softedge.solution.exceptionhandlers.custom.bean.TempUserException;
import com.softedge.solution.repomodels.TempUser;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface TempUserService {

    ResponseEntity<?> addTempUser(TempUserCM tempUserCM, HttpServletRequest request) throws TempUserException;

    TempUser getTempUserByEmailId(String emailId, HttpServletRequest request) throws TempUserException;

}
