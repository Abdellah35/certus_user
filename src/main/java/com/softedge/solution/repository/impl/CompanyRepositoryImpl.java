package com.softedge.solution.repository.impl;

import com.softedge.solution.contractmodels.CompanyCM;
import com.softedge.solution.repomodels.Company;
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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class CompanyRepositoryImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${countries.sql}")
    private String countrySql;
    @Value("${country.code.country.sql}")
    private String countryCodeToCountrySql;

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(@Qualifier("core-db") DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long saveCompanyDetails(Company company) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = "INSERT INTO certus_core_db.company_profile " +
                    "(logo_url, company_name, website, country, state, city," +
                    "created_date, created_by, modified_date, modified_by) " +
                    "VALUES(:logo, :companyName, :website, :country, :state, :city, :createdDate, " +
                    ":createdBy, :modifiedDate, :modifiedBy)";
            logger.info("Executing the query to save company_profile -> {}", sql);
            SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(company);
            namedParameterJdbcTemplate.update(sql, fileParameters, keyHolder);
            return keyHolder.getKey().longValue();
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return (long) 0;
        }
    }

        @Transactional(propagation = Propagation.REQUIRED)
        public Long updateCompanyDetails(Company company) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {
            try {
                String sql = "UPDATE certus_core_db.company_profile " +
                        "SET " +
                        "logo_url=:logo," +
                        "company_name=:companyName," +
                        "website=:website," +
                        "country=:country," +
                        "city=:city," +
                        "modified_date=:modifiedDate," +
                        "modified_by=:modifiedBy " +
                        "where id=:id";
                logger.info("Executing the query for update of company profile -> {}", sql);
                SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(company);
                int row = namedParameterJdbcTemplate.update(sql, fileParameters);
                return (long) row;
            } catch (Exception e) {
                logger.error("Error e :: {}", e);
                return (long) 0;
            }
    }

    public Company getCompanyDetailsById(Long id){
        Company company = null;
        try {
            Map<String, Long> parameter = new HashMap<>();
            parameter.put("id", id);

            String sql = "select * from certus_core_db.company_profile " +
                    "where id=:id";


            logger.info("Executing the query for get of company profile -> {}", sql);

            company = namedParameterJdbcTemplate.queryForObject(sql, parameter, new RowMapper<Company>() {
                @Override
                public Company mapRow(ResultSet resultSet, int i) throws SQLException {
                    Company company = new Company();
                    company.setId(resultSet.getLong("id"));
                    company.setCountry(resultSet.getLong("country"));
                    company.setState(resultSet.getLong("state"));
                    company.setWebsite(resultSet.getString("website"));
                    company.setLogo(resultSet.getString("logo_url"));
                    company.setCompanyName(resultSet.getString("company_name"));
                    company.setCity(resultSet.getString("city"));
                   return company;

                }
            });
            return company;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return null;
        }

    }


    public List<CompanyCM> getAllCompanies() throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {
        List<CompanyCM> companies = new ArrayList<>();
        try {

            String company_sql = "select cp.company_name , cp.website ,cp.city ,cp.website ,cp.logo_url ,cp.id as company_id, cp.state ,cm.country_code \n" +
                    "from certus_core_db.company_profile cp , certus_core_db.country_mtb cm \n" +
                    "where cp.country =cm.id;";


            logger.info("Executing the query for get all of company profile  {}", company_sql);

            companies = namedParameterJdbcTemplate.query(company_sql, new RowMapper<CompanyCM>() {
                @Override
                public CompanyCM mapRow(ResultSet resultSet, int i) throws SQLException {
                    CompanyCM company = new CompanyCM();
                    company.setId(resultSet.getLong("company_id"));
                    company.setCountry(resultSet.getString("country_code"));
                    company.setState(resultSet.getLong("state"));
                    company.setWebsite(resultSet.getString("website"));
                    company.setLogo(resultSet.getString("logo_url"));
                    company.setCompanyName(resultSet.getString("company_name"));
                    company.setCity(resultSet.getString("city"));
                    return company;

                }
            });
            return companies;
        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return null;
        }

    }

    public Boolean deleteCompanyById(Long id) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {

        try {
            Map<String, Long> parameter = new HashMap<>();
            parameter.put("id", id);

            String sql = "delete from certus_core_db.company_profile " +
                    "where id=:id";

            Company company = getCompanyDetailsById(id);
            logger.info("Executing the delete query for get of company profile -> {}", sql);
            SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(company);
            int row = namedParameterJdbcTemplate.update(sql, fileParameters);
            if(row>0){
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            logger.error("Error e :: {}", e);
            return false;
        }

    }


    public Long getCompanyIdByUsername(String username) {
        String sql = "SELECT cp.id from company_profile cp, " +
                " user_tbl ut," +
                "user_company_mapping ucm " +
                "where ut.id = ucm.user_id " +
                "and ucm.company_id = cp.id " +
                "and ut.email_id=:username ";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        Long id = 0l;
        try {
            id = namedParameterJdbcTemplate.queryForObject(sql, parameters, Long.class);
        } catch (EmptyResultDataAccessException e) {

        }
        return id;
    }


}
