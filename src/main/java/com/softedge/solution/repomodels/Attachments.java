package com.softedge.solution.repomodels;

public class Attachments {

    private Long attachmentsId;
    private String fileName;
    private String fileAttachment;

    public Long getAttachmentsId() {
        return attachmentsId;
    }

    public void setAttachmentsId(Long attachmentsId) {
        this.attachmentsId = attachmentsId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileAttachment() {
        return fileAttachment;
    }

    public void setFileAttachment(String fileAttachment) {
        this.fileAttachment = fileAttachment;
    }
}
