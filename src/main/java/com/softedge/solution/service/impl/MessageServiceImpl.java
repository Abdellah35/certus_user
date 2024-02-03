package com.softedge.solution.service.impl;

import com.softedge.solution.contractmodels.KycProcessDocumentDetailsCM;
import com.softedge.solution.contractmodels.MessageDetailsCM;
import com.softedge.solution.contractmodels.PlainMessageCM;
import com.softedge.solution.enuminfo.CategoryEnum;
import com.softedge.solution.exceptionhandlers.custom.GenericModuleException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeGenericEnum;
import com.softedge.solution.repomodels.MessagesDetails;
import com.softedge.solution.repomodels.UserRegistration;
import com.softedge.solution.repository.KycRepository;
import com.softedge.solution.repository.MessageRepository;
import com.softedge.solution.repository.UserRepository;
import com.softedge.solution.repository.impl.CompanyRepositoryImpl;
import com.softedge.solution.security.util.SecurityUtils;
import com.softedge.solution.service.MessageService;
import com.softedge.solution.service.certusabstractservice.CerterGenericErrorHandlingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MessageServiceImpl extends CerterGenericErrorHandlingService implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    protected SecurityUtils securityUtils;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    private KycRepository kycRepository;

    @Autowired
    private CompanyRepositoryImpl companyRepository;


    @Override
    public ResponseEntity<?> saveMessages(Long kycId, PlainMessageCM plainMessageCM, HttpServletRequest request) throws GenericModuleException {
        if (kycId != null) {
            if (!StringUtils.isEmpty(plainMessageCM.getMessage())) {
                String username = this.securityUtils.getUsernameFromToken(request);
                UserRegistration userRegistration = userRepository.findByUsername(username);
                MessagesDetails messagesDetails = new MessagesDetails();
                messagesDetails.setCreatedBy(username);
                messagesDetails.setLastUpdatedTimestamp(new Date());
                messagesDetails.setMarkAsRead(false);
                messagesDetails.setMessage(plainMessageCM.getMessage());
                KycProcessDocumentDetailsCM kycProcessDocumentDetailsCM = kycRepository.getKycDocumentDetailsById(kycId);
                if(kycProcessDocumentDetailsCM!=null && kycProcessDocumentDetailsCM.getId()!=null) {
                    messagesDetails.setKycId(kycId);
                    if (userRegistration.getCategory().equalsIgnoreCase(CategoryEnum.CLIENT.getValue())) {
                        messagesDetails.setRequestorUserId(userRegistration.getId());
                    } else if (userRegistration.getCategory().equalsIgnoreCase(CategoryEnum.USER.getValue())) {
                        if (kycProcessDocumentDetailsCM.getRequesteeUserId().equals(userRegistration.getId())) {
                            messagesDetails.setRequesteeUserId(userRegistration.getId());
                        } else {
                            throw this.errorService(ErrorCodeGenericEnum.UNAUTHORIZED.getErrorCode());
                        }
                    } else {
                        throw this.errorService(ErrorCodeGenericEnum.UNAUTHORIZED.getErrorCode());
                    }
                }else{
                    throw this.errorService(ErrorCodeGenericEnum.KYC_NOT_FOUND.getErrorCode());
                }
                Long id = messageRepository.messageDetailsSave(messagesDetails);
                if(id>0){
                    MessageDetailsCM messageDetailsCM= messageRepository.getMessageDetailsById(id);
                    return new ResponseEntity<>(messageDetailsCM, HttpStatus.OK);
                }else {
                    throw this.errorService(ErrorCodeGenericEnum.INTERNAL_SERVER_ERROR.getErrorCode());
                }

            }else {
                throw this.errorService(ErrorCodeGenericEnum.MESSAGE_IS_EMPTY.getErrorCode());
            }
        }else {
            throw this.errorService(ErrorCodeGenericEnum.KYC_NOT_FOUND.getErrorCode());
        }
    }

    @Override
    public ResponseEntity<?> getMessages(Long kycId, HttpServletRequest request) throws GenericModuleException {
        if(kycId!=null){
            List<MessageDetailsCM> messageDetailsCMS = new ArrayList<>();
            String username = this.securityUtils.getUsernameFromToken(request);
            Long companyId = companyRepository.getCompanyIdByUsername(username);
            UserRegistration userRegistration = userRepository.findByUsername(username);
            KycProcessDocumentDetailsCM kycProcessDocumentDetailsCM = kycRepository.getKycDocumentDetailsById(kycId);
            if(kycProcessDocumentDetailsCM!=null && kycProcessDocumentDetailsCM.getId()!=null) {
                if (kycProcessDocumentDetailsCM.getCompanyId().equals(companyId) ||
                        kycProcessDocumentDetailsCM.getRequesteeUserId().equals(userRegistration.getId())) {
                    messageDetailsCMS = messageRepository.getMessageDetailsByKycId(kycId);
                    return new ResponseEntity<>(messageDetailsCMS, HttpStatus.OK);
                } else {
                    throw this.errorService(ErrorCodeGenericEnum.UNAUTHORIZED.getErrorCode());
                }
            }else{
                throw this.errorService(ErrorCodeGenericEnum.KYC_NOT_FOUND.getErrorCode());
            }

        }else {
            throw this.errorService(ErrorCodeGenericEnum.KYC_NOT_FOUND.getErrorCode());
        }
    }


    @Override
    public ResponseEntity<?> deleteMessage(Long messageId, HttpServletRequest request) throws GenericModuleException {
        if(messageId != null){
            Boolean status = false;
            String username = this.securityUtils.getUsernameFromToken(request);
            MessageDetailsCM messageDetailsCM= messageRepository.getMessageDetailsById(messageId);
            if(username.equalsIgnoreCase(messageDetailsCM.getEmailId())){
                 status = messageRepository.deleteMessageById(messageId);
            }else {
                throw this.errorService(ErrorCodeGenericEnum.UNAUTHORIZED.getErrorCode());
            }
            if(status) {
                return new ResponseEntity("Success", HttpStatus.OK);
            } else {
                throw this.errorService(ErrorCodeGenericEnum.INTERNAL_SERVER_ERROR.getErrorCode());
            }
        }else{
            throw this.errorService(ErrorCodeGenericEnum.MESSAGE_ID_NOT_FOUNT.getErrorCode());
        }
    }

}
