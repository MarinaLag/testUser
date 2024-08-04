package ru.andersenlab.userservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "EMPLOYEE")
@Data
@Accessors(chain = true)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EMPLOYEE_ID")
    private UUID employeeId;

    @OneToOne
    @JoinColumn(name = "PERSONAL_DATA_ID", referencedColumnName = "ID")
    @EqualsAndHashCode.Exclude
    private PersonalData personalData;

    @ManyToOne
    @JoinColumn(name = "POSITION_ID", referencedColumnName = "ID")
    @EqualsAndHashCode.Exclude
    private Position position;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "BIRTHDAY_DATE")
    private LocalDate birthdayDate;

    @ManyToOne
    @JoinColumn(name = "OFFICE_ID", referencedColumnName = "ID")
    @EqualsAndHashCode.Exclude
    private Office office;

    @Column(name = "WORK_NUMBER")
    private String workNumber;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;
}
