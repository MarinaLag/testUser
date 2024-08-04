package ru.andersenlab.userservice.service;

import ru.andersenlab.userservice.domain.dto.EmployeeInfoDto;
import ru.andersenlab.userservice.domain.dto.EmployeeRegisterDto;
import ru.andersenlab.userservice.domain.dto.FilterResponseDto;
import ru.andersenlab.userservice.domain.dto.UpdateEmployeeInfoDto;
import ru.andersenlab.userservice.domain.dto.EmployeeDto;
import ru.andersenlab.userservice.domain.dto.ChangeAccessRequestDto;
import ru.andersenlab.userservice.domain.dto.EmployeeSearchDto;
import ru.andersenlab.userservice.domain.dto.FilterRequestDto;
import ru.andersenlab.userservice.domain.exception.InvalidRequestException;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    EmployeeInfoDto getEmployeeById(UUID employeeId) throws InvalidRequestException;

    List<EmployeeDto> getOrganizationAllUsers(EmployeeDto employeeDto);

    List<EmployeeInfoDto> getEmployeeByFullName(EmployeeSearchDto employeeSearchDto);

    String createEmployee(EmployeeRegisterDto employeeRegisterDto, String token);

    UpdateEmployeeInfoDto editEmployeeInfo(UUID employeeId, UpdateEmployeeInfoDto updateEmployeeInfoDto, String token);

    ChangeAccessRequestDto changeEmployeeAccessLevel(UUID employeeId, Integer levelAccessId);


    FilterResponseDto getFilteredCities(FilterRequestDto filterRequestDto);

    FilterResponseDto getFilteredPosition(FilterRequestDto filterRequestDto);

    FilterResponseDto getFilteredOfficeId(FilterRequestDto filterRequestDto);
}

