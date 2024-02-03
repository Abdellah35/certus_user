package com.softedge.solution.repository.rowmapper;

import com.softedge.solution.repomodels.LocationDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationRowmapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                LocationDetails addressCM = new LocationDetails();
                addressCM.setStateName(resultSet.getString("state_name"));
                addressCM.setStateId(resultSet.getLong("state_id"));
                addressCM.setCountryName(resultSet.getString("country_name"));
                addressCM.setPincode(resultSet.getString("pincode"));
                addressCM.setCountryCode(resultSet.getString("country_code"));
                addressCM.setCity(resultSet.getString("city"));
                addressCM.setAddress(resultSet.getString("address"));
                addressCM.setId(resultSet.getLong("id"));
                addressCM.setUserId(resultSet.getLong("user_id"));
                addressCM.setCreatedBy(resultSet.getString("created_by"));
                addressCM.setCreatedDate(resultSet.getDate("created_date"));
                addressCM.setModifiedBy(resultSet.getString("modified_by"));
                addressCM.setModifiedDate(resultSet.getDate("modified_date"));
                return addressCM;
        }
}
