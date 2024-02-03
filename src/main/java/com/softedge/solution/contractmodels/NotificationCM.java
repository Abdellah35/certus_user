package com.softedge.solution.contractmodels;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationCM {

    private Long id;
    private MapperObjectCM mapperObjectCM;
    private Date createdAt;
    private String message;
    private String nativeMessage;
    private String module;


}

