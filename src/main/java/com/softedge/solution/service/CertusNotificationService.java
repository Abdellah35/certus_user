package com.softedge.solution.service;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface CertusNotificationService {

    ResponseEntity<?> getNotification(HttpServletRequest request);

    ResponseEntity<?> markNotificationAsRead(Long notificationId);

    ResponseEntity<?> markAllNotificationAsRead(HttpServletRequest request);

    ResponseEntity<?> getNotificationCount(HttpServletRequest request);
}
