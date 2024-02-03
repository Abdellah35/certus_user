package com.softedge.solution.repository.impl;

import com.softedge.solution.contractmodels.CountryCM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class CountryRepositoryImpl {

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


    public List<CountryCM> getAllCountries() {
        List<CountryCM> countryCMList = null;
        countryCMList = namedParameterJdbcTemplate.query(countrySql, new RowMapper<CountryCM>() {
            @Override
            public CountryCM mapRow(ResultSet resultSet, int i) throws SQLException {
                return new CountryCM(resultSet.getString("country_code"),
                        resultSet.getString("country_name"),
                        resultSet.getString("country_phone_code"));
            }
        });
        return countryCMList;
    }

    public CountryCM getCountryById(Long countryId) {
        Map<String, Long> parameter = new HashMap<>();
        parameter.put("id", countryId);
        CountryCM countryCM = null;
        String sql = "select * from certus_core_db.country_mtb " +
                "where id=:id";
        countryCM = namedParameterJdbcTemplate.queryForObject(sql, parameter, new RowMapper<CountryCM>() {
            @Override
            public CountryCM mapRow(ResultSet resultSet, int i) throws SQLException {
                CountryCM countryCm = new CountryCM();
                countryCm.setId(countryId);
                countryCm.setCountryCode(resultSet.getString("country_code"));
                return countryCm;
            }
        });
        return countryCM;
    }

    public CountryCM getCountryByCountryCode(String countryCode) {
        CountryCM country = null;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("countryCode", countryCode);
        country = (CountryCM) namedParameterJdbcTemplate.queryForObject(countryCodeToCountrySql, parameters, new RowMapper<CountryCM>() {
            @Override
            public CountryCM mapRow(ResultSet resultSet, int i) throws SQLException {
                CountryCM countryCM = new CountryCM();
                countryCM.setCountryCode(resultSet.getString("country_code"));
                countryCM.setCountryName(resultSet.getString("country_name"));
                countryCM.setCountryPhoneCode(resultSet.getString("country_phone_code"));
                countryCM.setId(resultSet.getLong("id"));
                return countryCM;
            }
        });
        logger.info("The country object is :: {}", country.toString());
        return country;
    }
}
