package com.softedge.solution.contractmodels;

public class CountryCM {

    private Long id;
    private String countryCode;
    private String countryName;
    private String countryPhoneCode;
    private String countryLogo;


    public CountryCM() {
    }


    public CountryCM(String countryCode, String countryName, String countryPhoneCode, String countryLogo) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.countryPhoneCode = countryPhoneCode;
        this.countryLogo = countryLogo;
    }

    public CountryCM(String countryCode, String countryName, String countryPhoneCode) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.countryPhoneCode = countryPhoneCode;
    }

    public CountryCM(String country_code, String country_name, String country_phone_code, long id) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.countryPhoneCode = countryPhoneCode;
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryPhoneCode() {
        return countryPhoneCode;
    }

    public void setCountryPhoneCode(String countryPhoneCode) {
        this.countryPhoneCode = countryPhoneCode;
    }

    public String getCountryLogo() {
        return countryLogo;
    }

    public void setCountryLogo(String countryLogo) {
        this.countryLogo = countryLogo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
