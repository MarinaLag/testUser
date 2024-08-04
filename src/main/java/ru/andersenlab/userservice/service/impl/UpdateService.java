package ru.andersenlab.userservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andersenlab.userservice.domain.dto.UpdateEmployeeInfoDto;
import ru.andersenlab.userservice.domain.model.Employee;
import ru.andersenlab.userservice.domain.model.PersonalData;
import ru.andersenlab.userservice.repository.CitizenshipRepository;
import ru.andersenlab.userservice.repository.EmployeeRepository;
import ru.andersenlab.userservice.repository.OfficeRepository;
import ru.andersenlab.userservice.repository.PersonalDataRepository;
import ru.andersenlab.userservice.repository.PositionRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private final EmployeeRepository employeeRepository;
    private final PersonalDataRepository personalDataRepository;
    private final CitizenshipRepository citizenshipRepository;
    private final PositionRepository positionRepository;
    private final OfficeRepository officeRepository;

    @Transactional
    public void updateEndDate(UUID employeeId, LocalDate endDate) {
        employeeRepository.updateEndDate(employeeId, endDate);
    }

    public void updateEmployeeDetails(Employee employee, UpdateEmployeeInfoDto updateEmployeeInfoDto) {
        if (updateEmployeeInfoDto.getFirstName() != null) {
            employee.setFirstName(updateEmployeeInfoDto.getFirstName());
        }
        if (updateEmployeeInfoDto.getMiddleName() != null) {
            employee.setMiddleName(updateEmployeeInfoDto.getMiddleName());
        }
        if (updateEmployeeInfoDto.getLastName() != null) {
            employee.setLastName(updateEmployeeInfoDto.getLastName());
        }
        if (updateEmployeeInfoDto.getBirthday() != null) {
            employee.setBirthdayDate(updateEmployeeInfoDto.getBirthday());
        }
        if (updateEmployeeInfoDto.getWorkNumber() != null) {
            employee.setWorkNumber(updateEmployeeInfoDto.getWorkNumber());
        }
        if (updateEmployeeInfoDto.getStartDate() != null) {
            employee.setStartDate(updateEmployeeInfoDto.getStartDate());
        }
        if (updateEmployeeInfoDto.getEndDate() != null) {
            employee.setEndDate(updateEmployeeInfoDto.getEndDate());
        }
        if (updateEmployeeInfoDto.getPositionId() != null) {
            positionRepository.findById(updateEmployeeInfoDto.getPositionId()).ifPresent(employee::setPosition);
        }
        if (updateEmployeeInfoDto.getOfficeId() != null) {
            officeRepository.findById(Long.valueOf(updateEmployeeInfoDto.getOfficeId())).ifPresent(employee::setOffice);
        }

        PersonalData personalData = employee.getPersonalData();
        if (updateEmployeeInfoDto.getPassportSeries() != null) {
            personalData.setPassportSeries(updateEmployeeInfoDto.getPassportSeries());
        }
        if (updateEmployeeInfoDto.getPassportNumber() != null) {
            personalData.setPassportNumber(updateEmployeeInfoDto.getPassportNumber());
        }
        if (updateEmployeeInfoDto.getDateIssued() != null) {
            personalData.setDateIssued(updateEmployeeInfoDto.getDateIssued());
        }
        if (updateEmployeeInfoDto.getIssuedBy() != null) {
            personalData.setIssuedBy(updateEmployeeInfoDto.getIssuedBy());
        }
        if (updateEmployeeInfoDto.getCitizenshipId() != null) {
            citizenshipRepository.findById(updateEmployeeInfoDto.getCitizenshipId())
                    .ifPresent(personalData::setCitizenship);
        }
        if (updateEmployeeInfoDto.getAddress() != null) {
            personalData.setAddress(updateEmployeeInfoDto.getAddress());
        }
        if (updateEmployeeInfoDto.getPhoneNumber() != null) {
            personalData.setPhoneNumber(updateEmployeeInfoDto.getPhoneNumber());
        }
        personalDataRepository.save(personalData);
    }
}
