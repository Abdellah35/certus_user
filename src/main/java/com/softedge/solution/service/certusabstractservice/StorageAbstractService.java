package com.softedge.solution.service.certusabstractservice;

import com.softedge.solution.commons.FileCommonUtility;
import com.softedge.solution.exceptionhandlers.custom.Utilities.FileStorageException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUtilityEnum;
import com.softedge.solution.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class StorageAbstractService implements StorageService {

    private final Logger logger = LoggerFactory.getLogger(StorageAbstractService.class);

    private Path fileStorageLocation;

    @Override
    public String storeFile(MultipartFile file) throws FileStorageException {
        try {
            String filename = FileCommonUtility.storeFile(file, this.fileStorageLocation);
            return filename;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("File Storage Exception :: {}", e);
            throw new FileStorageException(ErrorCodeUtilityEnum.FILE_STORE_FAILED, ErrorCodeUtilityEnum.FILE_STORE_FAILED.getName());
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Resource resource = FileCommonUtility.loadFileAsResource(fileName, this.fileStorageLocation);
            return resource;
        } catch (FileStorageException e) {
            logger.error(e.getMessage());
            logger.error("File Storage Exception :: {}", e);
            throw new FileStorageException(ErrorCodeUtilityEnum.FILE_STORE_FAILED, ErrorCodeUtilityEnum.FILE_STORE_FAILED.getName());
        }
    }

    @Override
    public void removeFile(String filename) {

    }


    protected void utilFile(String imageDir) {
        this.fileStorageLocation = Paths.get(imageDir)
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            logger.error("Exception :: {}", e);
            throw new FileStorageException(ErrorCodeUtilityEnum.FILE_STORE_FAILED, ErrorCodeUtilityEnum.FILE_STORE_FAILED.getName());
        }
    }
}
