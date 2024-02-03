package com.softedge.solution.service.helper;

import com.softedge.solution.commons.AppConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CertusClientServiceHelper {

    @Value("${get.requested.client.user.sql}")
    private String clientRequestedUsersSql;

    @Value("${get.requested.client.user.count.sql}")
    private String clientRequestedUsersCountSql;

    public String getDynamicRequestedUserListSql(String search) {
        if (StringUtils.isNotEmpty(search)) {
            String queryString = AppConstants.SPACE_WHERE_SPACE + "(ut.email_id like '%" + search + "%'" + AppConstants.SPACE_OR_SPACE + "ut.name like '%" + search + "%' )";
            String finalSql = StringUtils.replace(clientRequestedUsersSql, "{0}", queryString);
            return finalSql;
        } else {
            String finalSql = StringUtils.replace(clientRequestedUsersSql, "{0}", AppConstants.SPACE);
            return finalSql;
        }
    }

    public String getDynamicRequestedUserCountSql(String search) {
        if (StringUtils.isNotEmpty(search)) {
            String queryString = AppConstants.SPACE_WHERE_SPACE + "(ut.email_id like '%" + search + "%'" + AppConstants.SPACE_OR_SPACE + "ut.name like '%" + search + "%' )";
            String finalSql = StringUtils.replace(clientRequestedUsersCountSql, "{0}", queryString);
            return finalSql;
        } else {
            String finalSql = StringUtils.replace(clientRequestedUsersCountSql, "{0}", AppConstants.SPACE);
            return finalSql;
        }
    }
}
