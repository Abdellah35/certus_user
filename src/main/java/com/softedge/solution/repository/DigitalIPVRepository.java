package com.softedge.solution.repository;

import com.softedge.solution.repomodels.DigitalIPV;
import com.softedge.solution.repomodels.UserRegistration;
import org.springframework.data.repository.CrudRepository;

public interface DigitalIPVRepository extends CrudRepository<DigitalIPV, Integer> {


    DigitalIPV findByUser(UserRegistration userRegistration);
}
