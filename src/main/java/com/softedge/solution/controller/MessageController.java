package com.softedge.solution.controller;

import com.softedge.solution.contractmodels.PlainMessageCM;
import com.softedge.solution.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/comment")
public class MessageController extends BaseController{

    @Autowired
    private MessageService messageService;

    @PostMapping("/{kyc-id}/message")
    public ResponseEntity saveMessage(@PathVariable("kyc-id") Long kycId, @RequestBody PlainMessageCM plainMessageCM, HttpServletRequest request){
        return messageService.saveMessages(kycId,plainMessageCM,request);
    }


    @GetMapping("/{kyc-id}/message")
    public ResponseEntity getMessages(@PathVariable("kyc-id") Long kycId, HttpServletRequest request){
        return messageService.getMessages(kycId, request);
    }

    @DeleteMapping("/message/{message-id}")
    public ResponseEntity deleteMessage(@PathVariable("message-id") Long messageId, HttpServletRequest request){
        return messageService.deleteMessage(messageId, request);
    }
}
