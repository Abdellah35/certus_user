package com.softedge.solution.contractmodels;

import lombok.Data;

@Data
public class KycRequestedUserCM {

    private Long id;
    private String name;
    private String emailId;
    private boolean registered;
    private String kycStatus;
    private String userType;
}
