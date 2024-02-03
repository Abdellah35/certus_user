package com.softedge.solution.service;

import com.softedge.solution.contractmodels.CompanyCM;
import com.softedge.solution.exceptionhandlers.custom.bean.CompanyException;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface CompanyService {


    ResponseEntity<?> addCompany(CompanyCM companyCM, HttpServletRequest request) throws CompanyException;

    ResponseEntity<?> updateCompany(CompanyCM companyCM, HttpServletRequest request) throws CompanyException;

    ResponseEntity<?> getCompanyById(Long id, HttpServletRequest request) throws CompanyException;

    ResponseEntity<?> deleteCompanyById(Long id, HttpServletRequest request) throws CompanyException;

    ResponseEntity<?> getAllCompanies(HttpServletRequest request) throws CompanyException;
}
