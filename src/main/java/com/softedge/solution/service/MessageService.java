package com.softedge.solution.service;

import com.softedge.solution.contractmodels.PlainMessageCM;
import com.softedge.solution.exceptionhandlers.custom.GenericModuleException;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface MessageService {

    ResponseEntity<?> saveMessages(Long kycId, PlainMessageCM plainMessageCM, HttpServletRequest request) throws GenericModuleException;

    ResponseEntity<?> getMessages(Long kycId, HttpServletRequest request) throws GenericModuleException;

    ResponseEntity<?> deleteMessage(Long messageId, HttpServletRequest request) throws GenericModuleException;
}
