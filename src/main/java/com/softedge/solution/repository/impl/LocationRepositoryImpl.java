package com.softedge.solution.repository.impl;

import com.softedge.solution.contractmodels.UserAddressCM;
import com.softedge.solution.repomodels.LocationDetails;
import com.softedge.solution.repository.rowmapper.LocationRowmapper;
import com.softedge.solution.repository.rowmapper.UserAddressCMRowmapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@Transactional
public class LocationRepositoryImpl {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${location.by.user.id.sql}")
    private String userLocationSql;

    @Value("${location.by.id.sql}")
    private String locationSql;

    @Autowired
    public void setDataSource(@Qualifier("core-db") DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<UserAddressCM> getUserAddressesByUserId(Long userId) {
        Map<String, Long> parameter = new HashMap<>();
        parameter.put("userId", userId);
        List<UserAddressCM> userAddressCM = new ArrayList<>();
        userAddressCM = namedParameterJdbcTemplate.query(userLocationSql, parameter, new UserAddressCMRowmapper());
        return userAddressCM;
    }

    public LocationDetails getUserAddressesByLocationId(Long locationId) {
        Map<String, Long> parameter = new HashMap<>();
        parameter.put("locationId", locationId);
        LocationDetails locationDetails = null;
        locationDetails = (LocationDetails) namedParameterJdbcTemplate.queryForObject(locationSql, parameter, new LocationRowmapper());
        return locationDetails;
    }


    public Long saveLocationDetails(LocationDetails locationDetails) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO location_tbl " +
                "(country_id, state_id, city, pincode, address, user_id, created_date, created_by) " +
                "VALUES(:countryId, :stateId, :city, :pincode, :address, :userId, :createdDate, :createdBy)";
        logger.info("Executing the query for save of location_tbl  -> {}", sql);
        SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(locationDetails);
        namedParameterJdbcTemplate.update(sql, fileParameters, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Long updateLocationDetails(LocationDetails locationDetails) throws EmptyResultDataAccessException, DataAccessException, SQLDataException, Exception {
        String sql = "UPDATE location_tbl " +
                "SET " +
                "country_id=:countryId," +
                "state_id=:stateId," +
                "city=:city," +
                "pincode=:pincode," +
                "address=:address," +
                "user_id=:userId," +
                "modified_date=:modifiedDate," +
                "modified_by=:modifiedBy " +
                "where id=:id";
        logger.info("Executing the query for update of location details -> {}", sql);
        SqlParameterSource fileParameters = new BeanPropertySqlParameterSource(locationDetails);
        int row = namedParameterJdbcTemplate.update(sql, fileParameters);
        return (long) row;

    }
}
