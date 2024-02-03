package com.softedge.solution.security.util;

import com.softedge.solution.contractmodels.AuthenticateUserCM;
import com.softedge.solution.contractmodels.ResetPasswordUserCM;
import com.softedge.solution.exceptionhandlers.custom.user.UserAccountModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeUserEnum;
import com.softedge.solution.repomodels.UserRegistration;
import com.softedge.solution.repository.impl.UserDetailsRepositoryImpl;
import com.softedge.solution.security.commons.JwtAuthenticationConfig;
import com.softedge.solution.security.models.AuthenticationResponse;
import com.softedge.solution.security.models.Authorities;
import com.softedge.solution.security.models.PasswordResetResponse;
import com.softedge.solution.service.CertusUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class RegistrationUtility {




    @Autowired
    JwtAuthenticationConfig config;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    private CertusUserDetailsService userDetailsService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    protected UserDetailsRepositoryImpl userDetailsRepositoryImpl;



    public ResponseEntity<?> processRegister(@RequestBody UserRegistration userRegistrationObject) {

        List<Authorities> authorities = new ArrayList<Authorities>();

        Authorities authority = new Authorities();
        authority.setUsername(userRegistrationObject.getUsername());
        authority.setAuthority("ROLE_USER");

        authorities.add(authority);
        /* authorities.add(new SimpleGrantedAuthority("ROLE_USER"));


        User user = new User(userRegistrationObject.getUsername(), null, authorities);


        jdbcUserDetailsManager.createUser(user);*/

        userRegistrationObject.setAuthorities(authorities);

        userDetailsService.addUser(userRegistrationObject);

        Instant now = Instant.now();
        String token = Jwts.builder()
                .setSubject(userRegistrationObject.getUsername())
                .claim("authorities",authorities.stream()
                       .map(Authorities::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(config.getExpiration())))
                .signWith(SignatureAlgorithm
                        .HS256, config.getSecret().getBytes())
                .compact();


        return ResponseEntity.ok(new AuthenticationResponse(token));

    }

    public ResponseEntity<?> getJwtForExistingUser(@RequestBody UserRegistration userRegistrationObject) {
        List<Authorities> authorities = userRegistrationObject.getAuthorities();

        Instant now = Instant.now();
        String token = Jwts.builder()
                .setSubject(userRegistrationObject.getUsername())
                .claim("authorities",authorities.stream()
                        .map(Authorities::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(config.getExpiration())))
                .signWith(SignatureAlgorithm
                        .HS256, config.getSecret().getBytes())
                .compact();


        return ResponseEntity.ok(new AuthenticationResponse(token));

    }


    public ResponseEntity<?> resetPassword(HttpServletRequest request, ResetPasswordUserCM resetPasswordUserCM) throws UserAccountModuleException {
        String username = this.securityUtils.getUsernameFromToken(request);
        // UserDetails user = userDetailsRepositoryImpl.getUserByUsername(username);
        UserRegistration registeredUser = userDetailsService.getUserByUsername(username);
        if (registeredUser != null) {
            String password = resetPasswordUserCM.getPassword();
            if (password != null && !StringUtils.isEmpty(password)) {
                boolean valid = PasswordValidator.isValid(password);
                if (valid) {
                    registeredUser.setPassword(passwordEncoder.encode(password));
                    registeredUser.setForcePasswordChange(true);
                    userDetailsService.addUser(registeredUser);
                    return ResponseEntity.ok(new PasswordResetResponse("Password Reset Success"));
                }
                else{
                    throw new UserAccountModuleException(ErrorCodeUserEnum.PASSWORD_RULE_MISMATCH, ErrorCodeUserEnum.PASSWORD_RULE_MISMATCH.getName());
                }
            }
            else{
                throw new UserAccountModuleException(ErrorCodeUserEnum.PASSWORD_EMPTY, ErrorCodeUserEnum.PASSWORD_EMPTY.getName());
            }
        } else {
            throw new UserAccountModuleException(ErrorCodeUserEnum.PASSWORD_RESET_FAILED, ErrorCodeUserEnum.PASSWORD_RESET_FAILED.getName());
        }
    }

    public ResponseEntity<?> authenticateUser(AuthenticateUserCM authenticateUserCM) throws UserAccountModuleException {
        UserRegistration registeredUser = userDetailsService.getUserByUsername(authenticateUserCM.getUsername());
        if (registeredUser != null) {
            if (passwordEncoder.matches(authenticateUserCM.getPassword(), registeredUser.getPassword())) {
                return this.generateToken(registeredUser);
            } else {
                return new ResponseEntity("Authentication failure", HttpStatus.UNAUTHORIZED);
            }
        } else {
            throw new UserAccountModuleException(ErrorCodeUserEnum.PASSWORD_RESET_FAILED, ErrorCodeUserEnum.PASSWORD_RESET_FAILED.getName());
        }
    }

    private ResponseEntity<?> generateToken(UserRegistration userRegistration) {
        Instant now = Instant.now();
        String token = Jwts.builder()
                .setSubject(userRegistration.getUsername())
                .claim("authorities", userRegistration.getAuthorities().stream()
                        .map(Authorities::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(config.getExpiration())))
                .signWith(SignatureAlgorithm
                        .HS256, config.getSecret().getBytes())
                .compact();

        return ResponseEntity.ok(new AuthenticationResponse(token));

    }
}
