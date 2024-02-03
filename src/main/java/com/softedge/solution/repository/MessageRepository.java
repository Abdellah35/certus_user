package com.softedge.solution.repository;

import com.softedge.solution.contractmodels.MessageDetailsCM;
import com.softedge.solution.exceptionhandlers.custom.GenericModuleException;
import com.softedge.solution.repomodels.MessagesDetails;

import java.util.List;

public interface MessageRepository {

    Long messageDetailsSave(MessagesDetails messagesDetails) throws GenericModuleException;

    MessageDetailsCM getMessageDetailsById(Long id) throws GenericModuleException;

    List<MessageDetailsCM> getMessageDetailsByKycId(Long kycId) throws GenericModuleException;

    Boolean deleteMessageById(Long id) throws GenericModuleException;
}
