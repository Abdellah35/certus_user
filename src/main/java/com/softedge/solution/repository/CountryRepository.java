package com.softedge.solution.repository;

import com.softedge.solution.repomodels.Country;
import org.springframework.data.repository.CrudRepository;

public interface CountryRepository extends CrudRepository<Country, Long> {

    Country findByCountryCode(String countryCode);
}
