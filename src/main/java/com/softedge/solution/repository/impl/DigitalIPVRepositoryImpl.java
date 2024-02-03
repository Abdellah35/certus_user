package com.softedge.solution.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.Map;

@Repository
@Transactional
public class DigitalIPVRepositoryImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${update.digital.ipv.approval.process.sql}")
    private String digitalIpvVerificationSql;

    @Autowired
    public void setDataSource(@Qualifier("core-db") DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Long updateDigitalIPVDetails(Long userId, String verificationStatus) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {

        logger.info("Executing the query for update of digital ipv details -> {}", digitalIpvVerificationSql);
        Map<String, Object> fileParameters = new HashMap<>();
        fileParameters.put("userId", userId);
        fileParameters.put("verficationStatus", verificationStatus);
        int row = namedParameterJdbcTemplate.update(digitalIpvVerificationSql, fileParameters);
        return (long) row;

    }


}
