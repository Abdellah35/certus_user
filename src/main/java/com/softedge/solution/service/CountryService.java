package com.softedge.solution.service;

import com.softedge.solution.contractmodels.CountryCM;
import com.softedge.solution.exceptionhandlers.custom.bean.CountryException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;

public interface CountryService {

    CountryCM getCountryByCountryCode(String countryCode) throws CountryException;

    ResponseEntity<?> getCountries() throws CountryException;

    ResponseEntity<?> loadCountries() throws CountryException, URISyntaxException, IOException;
}
