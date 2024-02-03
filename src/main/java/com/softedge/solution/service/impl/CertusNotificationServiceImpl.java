package com.softedge.solution.service.impl;

import com.softedge.solution.contractmodels.MapperObjectCM;
import com.softedge.solution.contractmodels.NotificationCM;
import com.softedge.solution.repomodels.NotificationDetails;
import com.softedge.solution.repomodels.UserRegistration;
import com.softedge.solution.repository.NotificationRepository;
import com.softedge.solution.repository.UserRepository;
import com.softedge.solution.security.util.SecurityUtils;
import com.softedge.solution.service.CertusNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CertusNotificationServiceImpl implements CertusNotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    SecurityUtils securityUtils;

    @Autowired
    protected UserRepository userRepository;

    @Override
    public ResponseEntity<?> getNotification(HttpServletRequest request) {
        String username = this.securityUtils.getUsernameFromToken(request);
        UserRegistration userRegistration = userRepository.findByUsername(username);
        List<NotificationDetails> notificationDetailsList = new ArrayList<>();
        if(userRegistration.getCategory().equalsIgnoreCase("client")) {
            notificationDetailsList = notificationRepository.getNotificationsByRequestor(userRegistration.getId());
        }else if(userRegistration.getCategory().equalsIgnoreCase("user")){
            notificationDetailsList = notificationRepository.getNotificationsByRequestee(userRegistration.getId());
        }
        List<NotificationCM> notificationCMS = new ArrayList<>();
        if(notificationDetailsList.size()>0){
            for(NotificationDetails notificationDetails : notificationDetailsList) {
                NotificationCM notificationCM = new NotificationCM();
                MapperObjectCM mapperObjectCM = new MapperObjectCM();
                Map<String, Long> map = new HashMap<>();
                map.put("companyId", notificationDetails.getCompanyId());
                mapperObjectCM.setMapper(map);
                notificationCM.setMapperObjectCM(mapperObjectCM);
                notificationCM.setCreatedAt(notificationDetails.getCreatedAt());
                notificationCM.setId(notificationDetails.getId());
                notificationCM.setMessage(notificationDetails.getMessage());
                notificationCM.setModule(notificationDetails.getModule());
                notificationCM.setNativeMessage(notificationDetails.getNativeMessage());
                notificationCMS.add(notificationCM);
            }
        }
        return new ResponseEntity<>(notificationCMS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> markNotificationAsRead(Long notificationId){
        Long status = notificationRepository.updateMarkedAsReadNotification(notificationId);
        MapperObjectCM mapperObjectCM = new MapperObjectCM();
        Map<String, String> map = new HashMap();
        if (status > 0) {
            map.put("status", "Success");
            mapperObjectCM.setMapper(map);
            return new ResponseEntity<>(mapperObjectCM, HttpStatus.OK);
        } else {
            map.put("status", "Failure");
            mapperObjectCM.setMapper(map);
            return new ResponseEntity<>(mapperObjectCM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> markAllNotificationAsRead(HttpServletRequest request) {
        String username = this.securityUtils.getUsernameFromToken(request);
        UserRegistration userRegistration = userRepository.findByUsername(username);
        Long status = 0l;
        if (userRegistration.getCategory().equalsIgnoreCase("client")) {
            status = notificationRepository.updateMarkedAsReadRequestorNotification(userRegistration.getId());
        } else if (userRegistration.getCategory().equalsIgnoreCase("user")) {
            status = notificationRepository.updateMarkedAsReadRequesteeNotification(userRegistration.getId());
        }
        MapperObjectCM mapperObjectCM = new MapperObjectCM();
        Map<String, String> map = new HashMap();
        if (status > 0) {
            map.put("status", "Success");
            mapperObjectCM.setMapper(map);
            return new ResponseEntity<>(mapperObjectCM, HttpStatus.OK);
        } else {
            map.put("status", "Failure");
            mapperObjectCM.setMapper(map);
            return new ResponseEntity<>(mapperObjectCM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getNotificationCount(HttpServletRequest request) {
        String username = this.securityUtils.getUsernameFromToken(request);
        UserRegistration userRegistration = userRepository.findByUsername(username);
        Long count = null;
        if(userRegistration.getCategory().equalsIgnoreCase("client")) {
            count = notificationRepository.getNotificationsCountForRequestor(userRegistration.getId());
        }else if(userRegistration.getCategory().equalsIgnoreCase("user")){
            count = notificationRepository.getNotificationsCountForRequestee(userRegistration.getId());
        }
        Map<String, Long> map = new HashMap<>();
        map.put("count", count);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
