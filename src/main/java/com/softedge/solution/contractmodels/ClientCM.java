package com.softedge.solution.contractmodels;

import java.util.Date;
import java.util.List;

public class ClientCM {

    private Long id;
    private String emailId;
    private String name;
    private String password;
    private Long companyId;
    private String category;
    private List<String> roles;
    private String gender;
    private Date dob;
    private String photo;
    private String userType;

    public ClientCM() {
    }

    public ClientCM(Long id, String emailId, String name, String password, Long companyId, String category, List<String> roles) {
        this.id = id;
        this.emailId = emailId;
        this.name = name;
        this.password = password;
        this.companyId = companyId;
        this.category = category;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }


    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "ClientCM{" +
                "id=" + id +
                ", emailId='" + emailId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", companyId=" + companyId +
                ", category='" + category + '\'' +
                ", userType='" + userType + '\'' +
                ", roles=" + roles +
                '}';
    }
}
