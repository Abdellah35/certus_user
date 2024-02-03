package com.softedge.solution.service.impl;

import com.softedge.solution.commons.CommonUtilities;
import com.softedge.solution.contractmodels.*;
import com.softedge.solution.enuminfo.CategoryEnum;
import com.softedge.solution.enuminfo.ProcessStatusEnum;
import com.softedge.solution.enuminfo.UserTypeEnum;
import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import com.softedge.solution.feignbeans.UserIPVActivation;
import com.softedge.solution.proxy.CertusRedisCacheServiceProxy;
import com.softedge.solution.repomodels.KycDocumentDetails;
import com.softedge.solution.repomodels.Mail;
import com.softedge.solution.repomodels.TempUser;
import com.softedge.solution.repomodels.UserRegistration;
import com.softedge.solution.repository.UserRepository;
import com.softedge.solution.repository.impl.KycRepositoryImpl;
import com.softedge.solution.repository.impl.TempUserRepositoryImpl;
import com.softedge.solution.security.util.RegistrationUtility;
import com.softedge.solution.service.CertusUserDetailsService;
import com.softedge.solution.service.certusabstractservice.CertusAbstractUserRegistrationService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CertusUserRegistrationServiceImpl extends CertusAbstractUserRegistrationService {

    private final Logger logger = LoggerFactory.getLogger(CertusUserRegistrationServiceImpl.class);

    @Autowired
    CertusUserDetailsService certusUserDetailsService;

    @Autowired
    private TempUserRepositoryImpl tempUserRepository;

    @Autowired
    private KycRepositoryImpl kycRepository;

    @Autowired
    protected UserRepository userRepository;


    @Autowired
    private CertusRedisCacheServiceProxy certusRedisCacheServiceProxy;

    @Autowired
    private RegistrationUtility registrationUtility;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;


    @Value("${certus-mailbox.certus-bcc-list}")
    private String certusBccList;


    @Override
    public UserIPVActivation createUser(UserIPVActivation userIPVActivation) throws UserAccountModuleException {
        if (userIPVActivation != null && StringUtils.isNotEmpty(userIPVActivation.getEmailId())) {
            UserRegistration userRegistration = certusUserDetailsService.getUserByUsername(userIPVActivation.getEmailId());
            if (userRegistration == null || !userRegistration.getUsername().equalsIgnoreCase(userIPVActivation.getEmailId())) {
                userIPVActivation.setActivationCode(CommonUtilities.random6DigitNumberGenerator());
                logger.info("The user is {}", userIPVActivation);
                sendEmailTrigger(userIPVActivation);
                //Validation
                if (StringUtils.isEmpty(userIPVActivation.getUserType())) {
                    userIPVActivation.setUserType(UserTypeEnum.Individual.getValue());
                }
           /*     if (!userIPVActivation.getUserType().equalsIgnoreCase(UserTypeEnum.Company.getValue()) || !userIPVActivation.getUserType().equalsIgnoreCase(UserTypeEnum.Individual.getValue())) {
                    userIPVActivation.setUserType(UserTypeEnum.Individual.getValue());
                }*/
                UserIPVActivation userActivation = certusRedisCacheServiceProxy.add(userIPVActivation);
                userActivation.setActivationCode(null);
                return userActivation;
            } else {
                throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_ID_EXISTS.getErrorCode());
            }
        }
        throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_ID_EMPTY.getErrorCode());
    }


    public void sendEmailTrigger(UserIPVActivation userIPVActivation){
        try {

            String mailTemplate = "otp-email-template";
            String email = userIPVActivation.getEmailId();

            logger.info("The email id of the user for whom we are send mail is -> {}", email);
                logger.info("Sending Email Template for the corporate share");

                Mail mail = new Mail();
                mail.setFrom("sales@softedgesolution.com");
                mail.setTo(userIPVActivation.getEmailId());

                String[] bccList = CommonUtilities.getStringArrayFromCommaSeparatedString(certusBccList);
                mail.setBcc(bccList);
                mail.setSubject("OTP for Certus User Account Activation");
                Map model = new HashMap();
                model.put("otp",userIPVActivation.getActivationCode());
                model.put("otp_registration_url", "https://certussecure.softedgesolution.com/welcome/registor");
                mail.setModel(model);
                sendSimpleMessage(mailTemplate,mail);
                logger.info("Mail sent to the user -> {}",email);

        }catch (MessagingException e){
            logger.error(e.getMessage());
        }catch (IOException e){
            logger.error(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }


    private void sendSimpleMessage(String mailTemplate, Mail mail) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        String html = templateEngine.process(mailTemplate, context);
        helper.setTo(mail.getTo());
        //helper.setCc(mail.getCc());
        helper.setBcc(mail.getBcc());
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());
        logger.info("The mail message is -> ",message);
        emailSender.send(message);

    }

    @Override
    public ResponseEntity<?> activate(ActivationUserCM activationUserCM) throws UserAccountModuleException{
        try {
            if (activationUserCM != null && StringUtils.isNotEmpty(activationUserCM.getEmailId())) {

                TempUser tempUserEmailId = tempUserRepository.getTempUserByEmailId(activationUserCM.getEmailId());
                if(tempUserEmailId==null){
                    UserIPVActivation cachedUser = certusRedisCacheServiceProxy.findById(activationUserCM.getEmailId());
                    logger.info("User from userFrom redis {} ", cachedUser);
                    logger.info("Requested User {} ", activationUserCM);
                    if (cachedUser != null && cachedUser.getActivationCode().equals(activationUserCM.getOtp())) {
                        UserRegistration userRegistration = new UserRegistration();
                        userRegistration.setUsername(cachedUser.getEmailId());
                        userRegistration.setName(cachedUser.getUsername());
                        userRegistration.setEnabled(true);
                        userRegistration.setCategory(CategoryEnum.USER.getValue());
                        userRegistration.setCreatedBy("admin");
                        userRegistration.setCreatedDate(new Date());
                        userRegistration.setForcePasswordChange(false);
                        userRegistration.setIpvCompleted(false);
                        userRegistration.setProfileCompleted(false);
                        userRegistration.setUserType(cachedUser.getUserType());
                        return registrationUtility.processRegister(userRegistration);
                    } else {
                        throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_ACTIVATION_OTP.getErrorCode());
                    }
                }
                else if(tempUserEmailId!=null){
                    Long id = tempUserEmailId.getId();
                    List<Long> requesteeIds = new ArrayList<>();
                    requesteeIds.add(id);
                    List<KycProcessDocumentDetailsCM> kycProcessDocumentDetailsCMS = kycRepository.tempKycDetailsByRequesteeId(requesteeIds);
                    if(kycProcessDocumentDetailsCMS.size()==0){
                        UserIPVActivation cachedUser = certusRedisCacheServiceProxy.findById(activationUserCM.getEmailId());
                        logger.info("User from userFrom redis {} ", cachedUser);
                        logger.info("Requested User {} ", activationUserCM);
                        if (cachedUser != null && cachedUser.getActivationCode().equals(activationUserCM.getOtp())) {
                            UserRegistration userRegistration = new UserRegistration();
                            userRegistration.setUsername(cachedUser.getEmailId());
                            userRegistration.setName(cachedUser.getUsername());
                            userRegistration.setEnabled(true);
                            userRegistration.setCategory(CategoryEnum.USER.getValue());
                            userRegistration.setCreatedBy("admin");
                            userRegistration.setCreatedDate(new Date());
                            userRegistration.setForcePasswordChange(false);
                            userRegistration.setIpvCompleted(false);
                            userRegistration.setProfileCompleted(false);
                            return registrationUtility.processRegister(userRegistration);
                        } else {
                            throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_ACTIVATION_OTP.getErrorCode());
                        }
                    }
                    else if(kycProcessDocumentDetailsCMS.size()>0){

                        UserIPVActivation cachedUser = certusRedisCacheServiceProxy.findById(activationUserCM.getEmailId());
                        logger.info("User from userFrom redis {} ", cachedUser);
                        logger.info("Requested User {} ", activationUserCM);
                        if (cachedUser != null && cachedUser.getActivationCode().equals(activationUserCM.getOtp())) {
                            UserRegistration userRegistration = new UserRegistration();
                            userRegistration.setUsername(cachedUser.getEmailId());
                            userRegistration.setName(cachedUser.getUsername());
                            userRegistration.setEnabled(true);
                            userRegistration.setCategory(CategoryEnum.USER.getValue());
                            userRegistration.setCreatedBy("admin");
                            userRegistration.setCreatedDate(new Date());
                            userRegistration.setForcePasswordChange(false);
                            userRegistration.setIpvCompleted(false);
                            userRegistration.setUserType(cachedUser.getUserType());
                            userRegistration.setProfileCompleted(false);
                            ResponseEntity<?> responseEntity = registrationUtility.processRegister(userRegistration);
                            UserRegistration registeredUser = userRepository.findByUsername(activationUserCM.getEmailId());
                            Long registeredRequesteeId = registeredUser.getId();
                            kycRepository.updateUserKycStatusRegistration(tempUserEmailId.getId(), registeredRequesteeId);
                            kycProcessDocumentDetailsCMS.forEach(kycProcessDocumentDetailsCM -> {
                                KycDocumentDetails kycDocumentDetails = new KycDocumentDetails();
                                kycDocumentDetails.setCompanyId(kycProcessDocumentDetailsCM.getCompanyId());
                                kycDocumentDetails.setRequestorUserId(kycProcessDocumentDetailsCM.getRequestorUserId());
                                kycDocumentDetails.setRequesteeUserId(registeredRequesteeId);
                                kycDocumentDetails.setDocumentId(kycProcessDocumentDetailsCM.getDocumentId());
                                kycDocumentDetails.setProcessStatus(ProcessStatusEnum.REQUESTED.getValue());
                                kycDocumentDetails.setCreatedDate(new Date());
                                kycDocumentDetails.setCreatedBy(activationUserCM.getEmailId());
                                kycDocumentDetails.setModifiedDate(new Date());
                                kycDocumentDetails.setModifiedBy(activationUserCM.getEmailId());

                                kycRepository.requestKycDocumentSave(kycDocumentDetails);

                            });

                            kycRepository.deleteTempKyc(id);
                            tempUserRepository.deleteTempUser(id);
                            return responseEntity;

                        } else {
                            throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_ACTIVATION_OTP.getErrorCode());
                        }

                    }
                }




            }
            throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_ACTIVATION_NULL.getErrorCode());
        }
        catch (UserAccountModuleException e){
            throw e;
        }
        catch (Exception e) {
            throw this.errorUserRegistrationService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<?> resetPassword(HttpServletRequest request, ResetPasswordUserCM resetPasswordUserCM) throws UserAccountModuleException {
        try {
            if (resetPasswordUserCM != null && StringUtils.isNotEmpty(resetPasswordUserCM.getEmailId()) && StringUtils.isNotEmpty(resetPasswordUserCM.getPassword())) {
                return registrationUtility.resetPassword(request,resetPasswordUserCM);
            }
            throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_PASSWORD_EMPTY.getErrorCode());
        }
        catch (UserAccountModuleException e){
            throw e;
        }
        catch (Exception e) {
            throw this.errorUserRegistrationService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<?> authenticateUser(AuthenticateUserCM authenticateUserCM) throws UserAccountModuleException {
        try {
            if (authenticateUserCM != null && StringUtils.isNotEmpty(authenticateUserCM.getUsername()) && StringUtils.isNotEmpty(authenticateUserCM.getPassword())) {
                return registrationUtility.authenticateUser(authenticateUserCM);
            }
            throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_PASSWORD_EMPTY.getErrorCode());
        }
        catch (UserAccountModuleException e){
            throw e;
        }
        catch (Exception e) {
            throw this.errorUserRegistrationService(ErrorCodeUserEnum.UNAUTHORIZED.getErrorCode(), e);
        }
    }

    @Override
    public UserIPVActivation forgotPassword(ForgotPasswordCM forgotPasswordCM) throws UserAccountModuleException {
        String emailId = forgotPasswordCM.getEmailId();
        if (emailId != null && StringUtils.isNotEmpty(emailId)) {
            UserRegistration userRegistration = certusUserDetailsService.getUserByUsername(emailId);
            if (userRegistration != null && userRegistration.getUsername().equalsIgnoreCase(emailId)) {
                UserIPVActivation userIPVActivation = new UserIPVActivation();
                userIPVActivation.setActivationCode(CommonUtilities.random6DigitNumberGenerator());
                userIPVActivation.setEmailId(emailId);
                userIPVActivation.setUsername(userRegistration.getName());
                logger.info("The user is {}", userIPVActivation);
                sendEmailTrigger(userIPVActivation);
                return certusRedisCacheServiceProxy.add(userIPVActivation);
            } else {
                throw this.errorUserRegistrationService(ErrorCodeUserEnum.EMAIL_ID_INVALID.getErrorCode());
            }
        }
        throw this.errorUserRegistrationService(ErrorCodeUserEnum.EMAIL_ID_INVALID.getErrorCode());
    }

    @Override
    public ResponseEntity<?> validateUserOtp(ActivationUserCM activationUserCM) throws UserAccountModuleException{
        try {
            if (activationUserCM != null && StringUtils.isNotEmpty(activationUserCM.getEmailId())) {
                UserIPVActivation cachedUser = certusRedisCacheServiceProxy.findById(activationUserCM.getEmailId());
                logger.info("User from userFrom redis {} ", cachedUser);
                logger.info("Requested User {} ", activationUserCM);
                if (cachedUser != null && cachedUser.getActivationCode().equals(activationUserCM.getOtp())) {
                    UserRegistration userRegistration = certusUserDetailsService.getUserByUsername(activationUserCM.getEmailId());
                    if (userRegistration != null) {
                        return registrationUtility.getJwtForExistingUser(userRegistration);
                    } else {
                        return this.activate(activationUserCM);
                    }
                } else {
                    throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_ACTIVATION_OTP.getErrorCode());
                }
            }
            throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_ACTIVATION_NULL.getErrorCode());
        }
        catch (UserAccountModuleException e){
            throw e;
        }
        catch (Exception e) {
            throw this.errorUserRegistrationService(ErrorCodeUserEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }

    }

    @Override
    public ResponseEntity<?> resendOtp(ActivationUserCM activationUserCM) throws UserAccountModuleException{
        try{
            if (activationUserCM != null && StringUtils.isNotEmpty(activationUserCM.getEmailId())) {
                UserIPVActivation cachedUser = certusRedisCacheServiceProxy.findById(activationUserCM.getEmailId());
                logger.info("User from userFrom redis {} ", cachedUser);
                logger.info("Requested User {} ", activationUserCM);
                UserIPVActivation userIPVActivation = new UserIPVActivation();
                userIPVActivation.setActivationCode(CommonUtilities.random6DigitNumberGenerator());
                userIPVActivation.setEmailId(cachedUser.getEmailId());
                userIPVActivation.setUsername(cachedUser.getUsername());
                userIPVActivation.setUserType(cachedUser.getUserType());
                certusRedisCacheServiceProxy.add(userIPVActivation);
                sendEmailTrigger(userIPVActivation);
                return ResponseEntity.ok(userIPVActivation);
            } else {
                throw this.errorUserRegistrationService(ErrorCodeUserEnum.EMAIL_ID_INVALID.getErrorCode());
            }
        }
        catch(UserAccountModuleException e){
            throw this.errorUserRegistrationService(ErrorCodeUserEnum.USER_ACTIVATION_OTP.getErrorCode());
        }
        catch (Exception e){
            throw this.errorUserRegistrationService(ErrorCodeUserEnum.EMAIL_ID_INVALID.getErrorCode(), e);
        }
    }
}