package com.softedge.solution.controller;

import com.softedge.solution.contractmodels.ClientCM;
import com.softedge.solution.contractmodels.CompanyCM;
import com.softedge.solution.service.CertusClientService;
import com.softedge.solution.service.CertusDigitalIPVService;
import com.softedge.solution.service.CertusUserDetailsService;
import com.softedge.solution.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/admin")
public class AdminController extends BaseController {

    @Autowired
    CompanyService companyService;

    @Autowired
    CertusClientService clientService;

    @Autowired
    CertusUserDetailsService certusUserDetailsService;

    @Autowired
    CertusClientService certusClientService;

    @Autowired
    private CertusDigitalIPVService certusDigitalIPVService;

    @PostMapping("/save-company")
    public ResponseEntity<?> saveCompany(@RequestBody CompanyCM companyCM, HttpServletRequest request) {
        return companyService.addCompany(companyCM, request);
    }

    @PostMapping("/save-client")
    public ResponseEntity<?> saveClient(@RequestBody ClientCM clientCM, HttpServletRequest request) {
      return clientService.addUser(clientCM);
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<?> getClient(@PathVariable  Long id, HttpServletRequest request)  {
        return clientService.getUserClient(id,request);
    }

    @GetMapping("/client")
    public ResponseEntity<?> getAllClients( HttpServletRequest request)  {
        return clientService.getAllClients(request);
    }

    @GetMapping("/company")
    public ResponseEntity<?> getAllCompanies( HttpServletRequest request) {
        return companyService.getAllCompanies(request);
    }

    @PutMapping("/save-client")
    public ResponseEntity<?> updateClient(@RequestBody ClientCM clientCM, HttpServletRequest request) {
        return clientService.updateUserClient(clientCM,request);
    }

    @DeleteMapping("/client/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable  Long id, HttpServletRequest request) {
        return clientService.deleteUserClient(id,request);
    }

    @GetMapping("/company/{id}")
    public ResponseEntity<?> getCompany(@PathVariable  Long id, HttpServletRequest request)  {
        return companyService.getCompanyById(id,request);
    }

    @PutMapping("/save-company")
    public ResponseEntity<?> updateCompany(@RequestBody CompanyCM companyCM, HttpServletRequest request) {
        return companyService.updateCompany(companyCM,request);
    }


    @DeleteMapping("/company/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable  Long id, HttpServletRequest request){
        return companyService.deleteCompanyById(id,request);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        return certusUserDetailsService.getUsersSession(request);
    }

    @PutMapping("/user/verification")
    public ResponseEntity<?> digitalIpvApproval(HttpServletRequest request, @RequestParam("user-id") long userId,
                                                @RequestParam("verification-status") String verificationStatus) {
        return certusDigitalIPVService.approvalDigitalIpv(request, userId, verificationStatus);
    }

    @GetMapping("/dashboard/user-summary")
    public ResponseEntity<?> getAdminDashboardUserSummary(HttpServletRequest request) {
        return certusClientService.getAdminDashboardUsersCount(request);
    }

}
