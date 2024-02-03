package com.softedge.solution.repository.impl;

import com.softedge.solution.contractmodels.AdminDashboardUserCountSummaryCM;
import com.softedge.solution.contractmodels.ClientCM;
import com.softedge.solution.contractmodels.ClientDashboardUserCountSummaryCM;
import com.softedge.solution.contractmodels.KycRequestedUserCM;
import com.softedge.solution.repomodels.Client;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional
public class ClientRepositoryImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${get.all.clients.sql}")
    private String getAllClientsQuerySql;

    @Value("${client.dashboard.user.count.summary}")
    private String clientDashboardUserCountInfoSql;

    @Value("${admin.dashboard.user.count.summary}")
    private String adminDashboardUserCountInfoSql;


    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(@Qualifier("core-db") DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int mapUserCompany(Client client)  {
        try {
           // KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = "INSERT INTO certus_core_db.user_company_mapping " +
                    "(user_id, company_id," +
                    "created_date, created_by, modified_date, modified_by) " +
                    "VALUES(:userId, :companyId, :createdDate, " +
                    ":createdBy, :modifiedDate, :modifiedBy)";
            logger.info("Executing the query to save user_company_mapping -> {}", sql);
            SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(client);
            int addedId = namedParameterJdbcTemplate.update(sql, fileParameters);
            // return keyHolder.getKey().longValue();
            return addedId;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return  0;
        }
    }

    public Client getCompanyByUserId(Long id) {
        Client clientQuery = null;

        try {
            Map<String, Long> parameter = new HashMap<>();
            parameter.put("id", id);

            String sql = "select * from certus_core_db.user_company_mapping " +
                    "where user_id=:id";

            logger.info("Executing the query for get of user company mapping -> {}", sql);

            clientQuery = namedParameterJdbcTemplate.queryForObject(sql, parameter, new RowMapper<Client>() {
                @Override
                public Client mapRow(ResultSet resultSet, int i) throws SQLException {
                    Client client = new Client();
                    client.setUserId(resultSet.getLong("user_id"));
                    client.setCompanyId(resultSet.getLong("company_id"));
                    return client;
                }
            });

            return clientQuery;

        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return null;
        }

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Long updateClientDetails(Client client)  {
        try {
            String sql = "UPDATE certus_core_db.user_company_mapping " +
                    "SET " +
                    "company_id=:companyId," +
                    "modified_date=:modifiedDate," +
                    "modified_by=:modifiedBy " +
                    "where user_id=:userId";
            logger.info("Executing the query for update of update client -> {}", sql);
            SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(client);
            int row = namedParameterJdbcTemplate.update(sql, fileParameters);
            return (long) row;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return (long) 0;
        }
    }

    public Boolean deleteCompanyUserMappingById(Long id) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {

        try {
            Map<String, Long> parameter = new HashMap<>();
            parameter.put("id", id);

            String sql = "delete from certus_core_db.user_company_mapping " +
                    "where user_id=:id";
            logger.info("Executing the delete query for get of company user mapping -> {}", sql);
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
            return false;
        }

    }


    public List<ClientCM> getAllClients() throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {
        List<ClientCM> clientQuery = new ArrayList<>();

        try {


            logger.info("Executing the query for get of user company mapping -> {}", getAllClientsQuerySql);

            clientQuery = namedParameterJdbcTemplate.query(getAllClientsQuerySql, new RowMapper<ClientCM>() {
                @Override
                public ClientCM mapRow(ResultSet resultSet, int i) throws SQLException {
                    ClientCM client = new ClientCM();
                    client.setId(resultSet.getLong("user_id"));
                    client.setCompanyId(resultSet.getLong("company_id"));
                    client.setCategory(resultSet.getString("category"));
                    String rolesAsString = resultSet.getString("roles");
                    String[] rolesAsArray = rolesAsString.split(",");
                    List<String> roles = Arrays.asList(rolesAsArray);
                    client.setRoles(roles);
                    client.setEmailId(resultSet.getString("email_id"));
                    client.setName(resultSet.getString("name"));
    
                    return client;
                }
            });

            return clientQuery;

        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return null;
        }

    }


    public List<KycRequestedUserCM> getClientRequestedUsers(String finalSql, Long companyId, int rows, int page, String orderBy, String sort) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("companyId", companyId);
        parameters.put("VALUE", orderBy);
        parameters.put("SORT", sort);
        parameters.put("ROWS", rows);
        parameters.put("PAGE", page);

        List<KycRequestedUserCM> kycRequestedUserCMS = new ArrayList<>();
        kycRequestedUserCMS = namedParameterJdbcTemplate.query(finalSql, parameters, new RowMapper<KycRequestedUserCM>() {
            @Override
            public KycRequestedUserCM mapRow(ResultSet resultSet, int i) throws SQLException {
                KycRequestedUserCM kycRequestedUserCM = new KycRequestedUserCM();
                kycRequestedUserCM.setEmailId(resultSet.getString("EMAIL_ID"));
                kycRequestedUserCM.setId(resultSet.getLong("ID"));
                kycRequestedUserCM.setName(resultSet.getString("NAME"));
                kycRequestedUserCM.setRegistered(resultSet.getBoolean("REGISTERED"));
                kycRequestedUserCM.setKycStatus(resultSet.getString("kyc_status"));
                kycRequestedUserCM.setUserType(resultSet.getString("user_type"));
                return kycRequestedUserCM;

            }
        });
        return kycRequestedUserCMS;

    }

    public Long getClientRequestedUserCount(String finalSql, Long companyId) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("companyId", companyId);
        Long kycRequestedUsercount = 0L;
        kycRequestedUsercount = namedParameterJdbcTemplate.queryForObject(finalSql, parameters, Long.class);
        return kycRequestedUsercount;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long updateClientInfo(ClientCM clientCM) {
        try {
            String sql = "UPDATE certus_core_db.user_tbl " +
                    "SET " +
                    "name=:name," +
                    "dob=:dob," +
                    "gender=:gender, " +
                    "photo=:photo, " +
                    "modified_date=:modifiedDate, " +
                    "modified_by=:modifiedBy, " +
                    "profile_completed=:profileCompleted " +
                    "where id=:id";
            logger.info("Executing the query for update of update client -> {}", sql);
            Map<String, Object> fileParameters = new HashMap<>();
            fileParameters.put("name", clientCM.getName());
            fileParameters.put("dob", clientCM.getDob());
            fileParameters.put("gender", clientCM.getGender());
            fileParameters.put("photo", clientCM.getPhoto());
            fileParameters.put("modifiedBy", clientCM.getEmailId());
            fileParameters.put("modifiedDate", new Date());
            fileParameters.put("id", clientCM.getId());
            fileParameters.put("profileCompleted", true);
            int row = namedParameterJdbcTemplate.update(sql, fileParameters);
            return (long) row;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return (long) 0;
        }
    }

    public ClientDashboardUserCountSummaryCM getClientDashboardUserCounts(Long companyId) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("companyId", companyId);

        ClientDashboardUserCountSummaryCM clientDashboardUserCountSummaryCMS;
        clientDashboardUserCountSummaryCMS = namedParameterJdbcTemplate.queryForObject(clientDashboardUserCountInfoSql, parameters, new RowMapper<ClientDashboardUserCountSummaryCM>() {
            @Override
            public ClientDashboardUserCountSummaryCM mapRow(ResultSet resultSet, int i) throws SQLException {
                ClientDashboardUserCountSummaryCM userCountSummaryCM = new ClientDashboardUserCountSummaryCM();
                userCountSummaryCM.setRegistered(resultSet.getLong("registered_count"));
                userCountSummaryCM.setUnregistered(resultSet.getLong("unregistered_count"));
                userCountSummaryCM.setIpvCompleted(resultSet.getLong("ipv_completed_count"));
                return userCountSummaryCM;

            }
        });
        return clientDashboardUserCountSummaryCMS;

    }

    public AdminDashboardUserCountSummaryCM getAdminDashboardUserCounts() throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {

        Map<String, Object> parameters = new HashMap<>();


        AdminDashboardUserCountSummaryCM adminDashboardUserCountSummaryCM;
        adminDashboardUserCountSummaryCM = namedParameterJdbcTemplate.queryForObject(adminDashboardUserCountInfoSql, parameters, new RowMapper<AdminDashboardUserCountSummaryCM>() {
            @Override
            public AdminDashboardUserCountSummaryCM mapRow(ResultSet resultSet, int i) throws SQLException {
                AdminDashboardUserCountSummaryCM userCountSummaryCM = new AdminDashboardUserCountSummaryCM();
                userCountSummaryCM.setRegistered(resultSet.getLong("registered"));
                userCountSummaryCM.setUnregistered(resultSet.getLong("unregistered"));
                userCountSummaryCM.setIpvAccepted(resultSet.getLong("ipv_accepted_count"));
                userCountSummaryCM.setIpvInProgress(resultSet.getLong("ipv_in_progress_count"));
                userCountSummaryCM.setIpvPending(resultSet.getLong("ipv_pending_count"));
                userCountSummaryCM.setIpvRejected(resultSet.getLong("ipv_rejected_count"));
                return userCountSummaryCM;

            }
        });
        return adminDashboardUserCountSummaryCM;

    }


}
