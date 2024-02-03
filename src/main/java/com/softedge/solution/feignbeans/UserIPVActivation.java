package com.softedge.solution.feignbeans;

import java.util.Objects;

public class UserIPVActivation {

    private String username;
    private String emailId;
    private Integer activationCode;
    private String userType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Integer getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(Integer activationCode) {
        this.activationCode = activationCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserIPVActivation that = (UserIPVActivation) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(emailId, that.emailId) &&
                Objects.equals(activationCode, that.activationCode) &&
                Objects.equals(userType, that.userType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, emailId, activationCode, userType);
    }

    @Override
    public String toString() {
        return "UserIPVActivation{" +
                "username='" + username + '\'' +
                ", emailId='" + emailId + '\'' +
                ", activationCode=" + activationCode +
                ", userType='" + userType + '\'' +
                '}';
    }
}
