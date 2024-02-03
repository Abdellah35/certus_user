package com.softedge.solution.contractmodels;

import lombok.Data;

@Data
public class AdminDashboardUserCountSummaryCM {

    private Long registered;
    private Long ipvInProgress;
    private Long ipvAccepted;
    private Long ipvRejected;
    private Long ipvPending;
    private Long unregistered;

}
