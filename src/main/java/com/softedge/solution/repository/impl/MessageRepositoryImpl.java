package com.softedge.solution.repository.impl;

import com.softedge.solution.contractmodels.MessageDetailsCM;
import com.softedge.solution.exceptionhandlers.GenericExceptionHandler;
import com.softedge.solution.exceptionhandlers.custom.GenericModuleException;
import com.softedge.solution.repomodels.MessagesDetails;
import com.softedge.solution.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${insert.comment.messagesDetails.sql}")
    private String insertMessagesSql;

    @Value("${get.message.by.id.sql}")
    private String messageDetailsCMByIdSql;

    @Value("${get.messages.by.kyc}")
    private String messagesByKycIdSql;

    @Autowired
    public void setDataSource(@Qualifier("core-db") DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long messageDetailsSave(MessagesDetails messagesDetails) throws GenericModuleException {
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            logger.info("Executing the query for inserting the kyc Message details -> {}", insertMessagesSql);
            SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(messagesDetails);
            namedParameterJdbcTemplate.update(insertMessagesSql, fileParameters, keyHolder);
            return keyHolder.getKey().longValue();
        }catch (Exception e){
            logger.error("Error {}",e);
            throw GenericExceptionHandler.exceptionHandler(e, MessageRepositoryImpl.class);
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MessageDetailsCM getMessageDetailsById(Long id) throws GenericModuleException {
        try{
            Map<String, Long> parameter = new HashMap<>();
            parameter.put("id", id);
            logger.info("Executing the query for get the Message details CM-> {}", messageDetailsCMByIdSql);

            return namedParameterJdbcTemplate.queryForObject(messageDetailsCMByIdSql, parameter, new RowMapper<MessageDetailsCM>() {
                @Override
                public MessageDetailsCM mapRow(ResultSet resultSet, int i) throws SQLException {
                    MessageDetailsCM messageDetailsCM = new MessageDetailsCM();
                    messageDetailsCM.setEmailId(resultSet.getString("EMAIL_ID"));
                    messageDetailsCM.setId(resultSet.getLong("ID"));
                    messageDetailsCM.setName(resultSet.getString("NAME"));
                    messageDetailsCM.setMessage(resultSet.getString("MESSAGE"));
                    messageDetailsCM.setTimestamp(resultSet.getDate("LAST_UPDATED_TIMESTAMP"));
                    return messageDetailsCM;
                }
            });
        }catch (Exception e){
            logger.error("Error {}",e);
            throw GenericExceptionHandler.exceptionHandler(e, MessageRepositoryImpl.class);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<MessageDetailsCM> getMessageDetailsByKycId(Long kycId) throws GenericModuleException {
        try{
            Map<String, Long> parameter = new HashMap<>();
            parameter.put("kycId", kycId);
            logger.info("Executing the query for get the Message details CM-> {}", messagesByKycIdSql);

            return namedParameterJdbcTemplate.query(messagesByKycIdSql, parameter, new RowMapper<MessageDetailsCM>() {
                @Override
                public MessageDetailsCM mapRow(ResultSet resultSet, int i) throws SQLException {
                    MessageDetailsCM messageDetailsCM = new MessageDetailsCM();
                    messageDetailsCM.setEmailId(resultSet.getString("EMAIL_ID"));
                    messageDetailsCM.setId(resultSet.getLong("ID"));
                    messageDetailsCM.setName(resultSet.getString("NAME"));
                    messageDetailsCM.setMessage(resultSet.getString("MESSAGE"));
                    messageDetailsCM.setTimestamp(resultSet.getDate("LAST_UPDATED_TIMESTAMP"));
                    return messageDetailsCM;
                }
            });
        }catch (Exception e){
            logger.error("Error {}",e);
            throw GenericExceptionHandler.exceptionHandler(e, MessageRepositoryImpl.class);
        }
    }

    @Override
    @Transactional
    public Boolean deleteMessageById(Long id) throws GenericModuleException{
        try {
            Map<String, Long> parameter = new HashMap<>();
            parameter.put("id", id);
            String sql = "delete from certus_core_db.COMMENT_MSG_TBL " +
                    "where id=:id";
            logger.info("Executing the delete query for delete the message -> {}", sql);
            /*  SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(company);*/
            int row = namedParameterJdbcTemplate.update(sql, parameter);
            if(row>0){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            throw GenericExceptionHandler.exceptionHandler(e, MessageRepositoryImpl.class);
        }

    }


}
