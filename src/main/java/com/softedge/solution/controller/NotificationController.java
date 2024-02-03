package com.softedge.solution.controller;

import com.softedge.solution.service.CertusNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notification")
public class NotificationController extends BaseController {

    @Autowired
    private CertusNotificationService certusNotificationService;

    @GetMapping("/get-notification")
    public ResponseEntity<?> getUserNotifications(HttpServletRequest request){
        return certusNotificationService.getNotification(request);
    }

    @PutMapping("/{notification-id}/mark-as-read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable("notification-id") Long notificationId){
        return certusNotificationService.markNotificationAsRead(notificationId);
    }

    @PutMapping("/mark-all-as-read")
    public ResponseEntity<?> markAllNotificationAsRead(HttpServletRequest request){
        return certusNotificationService.markAllNotificationAsRead(request);
    }

    @GetMapping("/count")
    public ResponseEntity<?> notificationCount(HttpServletRequest request){
        return certusNotificationService.getNotificationCount(request);
    }

}
