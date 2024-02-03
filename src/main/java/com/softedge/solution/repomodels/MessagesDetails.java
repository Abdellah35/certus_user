package com.softedge.solution.repomodels;

import lombok.Data;

import java.util.Date;

@Data
public class MessagesDetails {

    private Long id;
    private Long requesteeUserId;
    private Long requestorUserId;
    private Long kycId;
    private String message;
    private Date lastUpdatedTimestamp;
    private String createdBy;
    private boolean markAsRead;
}
