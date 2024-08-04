package ru.andersenlab.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.andersenlab.userservice.client.AuthServiceFeignClient;
import ru.andersenlab.userservice.domain.dto.AccountCredentialsDto;
import ru.andersenlab.userservice.domain.dto.BlockRequestDto;
import ru.andersenlab.userservice.domain.dto.ChangeAccessRequestDto;
import ru.andersenlab.userservice.domain.dto.EmployeeDto;
import ru.andersenlab.userservice.domain.dto.EmployeeInfoDto;
import ru.andersenlab.userservice.domain.dto.EmployeeRegisterDto;
import ru.andersenlab.userservice.domain.dto.EmployeeSearchDto;
import ru.andersenlab.userservice.domain.dto.FilterRequestDto;
import ru.andersenlab.userservice.domain.dto.FilterResponseDto;
import ru.andersenlab.userservice.domain.dto.UpdateEmployeeInfoDto;
import ru.andersenlab.userservice.domain.exception.BadRequestException;
import ru.andersenlab.userservice.domain.exception.EmployeeNotFoundException;
import ru.andersenlab.userservice.domain.exception.InvalidRequestException;
import ru.andersenlab.userservice.domain.mapper.EmployeeMapper;
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
import ru.andersenlab.userservice.service.EmployeeService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {


    private final EmployeeRepository employeeRepository;
    private final AuthServiceFeignClient authServiceFeignClient;
    private final UpdateService updateService;
    private final FeignAuthService feignAuthService;
    private final EmployeeMapper employeeMapper;
    private final PersonalDataRepository personalDataRepository;
    private final RestTemplate restTemplate;
    private final CitizenshipRepository citizenshipRepository;
    private final PositionRepository positionRepository;
    private final OfficeRepository officeRepository;


    @Override
    public EmployeeInfoDto getEmployeeById(UUID employeeId) throws InvalidRequestException {
        return employeeRepository.findById(employeeId)
                .map(this::mapToDto)
                .orElse(null);
    }

    private EmployeeInfoDto mapToDto(Employee employee) {
        String status = employee.getEndDate() == null ? "Активен" : "Заблокирован";
        return new EmployeeInfoDto(
                employee.getFirstName(),
                employee.getMiddleName(),
                employee.getLastName(),
                employee.getBirthdayDate().toString(),
                employee.getPosition().getNamePosition(),
                employee.getOffice().getId(),
                employee.getWorkNumber(),
                employee.getStartDate().toString(),
                employee.getBirthdayDate().toString(),
                employee.getPersonalData().getAddress(),
                status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDto> getOrganizationAllUsers(EmployeeDto employeeDto) {
        if (employeeDto == null) {
            throw new IllegalArgumentException("EmployeeDto cannot be null");
        }
        Employee employee = new Employee();
        employee.setOffice(new Office());
        employee.setBirthdayDate(LocalDate.now());
        employee.setPosition(new Position());
        employee.setPersonalData(new PersonalData());

        Page<Employee> employees = employeeRepository.findBySearchString(
                employeeDto.getSearch_string(),
                employeeDto.getName_positions(),
                employeeDto.getCities(),
                employeeDto.getOffice_ids(),
                PageRequest.of(employeeDto.getPage() - 1, employeeDto.getPage_size()));
        return mapFromEntities(employees.getContent());
    }

    @Override
    @Transactional
    public List<EmployeeInfoDto> getEmployeeByFullName(EmployeeSearchDto employeeSearchDto) {
        String firstName = employeeSearchDto.getFirstName();
        String middleName = employeeSearchDto.getMiddleName();
        String lastName = employeeSearchDto.getLastName();

        if ((firstName + lastName + middleName).length() > 80) {
            throw new BadRequestException("Максимальное количество символов не более 80!");
        }
        List<Employee> employees = employeeRepository.searchByFullName(firstName, lastName, middleName);
        if (employees.isEmpty()) {
            throw new NoSuchElementException("Сотрудник не найден");
        }
        return mapToEmployeeInfoDto(employees);
    }

    private Citizenship validateAndGetCitizenship(Integer citizenshipId) {
        return citizenshipRepository.findById(citizenshipId)
                .orElseThrow(() -> new NoSuchElementException("Заданный citizenship_id не найден"));
    }

    private Position validateAndGetPosition(Long positionId) {
        return positionRepository.findById(Math.toIntExact(positionId))
                .orElseThrow(() -> new NoSuchElementException("Заданный position_id не найден"));
    }

    private Office validateAndGetOffice(Long officeId) {
        return officeRepository.findById(officeId)
                .orElseThrow(() -> new NoSuchElementException("Заданный office_id не найден"));
    }

    @Override
    public FilterResponseDto getFilteredCities(FilterRequestDto filterRequestDto) {
        List<Integer> officeIds = filterRequestDto.officeIds();
        List<String> namePositions = filterRequestDto.namePositions();
        if (officeIds == null || namePositions == null) {
            throw new BadRequestException("officeIds or namePositions cannot be null");
        }
        List<String> filteredCities = employeeRepository.getFilteredCities(officeIds, namePositions);
        return new FilterResponseDto(null,null,filteredCities);
    }
    @Override
    public FilterResponseDto getFilteredPosition(FilterRequestDto filterRequestDto) {
        List<Integer> officeIds = filterRequestDto.officeIds();
        List<String> cities = filterRequestDto.cities();
        if (officeIds == null || cities == null) {
            throw new BadRequestException("officeId or Cities equals NULL");
        }
        List<String> filteredPositions = employeeRepository.getFilteredPosition(officeIds, cities);
        return new FilterResponseDto(null,filteredPositions,null);
    }
    @Override
    public FilterResponseDto getFilteredOfficeId(FilterRequestDto filterRequestDto) {
        List<String> cities = filterRequestDto.cities();
        List<String> namePositions = filterRequestDto.namePositions();
        if (cities == null || namePositions == null) {
            throw new BadRequestException("cities or namePosition equals NULL");
        }
        List<Integer> filteredOfficeId = employeeRepository.getFilteredOfficeId(namePositions,cities);
        return new FilterResponseDto(filteredOfficeId,null,null);
    }


    public boolean callExternalEndpoint(AccountCredentialsDto dto, String token) {
        String url = "http://localhost:8080/api/v1/auth/new_employee";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", token);

        HttpEntity<AccountCredentialsDto> httpEntity = new HttpEntity<>(dto, httpHeaders);

        return Boolean.TRUE.equals(restTemplate.exchange(url, HttpMethod.POST, httpEntity, Boolean.class).getBody());
    }


    public static AccountCredentialsDto convertToDto(EmployeeRegisterDto registerDto) {
        AccountCredentialsDto accountCredentialsDto = new AccountCredentialsDto();
        accountCredentialsDto.setEmail(registerDto.getWorkEmail());
        accountCredentialsDto.setAccess_level(registerDto.getAccessLevelId());
        accountCredentialsDto.setPassword(registerDto.getPassword());
        accountCredentialsDto.set_blocked(registerDto.isBlocked());
        return accountCredentialsDto;
    }


    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_СуперЮзер')")
    public UpdateEmployeeInfoDto editEmployeeInfo(UUID employeeId, UpdateEmployeeInfoDto updateEmployeeInfoDto, String token) {
        log.info("Received edit request for employeeId: {}", employeeId);
        log.info("UpdateEmployeeInfoDto received: {}", updateEmployeeInfoDto);
        checkRequest(employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Сотрудник с id " + employeeId + " не найден"));
        updateService.updateEmployeeDetails(employee, updateEmployeeInfoDto);

        setEmployeeEndDate(employee, updateEmployeeInfoDto, token);
        feignAuthService.retryOnServerError(() -> authServiceFeignClient.changeAccessLevel(new ChangeAccessRequestDto(employeeId, updateEmployeeInfoDto.getAccessLevelId())));
        log.info("Employee after update: {}", employee);
        employeeRepository.save(employee);
        UpdateEmployeeInfoDto result = employeeMapper.toDto(employee);
        result.setAccessLevelId(updateEmployeeInfoDto.getAccessLevelId());
        return result;
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_СуперЮзер')")
    public ChangeAccessRequestDto changeEmployeeAccessLevel(UUID employeeId, Integer accessLevelId) {
        ChangeAccessRequestDto requestDto = new ChangeAccessRequestDto(employeeId, accessLevelId);
        log.info("Sending change access level request: {}", requestDto);
        try {
            feignAuthService.retryOnServerError(() -> authServiceFeignClient.changeAccessLevel(requestDto));
        } catch (RuntimeException ex) {
            log.error("Failed to change access level after retries: {}", ex.getMessage(), ex);
            throw ex;
        }
        return requestDto;
    }

    @Override
    @Transactional(rollbackFor = NoSuchElementException.class)
    public String createEmployee(EmployeeRegisterDto employeeRegisterDto, String token) {
        Citizenship citizenship = validateAndGetCitizenship(employeeRegisterDto.getCitizenshipId());
        Position position = validateAndGetPosition(employeeRegisterDto.getPositionId());
        Office office = validateAndGetOffice(employeeRegisterDto.getOfficeId());

        if (!callExternalEndpoint(convertToDto(employeeRegisterDto), token)) {
            throw new IllegalArgumentException("сотрудник с таким email уже существует");
        }

        PersonalData personalData = EmployeeMapper.INSTANCE.toPersonalData(employeeRegisterDto, citizenship);
        personalDataRepository.save(personalData);
        Employee employee = EmployeeMapper.INSTANCE.toEmployee(employeeRegisterDto, personalData, position, office);
        employeeRepository.save(employee);
        return "Сотрудник успешно зарегистрирован";
    }


    private List<EmployeeInfoDto> mapToEmployeeInfoDto(List<Employee> employees) {
        return employees.stream()
                .map(employee -> new EmployeeInfoDto(
                        employee.getFirstName(),
                        employee.getMiddleName(),
                        employee.getLastName(),
                        employee.getPosition().getNamePosition(),
                        employee.getBirthdayDate().toString(),
                        employee.getOffice().getId(),
                        employee.getWorkNumber(),
                        employee.getStartDate().toString(),
                        employee.getBirthdayDate().toString(),
                        employee.getPersonalData().getAddress(),
                        employee.getEndDate() == null ? "Активен" : "Заблокирован"
                ))
                .collect(Collectors.toList());
    }

    public static List<EmployeeDto> mapFromEntities(List<Employee> employees) {
        List<EmployeeDto> employeeDtos = new ArrayList<>();
        for (Employee employee : employees) {
            String fullName = String.format("%s %s %s",
                    employee.getFirstName(),
                    employee.getMiddleName(),
                    employee.getLastName());
            String city = employee.getOffice() != null ? employee.getOffice().getCity() : null;
            EmployeeDto employeeDto = getEmployeeDto(employee, fullName, city);
            employeeDtos.add(employeeDto);
        }
        return employeeDtos;
    }

    private static EmployeeDto getEmployeeDto(Employee employee, String fullName, String city) {
        Integer officeId = employee.getOffice() != null ? employee.getOffice().getId() : null;
        String positionName = employee.getPosition() != null ? employee.getPosition().getNamePosition() : null;

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setSearch_string(fullName);
        employeeDto.setCities(city != null ? List.of(city) : List.of());
        employeeDto.setOffice_ids(officeId != null ? List.of(officeId) : List.of());
        employeeDto.setName_positions(positionName != null ? List.of(positionName) : List.of());
        return employeeDto;
    }

    private void checkRequest(UUID employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Идентификатор сотрудника обязателен");
        }
    }

    private void blockEmployee(UUID employeeId, LocalDate endDate, String token) {
        String employeeIdString = employeeId.toString();
        LocalDate currentDate = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDate();
        if (endDate.isBefore(currentDate) || endDate.isEqual(currentDate)) {
            BlockRequestDto blockRequestDto = new BlockRequestDto(employeeIdString, true);
            log.info("Sending block request: {}", blockRequestDto);
                feignAuthService.tryCatchResponse(blockRequestDto, token);
            } else {
            updateService.updateEndDate(employeeId, endDate);
        }
    }

    private void unblockEmployee(UUID employeeId, String token) {
        log.info("Unblocking process for employeeId: {}", employeeId);
        String employeeIdString = employeeId.toString();
        BlockRequestDto blockRequestDto = new BlockRequestDto(employeeIdString, false);
        log.info("Sending unblock request: {}", blockRequestDto);
        feignAuthService.tryCatchResponse(blockRequestDto, token);
    }

    private void setEmployeeEndDate(Employee employee, UpdateEmployeeInfoDto updateEmployeeInfoDto, String token) {
        LocalDate currentDate = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDate();
        if (updateEmployeeInfoDto.getEndDate() == null) {
            log.info("Setting endDate to null");
            unblockEmployee(employee.getEmployeeId(), token);
            employee.setEndDate(null);
            updateEmployeeInfoDto.setEndDate(employee.getEndDate());
        } else {
            if (updateEmployeeInfoDto.getEndDate().isAfter(currentDate)) {
                throw new IllegalArgumentException("Дата увольнения не может быть больше текущей");
            }
            log.info("Setting endDate to: {}", updateEmployeeInfoDto.getEndDate());
            employee.setEndDate(updateEmployeeInfoDto.getEndDate());
            blockEmployee(employee.getEmployeeId(), updateEmployeeInfoDto.getEndDate(), token);
        }
    }
}

