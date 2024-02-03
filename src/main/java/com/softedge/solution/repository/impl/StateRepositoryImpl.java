package com.softedge.solution.repository.impl;

import com.softedge.solution.contractmodels.StateCM;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class StateRepositoryImpl {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${state.country.code.sql}")
    private String stateCountryCodeSql;

    @Value("${state.sql}")
    private String stateSql;

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(@Qualifier("core-db") DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<StateCM> getStateByCountryCode(String countryCode) {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("countryCode", countryCode);
        List<StateCM> stateCM = new ArrayList<>();
        stateCM = namedParameterJdbcTemplate.query(stateCountryCodeSql, parameter, new RowMapper<StateCM>() {
            @Override
            public StateCM mapRow(ResultSet resultSet, int i) throws SQLException {
                return new StateCM(resultSet.getLong("id"), resultSet.getString("state_name"));
            }
        });
        return stateCM;
    }

    public StateCM getStateById(Long stateId) {
        Map<String, Long> parameter = new HashMap<>();
        parameter.put("id", stateId);
        StateCM stateCM = null;
        stateCM = namedParameterJdbcTemplate.queryForObject(stateSql, parameter, new RowMapper<StateCM>() {
            @Override
            public StateCM mapRow(ResultSet resultSet, int i) throws SQLException {
                return new StateCM(resultSet.getLong("id"), resultSet.getString("state_name"));

            }
        });
        return stateCM;
    }
}
