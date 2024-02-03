package com.softedge.solution.controller;

import com.softedge.solution.contractmodels.*;
import com.softedge.solution.feignbeans.UserIPVActivation;
import com.softedge.solution.service.CertusUserRegistrationService;
import com.softedge.solution.service.TempUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserAccountController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(UserAccountController.class);

    @Autowired
    private CertusUserRegistrationService certusUserRegistrationService;

    @Autowired
    private TempUserService tempUserService;


    @PostMapping("/create-account")
    public UserIPVActivation add(@RequestBody UserIPVActivation userIPVActivation){
        return certusUserRegistrationService.createUser(userIPVActivation);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody ActivationUserCM activationUserCM){
        return certusUserRegistrationService.resendOtp(activationUserCM);
    }

    @PostMapping("/tmp-create-user")
    public ResponseEntity<?> addTempUser(@RequestBody TempUserCM tempUserCM, HttpServletRequest request){
        return tempUserService.addTempUser(tempUserCM, request);
    }

    // Generate OTP for forgot-password
    @PostMapping("/forgot-password")
    public UserIPVActivation forgotPassword(@RequestBody ForgotPasswordCM forgotPasswordCM){
        return certusUserRegistrationService.forgotPassword(forgotPasswordCM);
    }

    @PostMapping("/activate-account")
    public ResponseEntity<?> activate(@RequestBody ActivationUserCM activationUserCM) {
        return certusUserRegistrationService.activate(activationUserCM);
    }

    // Where user-otp at the time of forgot
    @PostMapping("/user-otp")
    public ResponseEntity<?> validateUserOtp(@RequestBody ActivationUserCM activationUserCM) {
        return certusUserRegistrationService.validateUserOtp(activationUserCM);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(HttpServletRequest request, @RequestBody ResetPasswordUserCM resetPasswordUserCM) {
        return certusUserRegistrationService.resetPassword(request, resetPasswordUserCM);

    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody AuthenticateUserCM authenticateUserCM) {
        return certusUserRegistrationService.authenticateUser(authenticateUserCM);
    }

    @GetMapping("/feign-client/redis/user-ipv")
    public String getIPVByEmailId(@RequestParam("email-id") String emailId) {
        logger.info("The emailid is :: {}", emailId);
        return "user obtained";
        // return certusRedisCacheServiceProxy.findById(emailId);
    }

}
