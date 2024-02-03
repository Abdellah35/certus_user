package com.softedge.solution.service;

import com.softedge.solution.contractmodels.ActivationUserCM;
import com.softedge.solution.contractmodels.AuthenticateUserCM;
import com.softedge.solution.contractmodels.ForgotPasswordCM;
import com.softedge.solution.contractmodels.ResetPasswordUserCM;
import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.feignbeans.UserIPVActivation;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface CertusUserRegistrationService {

    UserIPVActivation createUser(UserIPVActivation userIPVActivation) throws UserAccountModuleException, SecurityException;

    ResponseEntity<?> activate(ActivationUserCM activationUserCM);

    ResponseEntity<?> resetPassword(HttpServletRequest request, ResetPasswordUserCM resetPasswordUserCM) throws UserAccountModuleException;

    ResponseEntity<?> authenticateUser(AuthenticateUserCM authenticateUserCM) throws UserAccountModuleException;

    UserIPVActivation forgotPassword(ForgotPasswordCM forgotPasswordCM);

    ResponseEntity<?> validateUserOtp(ActivationUserCM activationUserCM);

    ResponseEntity<?> resendOtp(ActivationUserCM activationUserCM) throws UserAccountModuleException;
}
