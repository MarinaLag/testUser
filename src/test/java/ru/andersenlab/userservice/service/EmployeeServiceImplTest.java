package ru.andersenlab.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.andersenlab.userservice.client.AuthServiceFeignClient;
import ru.andersenlab.userservice.domain.dto.BlockRequestDto;
import ru.andersenlab.userservice.domain.dto.ChangeAccessRequestDto;
import ru.andersenlab.userservice.domain.dto.EmployeeDto;
import ru.andersenlab.userservice.domain.dto.EmployeeInfoDto;
import ru.andersenlab.userservice.domain.dto.UpdateEmployeeInfoDto;
import ru.andersenlab.userservice.domain.mapper.EmployeeMapper;
import ru.andersenlab.userservice.domain.model.Employee;
import ru.andersenlab.userservice.domain.model.Office;
import ru.andersenlab.userservice.domain.model.PersonalData;
import ru.andersenlab.userservice.domain.model.Position;
import ru.andersenlab.userservice.repository.EmployeeRepository;
import ru.andersenlab.userservice.service.impl.EmployeeServiceImpl;
import ru.andersenlab.userservice.service.impl.FeignAuthService;
import ru.andersenlab.userservice.service.impl.UpdateService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AuthServiceFeignClient authServiceFeignClient;

    @Mock
    private UpdateService updateService;

    @Mock
    private FeignAuthService feignAuthService;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private UUID employeeId;
    private UpdateEmployeeInfoDto updateEmployeeInfoDto;
    private Employee employee;

    @Test

    @BeforeEach
    public void setUp() {
        employeeId = UUID.randomUUID();
        updateEmployeeInfoDto = new UpdateEmployeeInfoDto();
        updateEmployeeInfoDto.setFirstName("John");
        updateEmployeeInfoDto.setLastName("Doe");
        updateEmployeeInfoDto.setMiddleName("Middle");
        updateEmployeeInfoDto.setBirthday(LocalDate.of(1990, 1, 1));
        updateEmployeeInfoDto.setWorkNumber("12345");
        updateEmployeeInfoDto.setStartDate(LocalDate.of(2020, 1, 1));
        updateEmployeeInfoDto.setEndDate(null);
        updateEmployeeInfoDto.setPositionId(222);
        updateEmployeeInfoDto.setOfficeId(333);
        updateEmployeeInfoDto.setPassportSeries("1234");
        updateEmployeeInfoDto.setPassportNumber("567890");
        updateEmployeeInfoDto.setDateIssued(LocalDate.of(2010, 1, 1));
        updateEmployeeInfoDto.setIssuedBy("some");
        updateEmployeeInfoDto.setCitizenshipId(444);
        updateEmployeeInfoDto.setAddress("123 Main Street");
        updateEmployeeInfoDto.setPhoneNumber("555-1234");

        employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setMiddleName("Middle");
        employee.setBirthdayDate(LocalDate.of(1990, 1, 1));
        employee.setWorkNumber("12345");
        employee.setStartDate(LocalDate.of(2020, 1, 1));
        employee.setEndDate(null);

        PersonalData personalData = new PersonalData();
        personalData.setPassportSeries("1234");
        personalData.setPassportNumber("567890");
        personalData.setDateIssued(LocalDate.of(2010, 1, 1));
        personalData.setIssuedBy("some");
        personalData.setAddress("123 Main Street");
        personalData.setPhoneNumber("555-1234");
        employee.setPersonalData(personalData);
    }

    @Test
    public void testGetEmployeeById_Found() {
        UUID employeeId = UUID.randomUUID();
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setMiddleName("M");
        employee.setLastName("Doe");
        employee.setBirthdayDate(LocalDate.of(1990, 1, 1));
        Position position = new Position();
        position.setNamePosition("Developer");
        employee.setPosition(position);
        Office office = new Office();
        office.setId(1);
        employee.setOffice(office);
        employee.setWorkNumber("12345");
        employee.setStartDate(LocalDate.of(2020, 1, 1));
        employee.setEndDate(null);
        PersonalData personalData = new PersonalData();
        personalData.setAddress("123 Main St");
        employee.setPersonalData(personalData);
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        EmployeeInfoDto expectedDto = new EmployeeInfoDto(
                "John",
                "M",
                "Doe",
                "1990-01-01",
                "Developer",
                1,
                "12345",
                "2020-01-01",
                "1990-01-01",
                "123 Main St",
                "Активен"
        );
        EmployeeInfoDto actualDto = employeeService.getEmployeeById(employeeId);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testGetOrganizationAllUsersValidEmployeeDto() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setSearch_string("test");
        employeeDto.setName_positions(List.of("Developer"));
        employeeDto.setCities(List.of("New York"));
        employeeDto.setOffice_ids(List.of(Math.toIntExact(1L)));
        employeeDto.setPage(1);
        employeeDto.setPage_size(10);

        List<Employee> employeeList = List.of(new Employee());
        Page<Employee> employeePage = new PageImpl<>(employeeList);

        when(employeeRepository.findBySearchString(
                anyString(),
                anyList(),
                anyList(),
                anyList(),
                any(Pageable.class)
        )).thenReturn(employeePage);

        List<EmployeeDto> result = employeeService.getOrganizationAllUsers(employeeDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findBySearchString(
                eq("test"),
                eq(List.of("Developer")),
                eq(List.of("New York")),
                eq(List.of(Math.toIntExact(1L))),
                eq(PageRequest.of(0, 10))
        );
    }

    @Test
    void testGetOrganizationAllUsersNullEmployeeDto() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> employeeService.getOrganizationAllUsers(null));
        assertEquals("EmployeeDto cannot be null", thrown.getMessage());
    }

    @Test
    public void testGetEmployeeById_NotFound() {
        UUID employeeId = UUID.randomUUID();
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
        EmployeeInfoDto actualDto = employeeService.getEmployeeById(employeeId);
        assertNull(actualDto);
    }


    @Test
    public void testEditEmployeeInfo() {
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        BlockRequestDto requestDto = new BlockRequestDto(employeeId.toString(), false);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        lenient().when(authServiceFeignClient.blockEmployee(requestDto, UUID.randomUUID().toString())).thenReturn(responseEntity);
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(updateEmployeeInfoDto);

        UpdateEmployeeInfoDto result = employeeService.editEmployeeInfo(employeeId, updateEmployeeInfoDto, UUID.randomUUID().toString());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(updateService, times(1)).updateEmployeeDetails(any(Employee.class), any(UpdateEmployeeInfoDto.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));

        assertEquals(updateEmployeeInfoDto, result);
    }

    @Test
    public void testChangeEmployeeAccessLevel() {
        UUID employeeId = UUID.randomUUID();
        Integer accessLevelId = 1;
        ChangeAccessRequestDto requestDto = new ChangeAccessRequestDto(employeeId, accessLevelId);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(authServiceFeignClient.changeAccessLevel(any(ChangeAccessRequestDto.class))).thenReturn(responseEntity);

        doAnswer(invocation -> {
            FeignAuthService.RetryableFeignCall call = invocation.getArgument(0);
            call.execute();
            return null;
        }).when(feignAuthService).retryOnServerError(any(FeignAuthService.RetryableFeignCall.class));

        ChangeAccessRequestDto result = employeeService.changeEmployeeAccessLevel(employeeId, accessLevelId);

        verify(feignAuthService, times(1)).retryOnServerError(any(FeignAuthService.RetryableFeignCall.class));
        verify(authServiceFeignClient, times(1)).changeAccessLevel(any(ChangeAccessRequestDto.class));

        assertEquals(requestDto, result);
    }
}
