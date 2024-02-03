package com.softedge.solution.service.impl;

import com.softedge.solution.contractmodels.*;
import com.softedge.solution.enuminfo.CategoryEnum;
import com.softedge.solution.enuminfo.OrderByEnum;
import com.softedge.solution.enuminfo.SortEnum;
import com.softedge.solution.exceptionhandlers.client.ClientServiceModuleException;
import com.softedge.solution.exceptionhandlers.custom.bean.ClientException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeClientEnum;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeCompanyEnum;
import com.softedge.solution.repomodels.Client;
import com.softedge.solution.repomodels.UserRegistration;
import com.softedge.solution.repository.UserRepository;
import com.softedge.solution.repository.impl.ClientRepositoryImpl;
import com.softedge.solution.repository.impl.UserDetailsRepositoryImpl;
import com.softedge.solution.security.models.Authorities;
import com.softedge.solution.security.util.SecurityUtils;
import com.softedge.solution.service.CertusClientService;
import com.softedge.solution.service.certusabstractservice.CertusAbstractClientService;
import com.softedge.solution.service.helper.CertusClientServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CertusClientServiceImpl extends CertusAbstractClientService implements CertusClientService {

    Logger logger = LoggerFactory.getLogger(CertusClientServiceImpl.class);
    private static final String regexStringSpliter = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    @Value("${user.detail.data.header.name}")
    private String csvHeaderName;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ClientRepositoryImpl clientRepository;

    @Autowired
    SecurityUtils securityUtils;

    @Autowired
    UserDetailsRepositoryImpl userDetailsRepositoryImpl;

    @Autowired
    CertusClientServiceHelper certusClientServiceHelper;


    @Override
    public ResponseEntity<?> addUser(ClientCM clientCM) throws ClientException {

        Client client = new Client();
        try {
            client = this.clientValidationService(clientCM, client);
        } catch (Exception e) {
                throw e;
            }
        String username = clientCM.getEmailId();
        UserRegistration userRegistration = new UserRegistration();
        logger.info("Registering the client :: {}", username);
        Authorities authority = new Authorities();
        List<Authorities> authorities = new ArrayList<Authorities>();
        authority.setUsername(username);
        authority.setAuthority("ROLE_CLIENT");
        authorities.add(authority);
        userRegistration.setUsername(username);
        userRegistration.setName(clientCM.getName());
        userRegistration.setCategory(CategoryEnum.CLIENT.getValue());
        userRegistration.setPassword(passwordEncoder.encode(clientCM.getPassword()));
        userRegistration.setAuthorities(authorities);
        userRegistration.setCreatedBy(username);
        userRegistration.setCreatedDate(new Date());
        userRegistration.setModifiedBy(username);
        userRegistration.setEnabled(true);
        userRegistration.setModifiedDate(new Date());
        userRegistration.setIpvCompleted(true);
        UserRegistration savedClientObject = userRepository.save(userRegistration);
        Long userId = savedClientObject.getId();
        clientCM.setId(userId);
        client.setUserId(userId);
        client.setCreatedBy(username);
            client.setCreatedDate(new Date());
            client.setModifiedBy(username);
            client.setModifiedDate(new Date());
            clientRepository.mapUserCompany(client);
            clientCM.setCategory(userRegistration.getCategory());
            clientCM.setEmailId(userRegistration.getUsername());
            clientCM.setCompanyId(client.getCompanyId());
            clientCM.setUserType("Client");
            List<String> roles = new ArrayList<>();
            userRegistration.getAuthorities().forEach(auth->{
                String userAuth = auth.getAuthority();
                roles.add(userAuth);
            });
            clientCM.setRoles(roles);
            return ResponseEntity.ok(clientCM);

    }

    @Override
    public ResponseEntity<?> getUserClient(Long id, HttpServletRequest request) throws ClientException {
        try{
            Client client = clientRepository.getCompanyByUserId(id);
            UserRegistration user = userRepository.findById(id).get();
            ClientCM clientCM = new ClientCM();
            clientCM.setId(id);
            clientCM.setCompanyId(client.getCompanyId());
            clientCM.setEmailId(user.getUsername());
            clientCM.setName(user.getName());
            clientCM.setCategory(user.getCategory());
            List<String> roles = new ArrayList<>();
            user.getAuthorities().forEach(auth->{
                String authority = auth.getAuthority();
                roles.add(authority);
            });

            clientCM.setRoles(roles);
            return ResponseEntity.ok(clientCM);

        }
        catch (Exception e){
            throw new ClientException(ErrorCodeClientEnum.INTERNAL_SERVER_ERROR, ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR.getName());
        }

    }


    @Override
    public ResponseEntity<?> updateUserClient(ClientCM clientCM, HttpServletRequest request) throws ClientException {

            Client client = clientRepository.getCompanyByUserId(clientCM.getId());
            try {
                client = this.clientValidationService(clientCM, client);
            } catch (Exception e) {
                throw e;
            }
        String username = clientCM.getEmailId();
        UserRegistration userRegistration = userRepository.findById(clientCM.getId()).get();
        logger.info("Registering the client :: {}", username);
        Authorities authority = new Authorities();
        List<Authorities> authorities = new ArrayList<Authorities>();
        authority.setUsername(username);
        authority.setAuthority("ROLE_CLIENT");
        authorities.add(authority);
        userRegistration.setUsername(username);
        userRegistration.setName(clientCM.getName());
        userRegistration.setCategory(CategoryEnum.CLIENT.getValue());
        userRegistration.setPassword(passwordEncoder.encode(clientCM.getPassword()));
        userRegistration.setAuthorities(authorities);
        userRegistration.setCreatedBy(username);
        userRegistration.setCreatedDate(new Date());
        userRegistration.setModifiedBy(username);
        userRegistration.setModifiedDate(new Date());
        UserRegistration savedClientObject = userRepository.save(userRegistration);
        Long userId = savedClientObject.getId();
        clientCM.setId(userId);
        client.setUserId(userId);
        client.setCreatedBy(username);
            client.setCreatedDate(new Date());
            client.setModifiedBy(username);
            client.setModifiedDate(new Date());
            clientRepository.updateClientDetails(client);
            clientCM.setCategory(userRegistration.getCategory());
            clientCM.setEmailId(userRegistration.getUsername());
            clientCM.setCompanyId(client.getCompanyId());
            clientCM.setId(userId);
            List<String> roles = new ArrayList<>();
            userRegistration.getAuthorities().forEach(auth -> {
                String userAuth = auth.getAuthority();
                roles.add(userAuth);
            });
            clientCM.setRoles(roles);
            return ResponseEntity.ok(clientCM);


    }

    @Override
    public ResponseEntity<?> deleteUserClient(Long id, HttpServletRequest request) throws ClientException {
        try{
            clientRepository.deleteCompanyUserMappingById(id);
            userRepository.deleteById(id);
            return  ResponseEntity.ok("Client deleted with id "+id);
        }
        catch (Exception e){
            throw new ClientException(ErrorCodeClientEnum.INTERNAL_SERVER_ERROR, ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR.getName());
        }

    }

    @Override
    public ResponseEntity<?> getAllClients(HttpServletRequest request) throws ClientException {
        try {
            List<ClientCM> allClients = clientRepository.getAllClients();
            return ResponseEntity.ok(allClients);
        } catch (Exception e) {
            throw new ClientException(ErrorCodeClientEnum.INTERNAL_SERVER_ERROR, ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR.getName());
        }
    }

    @Override
    public ResponseEntity<?> getClientRequestedUsers(HttpServletRequest request, String search, int page, int results, String orderBy, String sort) throws ClientServiceModuleException {
        List<KycRequestedUserCM> kycRequestedUserCMS = new ArrayList<>();
        try {
            String username = this.securityUtils.getUsernameFromToken(request);
            UserRegistration userRegistration = userRepository.findByUsername(username);
            Client client = clientRepository.getCompanyByUserId(userRegistration.getId());
            String finalQuery = certusClientServiceHelper.getDynamicRequestedUserListSql(search);
            if (!orderBy.equalsIgnoreCase("name") || !orderBy.equalsIgnoreCase("emailId")) {
                orderBy = OrderByEnum.NAME.getValue();
            } else {
                if (orderBy.equalsIgnoreCase("name"))
                    orderBy = OrderByEnum.NAME.getValue();
                else if (orderBy.equalsIgnoreCase("emailId")) {
                    orderBy = OrderByEnum.EMAILID.getValue();
                }
            }
            if (!sort.equalsIgnoreCase("asc") || !sort.equalsIgnoreCase("desc")) {
                sort = SortEnum.ASCENDING.getValue();
            }
            kycRequestedUserCMS = clientRepository.getClientRequestedUsers(finalQuery, client.getCompanyId(), results, page, orderBy, sort);
            return new ResponseEntity<>(kycRequestedUserCMS, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(kycRequestedUserCMS, HttpStatus.OK);
        } catch (Exception e) {
            throw this.errorClientService(ErrorCodeClientEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<?> getClientRequestedUsersCount(HttpServletRequest request, String search) throws ClientServiceModuleException {
        Long clientRequestedUserCount = 0L;
        try {
            String username = this.securityUtils.getUsernameFromToken(request);
            UserRegistration userRegistration = userRepository.findByUsername(username);
            Client client = clientRepository.getCompanyByUserId(userRegistration.getId());
            String finalQuery = certusClientServiceHelper.getDynamicRequestedUserCountSql(search);
            clientRequestedUserCount = clientRepository.getClientRequestedUserCount(finalQuery, client.getCompanyId());
            return new ResponseEntity<>(clientRequestedUserCount, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(clientRequestedUserCount, HttpStatus.OK);
        } catch (Exception e) {
            throw this.errorClientService(ErrorCodeClientEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<?> getClientInfo(HttpServletRequest request) throws ClientServiceModuleException {
        try {
            String username = this.securityUtils.getUsernameFromToken(request);
            UserRegistration userRegistration = userRepository.findByUsername(username);
            if (userRegistration.getCategory().equalsIgnoreCase(CategoryEnum.CLIENT.getValue())) {
                ClientCM clientCM = new ClientCM();
                clientCM.setCategory(userRegistration.getCategory());
                Client client = clientRepository.getCompanyByUserId(userRegistration.getId());
                clientCM.setCompanyId(client.getCompanyId());
                clientCM.setDob(userRegistration.getDob());
                clientCM.setEmailId(userRegistration.getUsername());
                clientCM.setGender(userRegistration.getGender());
                clientCM.setName(userRegistration.getName());
                clientCM.setId(userRegistration.getId());
                clientCM.setPhoto(userRegistration.getPhoto());

                List<String> roles = new ArrayList<>();
                for (Authorities authorities : userRegistration.getAuthorities()) {
                    roles.add(authorities.getAuthority());
                }
                clientCM.setRoles(roles);
                return new ResponseEntity<>(clientCM, HttpStatus.OK);
            } else {
                throw this.errorClientService(ErrorCodeClientEnum.UNAUTHORIZED.getErrorCode());
            }
        } catch (Exception e) {
            throw this.errorClientService(ErrorCodeClientEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }


    @Override
    public ResponseEntity<?> updateClientInfo(HttpServletRequest request, ClientCM client) throws ClientServiceModuleException {
        try {
            String username = this.securityUtils.getUsernameFromToken(request);
            UserRegistration userRegistration = userRepository.findByUsername(username);
            if (userRegistration.getCategory().equalsIgnoreCase(CategoryEnum.CLIENT.getValue())) {
                Long status = clientRepository.updateClientInfo(client);
                if (status != null && status > 0) {
                    MapperObjectCM mapperObjectCM = new MapperObjectCM();
                    Map<String, String> map = new HashMap<>();
                    map.put("status", "Success");
                    mapperObjectCM.setMapper(map);
                    return new ResponseEntity<>(mapperObjectCM, HttpStatus.OK);
                } else {
                    throw this.errorClientService(ErrorCodeClientEnum.INTERNAL_SERVER_ERROR.getErrorCode());
                }
            } else {
                throw this.errorClientService(ErrorCodeClientEnum.UNAUTHORIZED.getErrorCode());
            }
        } catch (Exception e) {
            logger.error("The error :: {} and {}", e.getMessage(), e);
            throw this.errorClientService(ErrorCodeClientEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public List<String[]> generateUserDataCSV(List<String> usernames) {
        List<UserRegistration> userRegistrations = new ArrayList<>();
        if (!usernames.isEmpty()) {
            UserRegistration userRegistration = null;
            for (String username : usernames) {
                userRegistration = userRepository.findByUsername(username);
                userRegistrations.add(userRegistration);
            }
            return createCsvDataForUserDetails(userRegistrations);
        } else {
            throw this.errorClientService(ErrorCodeClientEnum.UNAUTHORIZED.getErrorCode());
        }
    }

    private List<String[]> createCsvDataForUserDetails(List<UserRegistration> userRegistrationList) {
        List<String[]> outCsvData = new ArrayList<String[]>();
        // Headers creation for CSV output
        String[] headers = csvHeaderName.split(",");
        if (!userRegistrationList.isEmpty()) {

            // String Joiner - Is used to join the data with a delemiter
            // This join as to be in the same sequence as the column header defined in the CSV.properties.
            // Meaning the data and the header should be in the same sequence.
            List<String[]> dataStringArray = userRegistrationList.stream().map(
                    row ->
                            new StringJoiner(",")
                                    .add(row.getName() != null ? row.getName().trim() : "")
                                    .add(row.getUsername() != null ? row.getUsername().trim() : "")
                                    .add(row.getGender() != null ? row.getGender().trim() : "")
                                    .add(row.getDob() != null ? row.getDob().toString().trim() : "")
                                    .add(row.getPhone() != null ? row.getPhone().toString().trim() : "")
                                    .add(row.getNationality() != null ? row.getNationality().trim() : "")
                                    .add(row.getLocation().get(0).getCountry().getCountryName() != null ? row.getLocation().get(0).getCountry().getCountryName().trim() : "")
                                    .add(row.getLocation().get(0).getState().getStateName() != null ? row.getLocation().get(0).getState().getStateName().trim() : "")
                                    .add(row.getLocation().get(0).getCity() != null ? row.getLocation().get(0).getCity().trim() : "")
                                    .toString().split(regexStringSpliter)
            ).collect(Collectors.toList());
            outCsvData.add(headers);
            outCsvData.addAll(dataStringArray);
        }
        return outCsvData;
    }

    @Override
    public ResponseEntity<?> getClientDashboardUsersCount(HttpServletRequest request) throws ClientServiceModuleException {
        ClientDashboardUserCountSummaryCM userCountSummaryCM = new ClientDashboardUserCountSummaryCM();
        try {
            String username = this.securityUtils.getUsernameFromToken(request);
            UserRegistration userRegistration = userRepository.findByUsername(username);
            Client client = clientRepository.getCompanyByUserId(userRegistration.getId());

            userCountSummaryCM = clientRepository.getClientDashboardUserCounts(client.getCompanyId());
            return new ResponseEntity<>(userCountSummaryCM, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(userCountSummaryCM, HttpStatus.OK);
        } catch (Exception e) {
            throw this.errorClientService(ErrorCodeClientEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<?> getAdminDashboardUsersCount(HttpServletRequest request) throws ClientServiceModuleException {
        AdminDashboardUserCountSummaryCM userCountSummaryCM = new AdminDashboardUserCountSummaryCM();
        try {
            String username = this.securityUtils.getUsernameFromToken(request);
            UserRegistration userRegistration = userRepository.findByUsername(username);
            if (userRegistration.getCategory().equalsIgnoreCase("admin")) {
                userCountSummaryCM = clientRepository.getAdminDashboardUserCounts();
            } else {
                throw this.errorClientService(ErrorCodeClientEnum.UNAUTHORIZED.getErrorCode());
            }
            return new ResponseEntity<>(userCountSummaryCM, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(userCountSummaryCM, HttpStatus.OK);
        } catch (Exception e) {
            throw this.errorClientService(ErrorCodeClientEnum.INTERNAL_SERVER_ERROR.getErrorCode(), e);
        }
    }

}
