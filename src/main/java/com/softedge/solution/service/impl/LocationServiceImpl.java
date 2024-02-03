package com.softedge.solution.service.impl;

import com.softedge.solution.contractmodels.UserAddressCM;
import com.softedge.solution.contractmodels.UserCM;
import com.softedge.solution.exceptionhandlers.custom.bean.CountryException;
import com.softedge.solution.exceptionhandlers.custom.bean.LocationException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeCommonEnum;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import com.softedge.solution.repomodels.Location;
import com.softedge.solution.repomodels.LocationDetails;
import com.softedge.solution.repository.LocationRepository;
import com.softedge.solution.repository.impl.LocationRepositoryImpl;
import com.softedge.solution.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    LocationRepositoryImpl locationRepositoryImpl;

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressCM> getLocationsByUser(UserCM userCM) throws LocationException {
        if (userCM != null) {
            try {
                List<UserAddressCM> locations = locationRepositoryImpl.getUserAddressesByUserId(userCM.getId());
                return locations;
            } catch (EmptyResultDataAccessException e) {
                logger.error("Empty access :: {}", e);
                throw new CountryException(ErrorCodeUserEnum.USER_ID_EMPTY, ErrorCodeUserEnum.USER_ID_EMPTY.getName());
            } catch (DataAccessException e) {
                logger.error("Data access :: {}", e);
                throw new LocationException(ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR, ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR.getName());
            } catch (Exception e) {
                logger.error("The error :: {}", e);
                throw new LocationException(ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR, ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR.getName());
            }
        } else {
            throw new CountryException(ErrorCodeUserEnum.USER_ID_EMPTY, ErrorCodeUserEnum.USER_ID_EMPTY.getName());
        }
    }

    @Transactional
    public long saveLocations(List<Location> locations) {
        List<Location> locationList = (List<Location>) locationRepository.saveAll(locations);
        if (locationList.size() > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @Transactional
    public LocationDetails getLocationDetailsById(Long id) {
        return locationRepositoryImpl.getUserAddressesByLocationId(id);
    }

    @Transactional
    public Long saveLocationDetails(LocationDetails locationDetails) {
        try {
            return locationRepositoryImpl.saveLocationDetails(locationDetails);
        } catch (Exception e) {
            logger.error("Data access :: {}", e);
            throw new LocationException(ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR, ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR.getName());

        }
    }

    @Transactional
    public Long updateLocationDetails(LocationDetails locationDetails) {
        try {
            return locationRepositoryImpl.updateLocationDetails(locationDetails);
        } catch (Exception e) {
            logger.error("Data access :: {}", e);
            throw new LocationException(ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR, ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR.getName());

        }
    }
}
