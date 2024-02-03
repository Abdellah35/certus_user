package com.softedge.solution.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softedge.solution.commons.CommonUtilities;
import com.softedge.solution.repomodels.Attachments;
import com.softedge.solution.service.CertusDigitalIPVService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api/digital")
public class DigitalIPVController {

    private final Logger logger = LoggerFactory.getLogger(DigitalIPVController.class);
    @Autowired
    private CertusDigitalIPVService certusDigitalIPVService;

    @Value("${document.attachments}")
    private String documentAttachmentDir;

    @Value("${base.url}")
    private String baseUrl;

    @GetMapping("/ipv-pin")
    public ResponseEntity<?> getIPVPin(HttpServletRequest request) {
        return certusDigitalIPVService.getIPVCode(request);
    }

    @PostMapping("/ipv-info")
    @PutMapping("/ipv-info")
    public ResponseEntity<?> addIPVImage(@RequestParam("userIpvImage") MultipartFile file, HttpServletRequest request) {
        return certusDigitalIPVService.saveDigitalIPVImage(file, request);
    }

    @GetMapping("/ipv-info")
    public ResponseEntity<?> getIPVinfo(@RequestParam(name = "user-id", required = false) Long userId, HttpServletRequest request) {
        if (userId == null) {
            return certusDigitalIPVService.getIPVinfo(request);
        } else {
            return certusDigitalIPVService.getIPVinfo(userId, request);
        }
    }


    @PostMapping(value = "/documents", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> documents(@RequestBody Map<String, Object> objectMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (objectMap != null) {
            System.out.println("");
//            List<Attachments> attachments= (List<Attachments>) objectMap.get("attachments");
            List<Attachments> attachments = objectMapper.convertValue(objectMap.get("attachments"), new TypeReference<List<Attachments>>() {
            });
            for (Attachments attachment : attachments) {
                if (attachment.getFileAttachment() != null) {
                    logger.info("The file name is -> {} and {}", attachment.getFileAttachment());
                    byte[] documentImage = CommonUtilities.convertBase64EncodedImageToImageFile(attachment.getFileAttachment());
                    File f = new File(documentAttachmentDir);
                    System.out.println(f.toString());
                    String userDocumentsAttachmentsDirectory = null;
                    boolean userDocumentsAttachmentsExistsDirectory = false;

                    if (f.exists()) {
                        userDocumentsAttachmentsDirectory = f.toString() + File.separator + "document";
                        File f1 = new File(userDocumentsAttachmentsDirectory);
                        if (f1.exists()) {
                            userDocumentsAttachmentsExistsDirectory = true;
                        } else {
                            if (f1.mkdir()) {
                                System.out.println("user logo directory created..");
                                userDocumentsAttachmentsExistsDirectory = true;
                            }
                        }
                    } else {
                        if (f.mkdir()) {
                            userDocumentsAttachmentsDirectory = f.toString() + File.separator + "document";
                            File f1 = new File(userDocumentsAttachmentsDirectory);
                            if (f1.mkdir()) {
                                System.out.println("logo directory created..");
                                userDocumentsAttachmentsExistsDirectory = true;
                            }
                        }
                    }
                    // Appending timestamp image url
                    if (userDocumentsAttachmentsExistsDirectory) {
                        System.out.println("userPhotoDirectory---> " + userDocumentsAttachmentsDirectory);
                        try {
                            String imageUrl = userDocumentsAttachmentsDirectory + File.separator + attachment.getFileName() + "_" + new Date() + ".jpg";
                            FileOutputStream osf = new FileOutputStream(new File(imageUrl));
                            osf.write(documentImage);
                            osf.flush();
                            //Setting fle permission 644 in Aws Linux server
                            logger.info("Setting fle permission 644 in Aws Linux server");
//                            CommonUtility.setPermission(new File(userPhotoDirectory + File.separator + userDTO.getId() + "_" + userDTO.getImageUploadTime() + "_" + userDTO.getFirstName()+ userDTO.getLastName() + ".jpg"));
//                            System.out.println("file saved at ---> " + userPhotoDirectory + File.separator + userDTO.getId() + "_" + userDTO.getImageUploadTime() + "_" + userDTO.getFirstName()+ userDTO.getLastName() + ".jpg");
                            String url = baseUrl + imageUrl;
                            return new ResponseEntity<>(url, HttpStatus.OK);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return null;
    }

}
