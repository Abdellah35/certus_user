package com.softedge.solution.service;

import com.softedge.solution.contractmodels.UserCM;
import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.repomodels.UserRegistration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface CertusUserDetailsService {

    UserRegistration getUserByUsername(String username) throws UserAccountModuleException;

    UserRegistration addUser(UserRegistration userRegistration) throws UserAccountModuleException;

    ResponseEntity<?> saveUserDetails(UserCM userCM, HttpServletRequest request) throws Exception;

    ResponseEntity<?> getUserSessionInfo(HttpServletRequest request) throws UserAccountModuleException;

    ResponseEntity<?> getUsersSession(HttpServletRequest request) throws UserAccountModuleException;

    ResponseEntity<?> getCurrentUserDetails(HttpServletRequest request) throws UserAccountModuleException;

    ResponseEntity uploadFile(MultipartFile file, HttpServletRequest request);

    ResponseEntity<?> getCurrentUserDetails(HttpServletRequest request, Long userId) throws UserAccountModuleException;

}
