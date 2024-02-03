package com.softedge.solution.repomodels;

import java.util.Date;


public class NotificationDetails {

    private Long id;
    private String message;
    private String nativeMessage;
    private String module;
    private Date createdAt;
    private Long requesteeUserId;
    private Long requestorUserId;
    private Long companyId;
    private boolean messageRead = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNativeMessage() {
        return nativeMessage;
    }

    public void setNativeMessage(String nativeMessage) {
        this.nativeMessage = nativeMessage;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getRequesteeUserId() {
        return requesteeUserId;
    }

    public void setRequesteeUserId(Long requesteeUserId) {
        this.requesteeUserId = requesteeUserId;
    }

    public Long getRequestorUserId() {
        return requestorUserId;
    }

    public void setRequestorUserId(Long requestorUserId) {
        this.requestorUserId = requestorUserId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public boolean isMessageRead() {
        return messageRead;
    }

    public void setMessageRead(boolean messageRead) {
        this.messageRead = messageRead;
    }

    @Override
    public String toString() {
        return "NotificationDetails{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", nativeMessage='" + nativeMessage + '\'' +
                ", module='" + module + '\'' +
                ", createdAt=" + createdAt +
                ", requesteeUserId=" + requesteeUserId +
                ", requestorUserId=" + requestorUserId +
                ", companyId=" + companyId +
                ", messageRead=" + messageRead +
                '}';
    }
}
