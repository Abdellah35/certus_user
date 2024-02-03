package com.softedge.solution.repomodels;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Objects;


public class UserDetails {
    private Long id;
    private String name;
    private String gender;
    private String password;
    private String category;
    private String photo;
    private String nationality;
    private String emailId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date dob;
    private Long phone;
    private Date createdDate;
    private String createdBy;
    private Date modifiedDate;
    private String modifiedBy;
    private boolean forcePasswordChange;
    private boolean profileCompleted;
    private boolean ipvCompleted;
    private boolean active;
    private String userType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public boolean isForcePasswordChange() {
        return forcePasswordChange;
    }

    public void setForcePasswordChange(boolean forcePasswordChange) {
        this.forcePasswordChange = forcePasswordChange;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public boolean isIpvCompleted() {
        return ipvCompleted;
    }

    public void setIpvCompleted(boolean ipvCompleted) {
        this.ipvCompleted = ipvCompleted;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
        if (!(o instanceof UserDetails)) return false;
        UserDetails that = (UserDetails) o;
        return isForcePasswordChange() == that.isForcePasswordChange() &&
                isProfileCompleted() == that.isProfileCompleted() &&
                isIpvCompleted() == that.isIpvCompleted() &&
                isActive() == that.isActive() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getGender(), that.getGender()) &&
                Objects.equals(getPassword(), that.getPassword()) &&
                Objects.equals(getCategory(), that.getCategory()) &&
                Objects.equals(getPhoto(), that.getPhoto()) &&
                Objects.equals(getNationality(), that.getNationality()) &&
                Objects.equals(getEmailId(), that.getEmailId()) &&
                Objects.equals(getDob(), that.getDob()) &&
                Objects.equals(getPhone(), that.getPhone()) &&
                Objects.equals(getCreatedDate(), that.getCreatedDate()) &&
                Objects.equals(getCreatedBy(), that.getCreatedBy()) &&
                Objects.equals(getModifiedDate(), that.getModifiedDate()) &&
                Objects.equals(getModifiedBy(), that.getModifiedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getGender(), getPassword(), getCategory(), getPhoto(), getNationality(), getEmailId(), getDob(), getPhone(), getCreatedDate(), getCreatedBy(), getModifiedDate(), getModifiedBy(), isForcePasswordChange(), isProfileCompleted(), isIpvCompleted(), isActive());
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", password='" + password + '\'' +
                ", category='" + category + '\'' +
                ", photo='" + photo + '\'' +
                ", nationality='" + nationality + '\'' +
                ", emailId='" + emailId + '\'' +
                ", dob=" + dob +
                ", phone=" + phone +
                ", createdDate=" + createdDate +
                ", createdBy='" + createdBy + '\'' +
                ", modifiedDate=" + modifiedDate +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", forcePasswordChange=" + forcePasswordChange +
                ", profileCompleted=" + profileCompleted +
                ", ipvCompleted=" + ipvCompleted +
                ", active=" + active +
                '}';
    }
}
