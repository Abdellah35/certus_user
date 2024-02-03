package com.softedge.solution.service;

import com.softedge.solution.contractmodels.ClientCM;
import com.softedge.solution.exceptionhandlers.client.ClientServiceModuleException;
import com.softedge.solution.exceptionhandlers.custom.bean.ClientException;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CertusClientService {


    ResponseEntity<?> addUser(ClientCM clientCM) throws ClientException;

    ResponseEntity<?> getUserClient(Long id, HttpServletRequest request) throws ClientException;

    ResponseEntity<?> updateUserClient(ClientCM clientCM, HttpServletRequest request) throws ClientException;

    ResponseEntity<?> deleteUserClient(Long id, HttpServletRequest request) throws ClientException;

    ResponseEntity<?> getAllClients(HttpServletRequest request) throws ClientException;

    ResponseEntity<?> getClientRequestedUsers(HttpServletRequest request, String search, int page, int results, String orderBy, String sort) throws ClientServiceModuleException;

    ResponseEntity<?> getClientRequestedUsersCount(HttpServletRequest request, String search) throws ClientServiceModuleException;

    ResponseEntity<?> getClientInfo(HttpServletRequest request) throws ClientServiceModuleException;

    ResponseEntity<?> updateClientInfo(HttpServletRequest request, ClientCM client) throws ClientServiceModuleException;

    List<String[]> generateUserDataCSV(List<String> usernames);

    ResponseEntity<?> getClientDashboardUsersCount(HttpServletRequest request) throws ClientServiceModuleException;

    ResponseEntity<?> getAdminDashboardUsersCount(HttpServletRequest request) throws ClientServiceModuleException;
}
