package com.softedge.solution.controller;

import com.softedge.solution.contractmodels.StateCM;
import com.softedge.solution.contractmodels.UserCM;
import com.softedge.solution.service.CertusUserDetailsService;
import com.softedge.solution.service.CompanyService;
import com.softedge.solution.service.CountryService;
import com.softedge.solution.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
public class UserController extends BaseController {

    @Autowired
    private Environment environment;

    @Autowired
    private CertusUserDetailsService certusUserDetailsService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private StateService stateService;

    @Autowired
    CompanyService companyService;


    @PostMapping("/user/user-profile-info")
    public ResponseEntity<?> saveUser(@RequestBody UserCM userCM, HttpServletRequest request) throws Exception {
        return certusUserDetailsService.saveUserDetails(userCM, request);
    }

    @PutMapping("/user/user-profile-info")
    public ResponseEntity<?> updateUser(@RequestBody UserCM userCM, HttpServletRequest request) throws Exception {
        return certusUserDetailsService.saveUserDetails(userCM, request);
    }

    @GetMapping("/user/user-profile-info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request, @RequestParam(value="user-id",required = false) Long userId) {
        if (userId == null) {
            return certusUserDetailsService.getCurrentUserDetails(request);
        } else {
            return certusUserDetailsService.getCurrentUserDetails(request, userId);
        }
    }

    @GetMapping("/user/countries")
    public ResponseEntity<?> getCountries() {
        return countryService.getCountries();
    }

    @GetMapping("/user/state")
    public ResponseEntity<?> getStateByCountryCode(@RequestParam("country-code") String countryCode) {
        return stateService.getStateByCountryCode(countryCode);
    }

    @GetMapping("/user/state-id")
    public ResponseEntity<?> getStateById(@RequestParam("id") Long id) {
        StateCM stateCM = stateService.getStateByStateId(id);
        return new ResponseEntity<>(stateCM, HttpStatus.OK);
    }

    @GetMapping("/user/session")
    public ResponseEntity<?> userSessionInfo(HttpServletRequest request) {
        return certusUserDetailsService.getUserSessionInfo(request);
    }

    @GetMapping("/user/load-country")
    public ResponseEntity<?> loadCountryDetails() throws URISyntaxException, IOException {
        return countryService.loadCountries();
    }


    @PostMapping("/user/user-image")
    public ResponseEntity<?> fileUpload(@RequestParam("photoImage") MultipartFile file, HttpServletRequest request) {
        return certusUserDetailsService.uploadFile(file, request);
    }

    @PutMapping("/user/user-image")
    public ResponseEntity<?> updateFileUpload(@RequestParam("photoImage") MultipartFile file, HttpServletRequest request) {
        return certusUserDetailsService.uploadFile(file, request);
    }

    @GetMapping("/user/greet")
    public String greet() {
        return "greet " + environment.getProperty("local.server.port");
    }

    @GetMapping("/user/wish")
    public String wish() {
        return "wish " + environment.getProperty("local.server.port");
    }

    @GetMapping("/company-info/{id}")
    public ResponseEntity<?> getCompany(@PathVariable Long id, HttpServletRequest request) {
        return companyService.getCompanyById(id, request);
    }

}
