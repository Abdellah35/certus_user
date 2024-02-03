package com.softedge.solution.repository.impl;

import com.softedge.solution.repomodels.NotificationDetails;
import com.softedge.solution.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${get.requestee.notification}")
    private String requesteeNotificationSql;

    @Value("${get.requestor.notification}")
    private String requestorNotificationSql;

    @Value("${get.requestor.notification.count.sql}")
    private String requestorNotificationCountSql;

    @Value("${get.requestee.notification.count.sql}")
    private String requesteeNotificationCountSql;

    @Value("${update.notification}")
    private String updateMarkAsReadNotificationSql;

    @Value("${update.notification.requestor}")
    private String updateMarkedAsReadRequestorNotification;

    @Value("${update.notification.requestee}")
    private String updateMarkedAsReadRequesteeNotification;

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(@Qualifier("core-db") DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<NotificationDetails> getNotificationsByRequestee(Long userId){
        List<NotificationDetails> notificationDetails = new ArrayList<>();
        try {

            logger.info("Executing the query for get all notification  {}", requesteeNotificationSql);

            Map<String,Long> parameters = new HashMap<>();
            parameters.put("userId", userId);

            notificationDetails = namedParameterJdbcTemplate.query(requesteeNotificationSql, parameters, new RowMapper<NotificationDetails>() {
                @Override
                public NotificationDetails mapRow(ResultSet resultSet, int i) throws SQLException {
                    NotificationDetails notiDetails = new NotificationDetails();
                    notiDetails.setId(resultSet.getLong("id"));
                    notiDetails.setNativeMessage(resultSet.getString("native_message"));
                    notiDetails.setCompanyId(resultSet.getLong("company_id"));
                    notiDetails.setCreatedAt(resultSet.getDate("created_at"));
                    notiDetails.setMessage(resultSet.getString("message"));
                    notiDetails.setNativeMessage(resultSet.getString("native_message"));
                    notiDetails.setModule(resultSet.getString("module"));
                    return notiDetails;
                }
            });
            return notificationDetails;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return notificationDetails;
        }
    }

    @Override
    public List<NotificationDetails> getNotificationsByRequestor(Long userId){
        List<NotificationDetails> notificationDetails = new ArrayList<>();
        try {

            logger.info("Executing the query for get all notification  {}", requestorNotificationSql);

            Map<String,Long> parameters = new HashMap<>();
            parameters.put("userId", userId);

            notificationDetails = namedParameterJdbcTemplate.query(requestorNotificationSql, parameters, new RowMapper<NotificationDetails>() {
                @Override
                public NotificationDetails mapRow(ResultSet resultSet, int i) throws SQLException {
                    NotificationDetails notiDetails = new NotificationDetails();
                    notiDetails.setId(resultSet.getLong("id"));
                    notiDetails.setNativeMessage(resultSet.getString("native_message"));
                    notiDetails.setCompanyId(resultSet.getLong("company_id"));
                    notiDetails.setCreatedAt(resultSet.getDate("created_at"));
                    notiDetails.setMessage(resultSet.getString("message"));
                    notiDetails.setNativeMessage(resultSet.getString("native_message"));
                    notiDetails.setModule(resultSet.getString("module"));
                    return notiDetails;
                }
            });
            return notificationDetails;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return notificationDetails;
        }
    }


    @Override
    public Long getNotificationsCountForRequestor(Long userId){
        Long count=0l;
        try {
            logger.info("Executing the query for get all notification  {}", requestorNotificationCountSql);
            Map<String,Long> parameters = new HashMap<>();
            parameters.put("userId", userId);

            count = namedParameterJdbcTemplate.queryForObject(requestorNotificationCountSql, parameters,Long.class);

            return count;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return count;
        }
    }

    @Override
    public Long getNotificationsCountForRequestee(Long userId){
        Long count=0l;
        try {
            logger.info("Executing the query for get all notification  {}", requesteeNotificationCountSql);
            Map<String,Long> parameters = new HashMap<>();
            parameters.put("userId", userId);

            count = namedParameterJdbcTemplate.queryForObject(requesteeNotificationCountSql, parameters,Long.class);

            return count;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return count;
        }
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public Long updateMarkedAsReadNotification(Long id)  {
        try {
            Map<String, Long> parameters = new HashMap<>();
            parameters.put("id", id);
            logger.info("Executing the query for update of update notification -> {}", updateMarkAsReadNotificationSql);
            int row = namedParameterJdbcTemplate.update(updateMarkAsReadNotificationSql, parameters);
            return (long) row;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return (long) 0;
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Long updateMarkedAsReadRequestorNotification(Long requestorUserId)  {
        try {
            Map<String, Long> parameters = new HashMap<>();
            parameters.put("id", requestorUserId);
            logger.info("Executing the query for update of update notification -> {}", updateMarkedAsReadRequestorNotification);
            int row = namedParameterJdbcTemplate.update(updateMarkedAsReadRequestorNotification, parameters);
            return (long) row;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return (long) 0;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long updateMarkedAsReadRequesteeNotification(Long requesteeUserId)  {
        try {
            Map<String, Long> parameters = new HashMap<>();
            parameters.put("id", requesteeUserId);
            logger.info("Executing the query for update of update notification -> {}", updateMarkedAsReadRequesteeNotification);
            int row = namedParameterJdbcTemplate.update(updateMarkedAsReadRequesteeNotification, parameters);
            return (long) row;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return (long) 0;
        }
    }

}
