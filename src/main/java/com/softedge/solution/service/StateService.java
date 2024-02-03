package com.softedge.solution.service;

import com.softedge.solution.contractmodels.StateCM;
import com.softedge.solution.exceptionhandlers.custom.bean.StateException;
import org.springframework.http.ResponseEntity;

public interface StateService {

    StateCM getStateById(Long stateId) throws StateException;

    ResponseEntity<?> getStateByCountryCode(String countryCode) throws StateException;

    StateCM getStateByStateId(Long stateId) throws StateException;
}
