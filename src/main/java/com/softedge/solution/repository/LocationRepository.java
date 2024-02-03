package com.softedge.solution.repository;

import com.softedge.solution.exceptionhandlers.custom.bean.LocationException;
import com.softedge.solution.repomodels.Location;
import com.softedge.solution.repomodels.UserRegistration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LocationRepository extends CrudRepository<Location, Integer> {

    List<Location> findLocationsByuser(UserRegistration user) throws LocationException;
}
