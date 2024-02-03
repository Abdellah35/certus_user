package com.softedge.solution.service;

import com.softedge.solution.contractmodels.UserAddressCM;
import com.softedge.solution.contractmodels.UserCM;
import com.softedge.solution.exceptionhandlers.custom.bean.LocationException;
import com.softedge.solution.repomodels.Location;

import java.util.List;

public interface LocationService {

    List<UserAddressCM> getLocationsByUser(UserCM userRegistration) throws LocationException;

    public long saveLocations(List<Location> locations);
}
