package com.softedge.solution.repository.rowmapper;

import com.softedge.solution.contractmodels.UserAddressCM;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAddressCMRowmapper implements RowMapper {
    @Override
    public UserAddressCM mapRow(ResultSet resultSet, int i) throws SQLException {
        UserAddressCM addressCM = new UserAddressCM();
        addressCM.setStateName(resultSet.getString("state_name"));
        addressCM.setState(resultSet.getLong("state_id"));
        addressCM.setCountryName(resultSet.getString("country_name"));
        addressCM.setPincode(resultSet.getString("pincode"));
        addressCM.setCountryCode(resultSet.getString("country_code"));
        addressCM.setCity(resultSet.getString("city"));
        addressCM.setAddress(resultSet.getString("address"));
        addressCM.setId(resultSet.getLong("id"));
        return addressCM;
    }
}
