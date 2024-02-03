package com.softedge.solution.repository;

import com.softedge.solution.contractmodels.KycProcessDocumentDetailsCM;
import com.softedge.solution.exceptionhandlers.custom.GenericModuleException;

import java.util.List;

public interface KycRepository {

     KycProcessDocumentDetailsCM getKycDocumentDetailsById(Long id);

     List<KycProcessDocumentDetailsCM> tempKycDetailsByRequesteeId(List<Long> requesteeUserId)throws GenericModuleException ;
}
