package com.softedge.solution.service.impl;

import com.softedge.solution.contractmodels.StateCM;
import com.softedge.solution.exceptionhandlers.custom.bean.StateException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeCommonEnum;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeCountryEnum;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeStateEnum;
import com.softedge.solution.repository.StateRepository;
import com.softedge.solution.repository.impl.StateRepositoryImpl;
import com.softedge.solution.service.StateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StateServiceImpl implements StateService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private StateRepositoryImpl stateRepositoryImpl;

    @Override
    @Transactional(readOnly = true)
    public StateCM getStateById(Long stateId) throws StateException {
        if (stateId != null) {
            try {
                StateCM stateCM = new StateCM();
                stateCM = stateRepositoryImpl.getStateById(stateId);
                if (stateCM.getId() == null) {
                    throw new StateException(ErrorCodeStateEnum.STATE_INVALID, ErrorCodeStateEnum.STATE_INVALID.getName());
                }
                return stateCM;
            } catch (EmptyResultDataAccessException e) {
                logger.error("Empty access :: {}", e);
                throw new StateException(ErrorCodeStateEnum.STATE_INVALID, ErrorCodeStateEnum.STATE_INVALID.getName());
            } catch (DataAccessException e) {
                logger.error("Data access :: {}", e);
                throw new StateException(ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR, ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR.getName());
            } catch (Exception e) {
                logger.error("Exception :: {}", e);
                throw new StateException(ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR, ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR.getName());
            }
        } else {
            throw new StateException(ErrorCodeStateEnum.STATE_INVALID, ErrorCodeStateEnum.STATE_INVALID.getName());
        }
    }

    @Override
    public ResponseEntity<?> getStateByCountryCode(String countryCode) throws StateException {
        if (countryCode != null) {
            return new ResponseEntity<>(stateRepositoryImpl.getStateByCountryCode(countryCode), HttpStatus.OK);
        } else {
            throw new StateException(ErrorCodeCountryEnum.COUNTRY_CODE_INVALID);
        }
    }

    @Override
    public StateCM getStateByStateId(Long stateId) throws StateException {
        if (stateId != null) {
            return stateRepositoryImpl.getStateById(stateId);
        } else {
            throw new StateException(ErrorCodeStateEnum.STATE_INVALID);
        }
    }
}
