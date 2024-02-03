package com.softedge.solution.repository.impl;

import com.softedge.solution.contractmodels.KycProcessDocumentDetailsCM;
import com.softedge.solution.exceptionhandlers.GenericExceptionHandler;
import com.softedge.solution.exceptionhandlers.custom.GenericModuleException;
import com.softedge.solution.repomodels.KycDocumentDetails;
import com.softedge.solution.repository.KycRepository;
import com.softedge.solution.repository.rowmapper.KycProcessDocumentDetailsCMRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class KycRepositoryImpl implements KycRepository {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${update.kyc.user.registereduser.sql}")
    private String updateUserKycRegistereduserByClientSql;

    @Autowired
    public void setDataSource(@Qualifier("kyc-db") DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    @Transactional
    public KycProcessDocumentDetailsCM getKycDocumentDetailsById(Long id) throws GenericModuleException {
        try {
            String sql = "select * from kyc_document_details_view where id =:id";
            Map<String, Long> parameter = new HashMap<>();
            parameter.put("id", id);

            return namedParameterJdbcTemplate.queryForObject(sql, parameter, new KycProcessDocumentDetailsCMRowMapper());
        } catch (Exception e) {
            logger.error("Error {}", e);
            throw GenericExceptionHandler.exceptionHandler(e, KycRepositoryImpl.class);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long updateUserKycStatusRegistration(Long tempUserId, Long userId) throws GenericModuleException {
        try {
            logger.info("Executing the query for updating the kyc user status details > {}", updateUserKycRegistereduserByClientSql);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("unregisteredUser", false);
            parameters.put("tempUserId", tempUserId);
            parameters.put("userId", userId);
            int row = namedParameterJdbcTemplate.update(updateUserKycRegistereduserByClientSql, parameters);
            return (long) row;
        } catch (Exception e) {
            logger.error("Error {}", e);
            throw GenericExceptionHandler.exceptionHandler(e, GenericModuleException.class);
        }
    }

    @Override
    @Transactional
    public List<KycProcessDocumentDetailsCM> tempKycDetailsByRequesteeId(List<Long> requesteeUserId) throws GenericModuleException {
        try {
            String sql = "select * from temp_kyc_document_details_view where requestee_userid IN (:requesteeUserId)";
            Map<String, List<Long>> parameter = Collections.singletonMap("requesteeUserId", requesteeUserId);

            return namedParameterJdbcTemplate.query(sql, parameter, new KycProcessDocumentDetailsCMRowMapper());
        } catch (Exception e) {
            logger.error("Error {}",e);
            throw GenericExceptionHandler.exceptionHandler(e, KycRepositoryImpl.class);
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Long requestKycDocumentSave(KycDocumentDetails kycDocumentDetails) throws GenericModuleException {
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();

            String insertKycRequestDocumentSql = "INSERT INTO kyc_db.kyc_documents " +
                    "(company_id, requestor_userid, requestee_userid, document_id, created_date, created_by, process_status) " +
                    "VALUES(:companyId, :requestorUserId, :requesteeUserId, :documentId, :createdDate, :createdBy, :processStatus)";

            logger.info("Executing the query for inserting the kyc documents details -> {}", insertKycRequestDocumentSql);

            SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(kycDocumentDetails);
            namedParameterJdbcTemplate.update(insertKycRequestDocumentSql, fileParameters, keyHolder);
            return keyHolder.getKey().longValue();
        }catch (Exception e){
            logger.error("Error {}",e);
            throw GenericExceptionHandler.exceptionHandler(e, KycRepositoryImpl.class);
        }
    }

    public Boolean deleteTempKyc(Long requesteeId) throws GenericModuleException {

        try {
            Map<String, Long> parameter = new HashMap<>();
            parameter.put("requesteeId", requesteeId);

            String sql = "delete from kyc_db.temp_kyc_documents " +
                    "where requestee_userid=:requesteeId";
            logger.info("Executing the delete query for temp kyc documents -> {}", sql);
            int row = namedParameterJdbcTemplate.update(sql, parameter);
            if(row>0){
                return true;
            }
            else{
                return false;
            }

        } catch (Exception e) {
            logger.error("Error {}",e);
            throw GenericExceptionHandler.exceptionHandler(e, KycRepositoryImpl.class);
        }

    }

}
