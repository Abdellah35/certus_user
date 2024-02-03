package com.softedge.solution.contractmodels;

import lombok.Data;

@Data
public class ClientDashboardUserCountSummaryCM {

    private Long registered;
    private Long ipvCompleted;
    private Long unregistered;

}
