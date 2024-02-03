package com.softedge.solution.service.impl;

import com.softedge.solution.commons.CommonUtilities;
import com.softedge.solution.contractmodels.DigitalIPVCM;
import com.softedge.solution.contractmodels.MapperObjectCM;
import com.softedge.solution.enuminfo.IPVVerificationEnum;
import com.softedge.solution.exceptionhandlers.custom.Utilities.FileStorageException;
import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.exceptionhandlers.custom.user.UserServiceModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import com.softedge.solution.repomodels.DigitalIPV;
import com.softedge.solution.repomodels.UserRegistration;
import com.softedge.solution.repository.impl.DigitalIPVRepositoryImpl;
import com.softedge.solution.service.certusabstractservice.CertusAbstractDigitalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
import java.util.Map;
import java.util.Optional;

@Service(value = "certusDigitalIPVService")
public class CertusDigitalIPVServiceImpl extends CertusAbstractDigitalService {
    private final Logger logger = LoggerFactory.getLogger(CertusDigitalIPVServiceImpl.class);


    @Value("${digitalipv.relativedir}")
    private String imageDir;

    @Autowired
    private DigitalIPVRepositoryImpl digitalIPVRepositoryImpl;

    @Value("${base.url}")
    private String baseUrl;

    Path fileStorageLocation = Paths.get("/usr/share/nginx/html/certus-user-service/digital-ipv/").toAbsolutePath().normalize();

    @Override
    public ResponseEntity<MapperObjectCM> getIPVCode(HttpServletRequest request) throws UserAccountModuleException, UserServiceModuleException, FileStorageException {
        DigitalIPV digitalIPV = null;
        try {
//            TODO: SecurityUtils get current login
            String username = this.securityUtils.getUsernameFromToken(request);
            UserRegistration userRegistration = userRepository.findByUsername(username);
            digitalIPV = digitalIPVRepository.findByUser(userRegistration);
            int randomNubmer = CommonUtilities.random4DigitNumberGenerator();
            if (digitalIPV == null) {
                digitalIPV = new DigitalIPV();
            }
            digitalIPV.setIpvCode(randomNubmer);
            digitalIPV.setUser(userRegistration);
            digitalIPV.setUserRequestRaisedTime(new Date());
            if (digitalIPV.getUserIPVImage() != null) {
                digitalIPV.setVerificationStatus(IPVVerificationEnum.IN_PROGRESS.getValue());
            } else {
                digitalIPV.setVerificationStatus(IPVVerificationEnum.PENDING.getValue());
            }
            digitalIPV = digitalIPVRepository.save(digitalIPV);
            if (digitalIPV != null) {
                MapperObjectCM mapperObjectCM = new MapperObjectCM();
                Map<String, Integer> map = new HashMap();
                map.put("ipvCode", digitalIPV.getIpvCode());
                mapperObjectCM.setMapper(map);
                return new ResponseEntity(mapperObjectCM, HttpStatus.OK);
            } else {
                throw this.errorDigitalIPVService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode());
            }
        } catch (Exception e) {
            throw this.errorDigitalIPVService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<?> saveDigitalIPVImage(MultipartFile file, HttpServletRequest request) throws UserAccountModuleException, UserServiceModuleException, FileStorageException {
        DigitalIPV digitalIPV = new DigitalIPV();
        String username = this.securityUtils.getUsernameFromToken(request);
        UserRegistration userRegistration = userRepository.findByUsername(username);
        digitalIPV = digitalIPVRepository.findByUser(userRegistration);
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
            logger.info("Target Location {} ", targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            logger.info("base path {} ", baseUrl);

            String fileDownloadUri = ServletUriComponentsBuilder.
                    fromPath(baseUrl)
                    .scheme("https")
                    .path(imageDir)
                    .path(fileName)
                    .toUriString();
            fileDownloadUri=fileDownloadUri.replace("http:/","");

            logger.info("fileDownloadUri {} ", fileDownloadUri);
            userRegistration.setIpvCompleted(true);
            digitalIPV.setUser(userRegistration);
            digitalIPV.setVerificationStatus(IPVVerificationEnum.IN_PROGRESS.getValue());
            digitalIPV.setUserIPVImage(fileDownloadUri);
            digitalIPV.setUserRequestModifiedTime(new Date());
            digitalIPV.setFileName(fileName);
            digitalIPV.setFileSize(file.getSize());
            digitalIPV.setFileType(file.getContentType());
            try {
                digitalIPV = digitalIPVRepository.save(digitalIPV);
            } catch (Exception e) {
                throw this.errorDigitalIPVService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
            }
            MapperObjectCM<String, String> mapperObjectCM = new MapperObjectCM<>();
            Map<String, String> map = new HashMap<>();
            mapperObjectCM.setMapper(map);
            map.put("verificationStatus", digitalIPV.getVerificationStatus());
            return new ResponseEntity<>(mapperObjectCM, HttpStatus.OK);

        } catch (IOException ex) {
            return new ResponseEntity("Could not store file " + fileName + ". Please try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<?> getIPVinfo(Long userId, HttpServletRequest httpServletRequest) throws UserAccountModuleException, UserServiceModuleException, FileStorageException {
        DigitalIPVCM digitalIPVCM = new DigitalIPVCM();
        try {
            Optional<UserRegistration> userRegistrationOptional = userRepository.findById(userId);
            DigitalIPV digitalIPV = digitalIPVRepository.findByUser(userRegistrationOptional.get());
            digitalIPVCM.setAuditVerifiedTime(digitalIPV.getAuditVerifiedTime());
            digitalIPVCM.setIpvCode(digitalIPV.getIpvCode());
            digitalIPVCM.setUserIPVImage(digitalIPV.getUserIPVImage());
            digitalIPVCM.setUserRequestModifiedTime(digitalIPV.getUserRequestModifiedTime());
            digitalIPVCM.setUserRequestRaisedTime(digitalIPV.getUserRequestRaisedTime());
            digitalIPVCM.setVerificationStatus(digitalIPV.getVerificationStatus());
            return new ResponseEntity<>(digitalIPVCM, HttpStatus.OK);
        } catch (Exception e) {
            throw this.errorDigitalIPVService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<?> approvalDigitalIpv(HttpServletRequest request, Long userId, String verificationStatus) throws UserServiceModuleException, UserAccountModuleException, FileStorageException {
        Long status = null;
        if (userId != null && verificationStatus != null) {
            try {
                if (verificationStatus.equalsIgnoreCase("Accepted")) {
                    status = digitalIPVRepositoryImpl.updateDigitalIPVDetails(userId, IPVVerificationEnum.ACCEPTED.getValue());
                } else if (verificationStatus.equalsIgnoreCase("Rejected")) {
                    status = digitalIPVRepositoryImpl.updateDigitalIPVDetails(userId, IPVVerificationEnum.REJECTED.getValue());
                } else {
                    throw this.errorDigitalIPVService(ErrorCodeUserEnum.INVALID_FORM.getErrorCode());
                }
                } catch (Exception e) {
                throw this.errorDigitalIPVService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
                }
            } else {
            throw this.errorDigitalIPVService(ErrorCodeUserEnum.INVALID_FORM.getErrorCode());
            }
        MapperObjectCM mapperObjectCM = new MapperObjectCM();
        Map<String, String> map = new HashMap();
        if (status != null && status > 0) {
            map.put("status", "Success");
            mapperObjectCM.setMapper(map);
            return new ResponseEntity<>(mapperObjectCM, HttpStatus.OK);
        } else {
            map.put("status", "Failure");
            mapperObjectCM.setMapper(map);
            return new ResponseEntity<>(mapperObjectCM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
