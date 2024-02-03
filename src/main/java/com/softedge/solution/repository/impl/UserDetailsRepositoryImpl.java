package com.softedge.solution.repository.impl;

import com.softedge.solution.contractmodels.UserSessionCM;
import com.softedge.solution.repomodels.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class UserDetailsRepositoryImpl {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${user.session.value.sql}")
    private String userSessionSql;

    @Value("${get.alluser.session.sql}")
    private String allUserSessionSql;

    @Value("${user.detail.value.sql}")
    private String userDetailsSql;

    @Value("${user.detail.value.by.id.sql}")
    private String userDetailsByIdSql;

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(@Qualifier("core-db") DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public UserSessionCM getUserSession(String username) {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("emailId", username);
        UserSessionCM userSessionCM = null;
        userSessionCM = namedParameterJdbcTemplate.queryForObject(userSessionSql, parameter, new RowMapper<UserSessionCM>() {

            @Override
            public UserSessionCM mapRow(ResultSet resultSet, int i) throws SQLException {
                UserSessionCM user = new UserSessionCM();
                user.setCategory(resultSet.getString("category"));
                user.setEmailId(resultSet.getString("email_id"));
                user.setName(resultSet.getString("name"));
                user.setId(resultSet.getLong("id"));
                user.setForcePasswordChange(resultSet.getBoolean("force_password_change"));
                user.setProfileCompleted(resultSet.getBoolean("profile_completed"));
                user.setIpvCompleted(resultSet.getBoolean("ipv_completed"));
                user.setProfilePic(resultSet.getString("photo"));
                user.setUserType(resultSet.getString("user_type"));
                user.setVerificationState(resultSet.getString("verification_status"));
                return user;
            }
        });
        return userSessionCM;
    }

    public UserDetails getUserByUsername(String emailId) {
        UserDetails userDetails = null;
        try {
            Map<String, String> parameter = new HashMap<>();
            parameter.put("emailId", emailId);
            userDetails = namedParameterJdbcTemplate.queryForObject(userDetailsSql, parameter, new RowMapper<UserDetails>() {

                @Override
                public UserDetails mapRow(ResultSet resultSet, int i) throws SQLException {
                    UserDetails user = new UserDetails();
                    user.setEmailId(resultSet.getString("email_id"));
                    user.setName(resultSet.getString("name"));
                    user.setId(resultSet.getLong("id"));
                    user.setPhone(resultSet.getLong("phone"));
                    user.setNationality(resultSet.getString("nationality"));
                    user.setGender(resultSet.getString("gender"));
                    user.setDob(resultSet.getDate("dob"));
                    user.setPhoto(resultSet.getString("photo"));
                    user.setCreatedBy(resultSet.getString("created_by"));
                    user.setModifiedBy(resultSet.getString("modified_by"));
                    user.setCreatedDate(resultSet.getDate("created_date"));
                    user.setModifiedDate(resultSet.getDate("modified_date"));
                    user.setForcePasswordChange(resultSet.getBoolean("force_password_change"));
                    user.setActive(resultSet.getBoolean("active"));
                    user.setIpvCompleted(resultSet.getBoolean("ipv_completed"));
                    user.setProfileCompleted(resultSet.getBoolean("profile_completed"));
                    user.setUserType(resultSet.getString("user_type"));
                    return user;
                }
            });

        } catch (Exception e) {
            logger.error("Error e {}", e);
        }
        return userDetails;
    }

    public UserDetails getUserByid(Long id) {
        UserDetails userDetails = null;
        try {
            Map<String, Long> parameter = new HashMap<>();
            parameter.put("id", id);
            userDetails = namedParameterJdbcTemplate.queryForObject(userDetailsByIdSql, parameter, new RowMapper<UserDetails>() {

                @Override
                public UserDetails mapRow(ResultSet resultSet, int i) throws SQLException {
                    UserDetails user = new UserDetails();
                    user.setEmailId(resultSet.getString("email_id"));
                    user.setName(resultSet.getString("name"));
                    user.setId(resultSet.getLong("id"));
                    user.setPhone(resultSet.getLong("phone"));
                    user.setNationality(resultSet.getString("nationality"));
                    user.setGender(resultSet.getString("gender"));
                    user.setDob(resultSet.getDate("dob"));
                    user.setPhoto(resultSet.getString("photo"));
                    user.setCreatedBy(resultSet.getString("created_by"));
                    user.setModifiedBy(resultSet.getString("modified_by"));
                    user.setCreatedDate(resultSet.getDate("created_date"));
                    user.setModifiedDate(resultSet.getDate("modified_date"));
                    user.setForcePasswordChange(resultSet.getBoolean("force_password_change"));
                    user.setActive(resultSet.getBoolean("active"));
                    user.setIpvCompleted(resultSet.getBoolean("ipv_completed"));
                    user.setProfileCompleted(resultSet.getBoolean("profile_completed"));
                    return user;
                }
            });

        } catch (Exception e) {
            logger.error("Error e {}", e);
        }
        return userDetails;
    }

    public Long updateUserDetails(UserDetails userDetails) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {
        try {
            String sql = "UPDATE user_tbl " +
                    "SET " +
                    "name=:name," +
                    "category=:category," +
                    "dob=:dob," +
                    "gender=:gender," +
                    "nationality=:nationality," +
                    "phone=:phone," +
                    "photo=:photo," +
                    "profile_completed=:profileCompleted," +
                    "modified_date=:modifiedDate," +
                    "modified_by=:modifiedBy " +
                    "where id=:id";
            logger.info("Executing the query for update of location details -> {}", sql);
            SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(userDetails);
            int row = namedParameterJdbcTemplate.update(sql, fileParameters);
            return (long) row;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return (long) 0;
        }
    }

    public List<UserSessionCM> getUsersSession() {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("category", "USER");
        List<UserSessionCM> userSessionCM = null;
        userSessionCM = namedParameterJdbcTemplate.query(allUserSessionSql, parameter, new RowMapper<UserSessionCM>() {

            @Override
            public UserSessionCM mapRow(ResultSet resultSet, int i) throws SQLException {
                UserSessionCM user = new UserSessionCM();
                user.setCategory(resultSet.getString("category"));
                user.setEmailId(resultSet.getString("email_id"));
                user.setName(resultSet.getString("name"));
                user.setId(resultSet.getLong("id"));
                user.setForcePasswordChange(resultSet.getBoolean("force_password_change"));
                user.setProfileCompleted(resultSet.getBoolean("profile_completed"));
                user.setIpvCompleted(resultSet.getBoolean("ipv_completed"));
                user.setProfilePic(resultSet.getString("photo"));
                user.setVerificationState(resultSet.getString("verification_status"));
                return user;
            }
        });
        return userSessionCM;
    }
}
