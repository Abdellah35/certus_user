package com.softedge.solution.service.impl;

import com.softedge.solution.contractmodels.*;
import com.softedge.solution.enuminfo.CategoryEnum;
import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import com.softedge.solution.repomodels.Location;
import com.softedge.solution.repomodels.LocationDetails;
import com.softedge.solution.repomodels.UserDetails;
import com.softedge.solution.repomodels.UserRegistration;
import com.softedge.solution.service.certusabstractservice.CertusAbstractUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum.USER_LOCATION_MANDATORY;

@Service
public class CertusUserDetailsServiceImpl extends CertusAbstractUserDetailsService {


    @Value("${user.image.dir}")
    private String userImageDir;

    @Value("${user.logodir}")
    private String userLogoDir;


    @Value("${base.url}")
    private String baseUrl;

    Path fileStorageLocation = Paths.get("/usr/share/nginx/html/certus-user-service/uploadlogo/").toAbsolutePath().normalize();

    @Override
    @Transactional(readOnly = true)
    public UserRegistration getUserByUsername(String username) throws UserAccountModuleException {
        try {
            UserRegistration userRegistration = userRepository.findByUsername(username);
            return userRegistration;
        } catch (Exception e) {
            throw (UserAccountModuleException) this.errorUserService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public UserRegistration addUser(UserRegistration userRegistration) throws UserAccountModuleException {
        try {
            logger.info("Registering the user :: {}", userRegistration.getUsername());
            return userRepository.save(userRegistration);
        } catch (Exception e) {
            throw (UserAccountModuleException) this.errorUserService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }


    @Override
    public ResponseEntity saveUserDetails(UserCM userCM, HttpServletRequest request) throws UserAccountModuleException {
        try {
            if (userCM != null) {
                LocationDetails locationDetails = null;
                String username = this.securityUtils.getUsernameFromToken(request);
                UserDetails user = userDetailsRepositoryImpl.getUserByUsername(username);
                userCM.setUserType(user.getUserType());
                user = this.userValidationService(userCM, user);
                if (userCM.getUserAddressCMList().size() > 0) {
                    UserAddressCM addressCM = userCM.getUserAddressCMList().get(0);
                    List<UserAddressCM> locations = locationServiceImpl.getLocationsByUser(userCM);
                    if (userCM.getId() != null && locations != null && locations.size() > 0) {
                        UserAddressCM existingAddress = locations.get(0);
                        locationDetails = locationServiceImpl.getLocationDetailsById(existingAddress.getId());
                        locationDetails.setModifiedDate(new Date());
                        locationDetails.setModifiedBy(username);
                    } else {
                        locationDetails = new LocationDetails();
                        locationDetails.setCreatedDate(new Date());
                        locationDetails.setCreatedBy(username);
                    }
                    CountryCM country = countryService.getCountryByCountryCode(addressCM.getCountryCode());
                    StateCM stateCM = stateService.getStateByStateId(addressCM.getState());
                    addressCM.setCountryName(country.getCountryName());
                    addressCM.setStateName(stateCM.getStateName());
                    locationDetails.setAddress(addressCM.getAddress());
                    locationDetails.setCity(addressCM.getCity());
                    locationDetails.setCountryId(country.getId());
                    locationDetails.setStateId(addressCM.getState());
                    locationDetails.setPincode(addressCM.getPincode());
                    locationDetails.setUserId(user.getId());
                    if (addressCM.getId() == null) {
                        long saveLocationId = locationServiceImpl.saveLocationDetails(locationDetails);
                        locationDetails.setId(saveLocationId);
                    } else {
                        locationServiceImpl.updateLocationDetails(locationDetails);
                    }
                } else {
                    throw this.errorUserService(USER_LOCATION_MANDATORY.getErrorCode());
                }
                user.setNationality(userCM.getNationality());
                if (!StringUtils.isEmpty(userCM.getProfilePictureURL())) {
                    user.setPhoto(userCM.getProfilePictureURL());
                }
                user.setProfileCompleted(true);
                user.setPhone(userCM.getPhone());
                user.setCategory(CategoryEnum.USER.getValue());
                Long userId = userDetailsRepositoryImpl.updateUserDetails(user);
                if (userId > 0) {
                    userCM.setId(user.getId());
                    userCM.getUserAddressCMList().get(0).setId(locationDetails.getId());
                    return new ResponseEntity<>(userCM, HttpStatus.OK);
                } else {
                    throw this.errorUserService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode());
                }
            } else {
                throw this.errorUserService(ErrorCodeUserEnum.USER_ID_EMPTY.getErrorCode());
            }
        } catch (Exception e) {
            logger.error("Error :: {}", e.getMessage());
            throw this.errorUserService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<?> getUserSessionInfo(HttpServletRequest request) throws UserAccountModuleException {
        String username = this.securityUtils.getUsernameFromToken(request);
        UserSessionCM userSessionCM = userDetailsRepositoryImpl.getUserSession(username);
        return new ResponseEntity<>(userSessionCM, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getUsersSession(HttpServletRequest request) throws UserAccountModuleException {
        List<UserSessionCM> userSessionCMS = userDetailsRepositoryImpl.getUsersSession();
        return new ResponseEntity<>(userSessionCMS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getCurrentUserDetails(HttpServletRequest request) throws UserAccountModuleException {
        String username = this.securityUtils.getUsernameFromToken(request);
        UserRegistration userRegistration = userRepository.findByUsername(username);
        UserCM userCM = new UserCM();
        userCM.setId(userRegistration.getId());
        userCM.setProfilePictureURL(userRegistration.getPhoto());
        userCM.setDob(userRegistration.getDob());
        userCM.setEmailId(userRegistration.getUsername());
        userCM.setGender(userRegistration.getGender());
        userCM.setName(userRegistration.getName());
        userCM.setNationality(userRegistration.getNationality());
        userCM.setPhone(userRegistration.getPhone());
        for (Location location : userRegistration.getLocation()) {
            UserAddressCM addressCM = new UserAddressCM();
            addressCM.setId(location.getId());
            addressCM.setAddress(location.getAddress());
            addressCM.setCity(location.getCity());
            addressCM.setCountryName(location.getCountry().getCountryName());
            addressCM.setCountryCode(location.getCountry().getCountryCode());
            addressCM.setPincode(location.getPincode());
            addressCM.setState(location.getState().getId());
            addressCM.setStateName(location.getState().getStateName());
            userCM.getUserAddressCMList().add(addressCM);
        }
        return new ResponseEntity<>(userCM, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getCurrentUserDetails(HttpServletRequest request, Long userId) throws UserAccountModuleException {
        String username = this.securityUtils.getUsernameFromToken(request);
        UserRegistration userRegistration = userRepository.findByUsername(username);
        if (userRegistration.getCategory().equalsIgnoreCase(CategoryEnum.CLIENT.getValue())) {
            UserDetails userDetails = userDetailsRepositoryImpl.getUserByid(userId);
            UserCM userCM = new UserCM();
            userCM.setId(userDetails.getId());
            userCM.setProfilePictureURL(userDetails.getPhoto());
            userCM.setDob(userDetails.getDob());
            userCM.setEmailId(userDetails.getEmailId());
            userCM.setGender(userDetails.getGender());
            userCM.setName(userDetails.getName());
            userCM.setNationality(userDetails.getNationality());
            userCM.setPhone(userDetails.getPhone());
            List<UserAddressCM> addressCMS = locationServiceImpl.getLocationsByUser(userCM);
            userCM.getUserAddressCMList().addAll(addressCMS);
            return new ResponseEntity<>(userCM, HttpStatus.OK);
        } else {
            throw this.errorUserService(ErrorCodeUserEnum.UNAUTHORIZED.getErrorCode());
        }
    }


    public ResponseEntity uploadFile(MultipartFile file, HttpServletRequest request) {

        String username = this.securityUtils.getUsernameFromToken(request);
        UserRegistration userRegistration = userRepository.findByUsername(username);
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        logger.info("Orginal file name {} ", fileName);
        String currentDate = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        fileName = currentDate + "-" + fileName;

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                return new ResponseEntity("Sorry! Filename contains invalid path sequence " + fileName, HttpStatus.BAD_REQUEST);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = fileStorageLocation.resolve(fileName);
            logger.info("Target Location {} ",targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            logger.info("base path {} ",baseUrl);

            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromPath(baseUrl)
                    .scheme("https")
                    .path(userLogoDir)
                    .path(fileName)
                    .toUriString();

            fileDownloadUri=fileDownloadUri.replace("http:/","");

            logger.info("fileDownloadUri {} ", fileDownloadUri);
            MapperObjectCM<String, String> mapperObjectCM = new MapperObjectCM<>();
            Map<String, String> map = new HashMap<>();
            mapperObjectCM.setMapper(map);
            map.put("photourl", fileDownloadUri);
            userRegistration.setPhoto(fileDownloadUri);
            userRepository.save(userRegistration);
            return new ResponseEntity(mapperObjectCM, HttpStatus.OK);

        } catch (IOException ex) {
            logger.error("Error :: {} and {}", ex.getMessage(), ex);
            return new ResponseEntity("Could not store file " + fileName + ". Please try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
