package com.softedge.solution.service;

import com.softedge.solution.exceptionhandlers.custom.Utilities.FileStorageException;
import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.exceptionhandlers.custom.user.UserServiceModuleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface CertusDigitalIPVService {

    ResponseEntity<?> getIPVCode(HttpServletRequest request) throws UserServiceModuleException, UserAccountModuleException, FileStorageException;

    ResponseEntity<?> saveDigitalIPVImage(MultipartFile file, HttpServletRequest request) throws UserServiceModuleException, UserAccountModuleException, FileStorageException;

    ResponseEntity<?> getIPVinfo(HttpServletRequest request) throws UserServiceModuleException, UserAccountModuleException, FileStorageException;

    ResponseEntity<?> getIPVinfo(Long userId, HttpServletRequest request) throws UserServiceModuleException, UserAccountModuleException, FileStorageException;

    ResponseEntity<?> approvalDigitalIpv(HttpServletRequest request, Long userId, String verificationStatus) throws UserServiceModuleException, UserAccountModuleException, FileStorageException;


}
