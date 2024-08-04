package ru.andersenlab.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andersenlab.userservice.domain.dto.UpdateEmployeeInfoDto;
import ru.andersenlab.userservice.domain.model.Citizenship;
import ru.andersenlab.userservice.domain.model.Employee;
import ru.andersenlab.userservice.domain.model.Office;
import ru.andersenlab.userservice.domain.model.PersonalData;
import ru.andersenlab.userservice.domain.model.Position;
import ru.andersenlab.userservice.repository.CitizenshipRepository;
import ru.andersenlab.userservice.repository.EmployeeRepository;
import ru.andersenlab.userservice.repository.OfficeRepository;
import ru.andersenlab.userservice.repository.PersonalDataRepository;
import ru.andersenlab.userservice.repository.PositionRepository;
import ru.andersenlab.userservice.service.impl.UpdateService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PersonalDataRepository personalDataRepository;

    @Mock
    private CitizenshipRepository citizenshipRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private OfficeRepository officeRepository;

    @InjectMocks
    private UpdateService updateService;

    private Employee employee;
    private UpdateEmployeeInfoDto updateEmployeeInfoDto;
    private PersonalData personalData;

    @BeforeEach
    public void setUp() {
        employee = new Employee();
        employee.setEmployeeId(UUID.randomUUID());
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setMiddleName("Middle");
        employee.setBirthdayDate(LocalDate.of(1990, 1, 1));
        employee.setWorkNumber("12345");
        employee.setStartDate(LocalDate.of(2020, 1, 1));
        employee.setEndDate(null);

        personalData = new PersonalData();
        personalData.setPassportSeries("1234");
        personalData.setPassportNumber("567890");
        personalData.setDateIssued(LocalDate.of(2010, 1, 1));
        personalData.setIssuedBy("some");
        personalData.setAddress("123 Main Street");
        personalData.setPhoneNumber("555-1234");
        employee.setPersonalData(personalData);

        updateEmployeeInfoDto = new UpdateEmployeeInfoDto();
        updateEmployeeInfoDto.setFirstName("Jane");
        updateEmployeeInfoDto.setLastName("Smith");
        updateEmployeeInfoDto.setMiddleName("Middle");
        updateEmployeeInfoDto.setBirthday(LocalDate.of(1991, 2, 2));
        updateEmployeeInfoDto.setWorkNumber("67890");
        updateEmployeeInfoDto.setStartDate(LocalDate.of(2021, 2, 2));
        updateEmployeeInfoDto.setEndDate(LocalDate.of(2023, 3, 3));
        updateEmployeeInfoDto.setPositionId(1);
        updateEmployeeInfoDto.setOfficeId(2);
        updateEmployeeInfoDto.setPassportSeries("4321");
        updateEmployeeInfoDto.setPassportNumber("098765");
        updateEmployeeInfoDto.setDateIssued(LocalDate.of(2011, 2, 2));
        updateEmployeeInfoDto.setIssuedBy("another");
        updateEmployeeInfoDto.setCitizenshipId(3);
        updateEmployeeInfoDto.setAddress("456 Another Street");
        updateEmployeeInfoDto.setPhoneNumber("555-6789");
    }

    @Test
    public void testUpdateEndDate() {
        UUID employeeId = UUID.randomUUID();
        LocalDate endDate = LocalDate.now();

        updateService.updateEndDate(employeeId, endDate);

        verify(employeeRepository, times(1)).updateEndDate(employeeId, endDate);
    }

    @Test
    public void testUpdateEmployeeDetails() {
        Position position = new Position();
        Office office = new Office();
        Citizenship citizenship = new Citizenship();

        when(positionRepository.findById(updateEmployeeInfoDto.getPositionId())).thenReturn(Optional.of(position));
        when(officeRepository.findById(Long.valueOf(updateEmployeeInfoDto.getOfficeId()))).thenReturn(Optional.of(office));
        when(citizenshipRepository.findById(updateEmployeeInfoDto.getCitizenshipId())).thenReturn(Optional.of(citizenship));

        updateService.updateEmployeeDetails(employee, updateEmployeeInfoDto);

        assertEquals(updateEmployeeInfoDto.getFirstName(), employee.getFirstName());
        assertEquals(updateEmployeeInfoDto.getLastName(), employee.getLastName());
        assertEquals(updateEmployeeInfoDto.getMiddleName(), employee.getMiddleName());
        assertEquals(updateEmployeeInfoDto.getBirthday(), employee.getBirthdayDate());
        assertEquals(updateEmployeeInfoDto.getWorkNumber(), employee.getWorkNumber());
        assertEquals(updateEmployeeInfoDto.getStartDate(), employee.getStartDate());
        assertEquals(updateEmployeeInfoDto.getEndDate(), employee.getEndDate());
        assertEquals(position, employee.getPosition());
        assertEquals(office, employee.getOffice());

        assertEquals(updateEmployeeInfoDto.getPassportSeries(), personalData.getPassportSeries());
        assertEquals(updateEmployeeInfoDto.getPassportNumber(), personalData.getPassportNumber());
        assertEquals(updateEmployeeInfoDto.getDateIssued(), personalData.getDateIssued());
        assertEquals(updateEmployeeInfoDto.getIssuedBy(), personalData.getIssuedBy());
        assertEquals(citizenship, personalData.getCitizenship());
        assertEquals(updateEmployeeInfoDto.getAddress(), personalData.getAddress());
        assertEquals(updateEmployeeInfoDto.getPhoneNumber(), personalData.getPhoneNumber());

        verify(personalDataRepository, times(1)).save(personalData);
    }
}
