package com.softedge.solution.contractmodels;

import lombok.Data;

import java.util.Date;

@Data
public class MessageDetailsCM {

    private Long id;
    private String name;
    private String emailId;
    private Date timestamp;
    private String message;
}
