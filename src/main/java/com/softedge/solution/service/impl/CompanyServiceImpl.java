package com.softedge.solution.service.impl;

import com.softedge.solution.contractmodels.CompanyCM;
import com.softedge.solution.contractmodels.CountryCM;
import com.softedge.solution.contractmodels.MapperObjectCM;
import com.softedge.solution.exceptionhandlers.custom.bean.CompanyException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeCompanyEnum;
import com.softedge.solution.repomodels.Company;
import com.softedge.solution.repomodels.UserDetails;
import com.softedge.solution.repository.impl.CompanyRepositoryImpl;
import com.softedge.solution.repository.impl.CountryRepositoryImpl;
import com.softedge.solution.repository.impl.StateRepositoryImpl;
import com.softedge.solution.repository.impl.UserDetailsRepositoryImpl;
import com.softedge.solution.security.util.SecurityUtils;
import com.softedge.solution.service.CompanyService;
import com.softedge.solution.service.certusabstractservice.CertusAbstractCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanyServiceImpl extends CertusAbstractCompanyService implements CompanyService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CompanyRepositoryImpl companyRepository;

    @Autowired
    StateRepositoryImpl stateRepository;

    @Autowired
    CountryRepositoryImpl countryRepository;

    @Autowired
    SecurityUtils securityUtils;

    @Autowired
    UserDetailsRepositoryImpl userDetailsRepositoryImpl;

    @Override
    public ResponseEntity<?> addCompany(CompanyCM companyCM, HttpServletRequest request) throws  CompanyException{
        Long companyId = 0L;
        String username = this.securityUtils.getUsernameFromToken(request);
        UserDetails user = userDetailsRepositoryImpl.getUserByUsername(username);
        String countryCode = companyCM.getCountry();
        CountryCM countryCM = countryRepository.getCountryByCountryCode(countryCode);
        Company company = new Company();
        try {
            company = this.companyValidationService(companyCM, company);
        }catch (Exception e){
            throw e;
        }
        company.setLogo(companyCM.getLogo());
        company.setCountry(countryCM.getId());
        company.setCity(companyCM.getCity());
        company.setState(companyCM.getState());
        company.setCreatedDate(new Date());
        company.setCreatedBy(username);
        company.setModifiedDate(new Date());
        company.setModifiedBy(username);
        try {
            companyId = companyRepository.saveCompanyDetails(company);
        }catch (Exception e) {
            throw new CompanyException(ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR, ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR.getName());
        }
        if(companyId>0){
            companyCM.setId(companyId);
            return new ResponseEntity<>(companyCM, HttpStatus.OK);
        }
        else{
            throw new CompanyException(ErrorCodeCompanyEnum.COMPANY_ID_INVALID, ErrorCodeCompanyEnum.COMPANY_ID_INVALID.getName());
        }
    }

    @Override
    public ResponseEntity<?> updateCompany(CompanyCM companyCM, HttpServletRequest request) throws CompanyException{

            String username = this.securityUtils.getUsernameFromToken(request);
            UserDetails user = userDetailsRepositoryImpl.getUserByUsername(username);


            String countryCode = companyCM.getCountry();
            CountryCM countryCM = countryRepository.getCountryByCountryCode(countryCode);
            Company companyDetailsById = companyRepository.getCompanyDetailsById(companyCM.getId());

            if(companyDetailsById!=null){
                try{
                    companyDetailsById = this.companyValidationService(companyCM, companyDetailsById);
                }
                catch (Exception e){
                    throw e;
                }

                companyDetailsById.setLogo(companyCM.getLogo());
                companyDetailsById.setCountry(countryCM.getId());
                companyDetailsById.setCity(companyCM.getCity());
                companyDetailsById.setState(companyCM.getState());
                companyDetailsById.setModifiedDate(new Date());
                companyDetailsById.setModifiedBy(username);

                Long companyId = null;
                try {
                    companyId = companyRepository.updateCompanyDetails(companyDetailsById);
                } catch (Exception e) {
                    throw new CompanyException(ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR, ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR.getName());
                }
                if(companyId>0){
                    return new ResponseEntity<>(companyCM, HttpStatus.OK);
                }
                else{
                    throw new CompanyException(ErrorCodeCompanyEnum.COMPANY_ID_INVALID, ErrorCodeCompanyEnum.COMPANY_ID_INVALID.getName());
                }
            }
            else{

                return this.addCompany(companyCM,request);
            }
        }



    @Override
    public ResponseEntity<?> getCompanyById(Long id, HttpServletRequest request) throws CompanyException{
        CompanyCM companyCM = null;
        try{
            Company company = companyRepository.getCompanyDetailsById(id);
            CountryCM countryById = countryRepository.getCountryById(company.getCountry());
            if(company!=null){
                companyCM = new CompanyCM();
                companyCM.setId(company.getId());
                companyCM.setLogo(company.getLogo());
                companyCM.setCountry(countryById.getCountryCode());
                companyCM.setCity(company.getCity());
                companyCM.setCompanyName(company.getCompanyName());
                companyCM.setState(company.getState());
                companyCM.setWebsite(company.getWebsite());
                companyCM.setId(company.getId());
                return new ResponseEntity<>(companyCM, HttpStatus.OK);
            }
            else{
                throw new CompanyException(ErrorCodeCompanyEnum.COMPANY_ID_INVALID, ErrorCodeCompanyEnum.COMPANY_ID_INVALID.getName());
            }
        }
        catch (Exception e){
            throw new CompanyException(ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR, ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR.getName());
        }

    }

    @Override
    public ResponseEntity<?> deleteCompanyById(Long id, HttpServletRequest request) throws CompanyException{
        try {
            Boolean deleted = companyRepository.deleteCompanyById(id);
            MapperObjectCM mapperObjectCM = new MapperObjectCM();
            Map<String, String> map = new HashMap();
            if (deleted) {
                map.put("status", "company deleted");
                return new ResponseEntity<>(mapperObjectCM, HttpStatus.OK);
            } else {
                throw new CompanyException(ErrorCodeCompanyEnum.COMPANY_ID_INVALID, ErrorCodeCompanyEnum.COMPANY_ID_INVALID.getName());
            }
        }
        catch (Exception e) {
            throw new CompanyException(ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR, ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR.getName());
        }


    }

    @Override
    public ResponseEntity<?> getAllCompanies(HttpServletRequest request) throws CompanyException {
        try{
            List<CompanyCM> allCompanies = companyRepository.getAllCompanies();
            return ResponseEntity.ok(allCompanies);
        }
        catch (Exception e){
            throw new CompanyException(ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR, ErrorCodeCompanyEnum.INTERNAL_SERVER_ERROR.getName());
        }
    }
}
