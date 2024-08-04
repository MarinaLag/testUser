package ru.andersenlab.userservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Entity
@Table(name = "PERSONAL_DATA")
@Data
@Accessors(chain = true)
public class PersonalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "PASSPORT_NUMBER")
    private String passportNumber;

    @Column(name = "PASSPORT_SERIES")
    private String passportSeries;

    @Column(name = "DATE_ISSUED")
    private LocalDate dateIssued;

    @ManyToOne
    @JoinColumn(name = "CITIZENSHIP_ID", referencedColumnName = "ID")
    @EqualsAndHashCode.Exclude
    private Citizenship citizenship;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "ISSUED_BY")
    private String issuedBy;
}
