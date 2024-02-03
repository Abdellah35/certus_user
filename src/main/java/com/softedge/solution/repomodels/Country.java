package com.softedge.solution.repomodels;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="country_mtb")
@Data
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "country_name")
    private String countryName;
    @Column(name = "country_code")
    private String countryCode;
    @Column(name = "country_logo")
    private String countryLogo;
    @Column(name = "country_phone_code")
    private String phoneCode;


    @OneToOne(fetch = FetchType.EAGER, mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    private Location location;

}
