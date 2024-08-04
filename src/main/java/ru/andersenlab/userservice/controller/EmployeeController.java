package ru.andersenlab.userservice.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.andersenlab.userservice.domain.dto.EmployeeDto;
import ru.andersenlab.userservice.domain.dto.EmployeeInfoDto;
import ru.andersenlab.userservice.domain.dto.EmployeeRegisterDto;
import ru.andersenlab.userservice.domain.dto.EmployeeSearchDto;
import ru.andersenlab.userservice.domain.dto.ChangeAccessRequestDto;
import ru.andersenlab.userservice.domain.dto.UpdateEmployeeInfoDto;
import ru.andersenlab.userservice.domain.dto.FilterResponseDto;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Validated
@RequestMapping("api/v1/userservice")
public interface EmployeeController {

    @PostMapping("/employees")
    List<EmployeeDto> getEmployeeList(@RequestBody EmployeeDto employeeDto);

    @GetMapping("/{employee_id}")
    ResponseEntity<?> getEmployee(@PathVariable("employee_id") UUID employeeId);

    @PostMapping("/search")
    List<EmployeeInfoDto> getEmployeeByFullName(@Valid @RequestBody EmployeeSearchDto employeeSearchDto);

    @GetMapping("/employees/filter/city")
    ResponseEntity<FilterResponseDto> getFilteredCities(
            @RequestParam(required = false) List<Integer> office_ids,
            @RequestParam(required = false) List<String> name_positions);

    @GetMapping("/employees/filter/position")
    ResponseEntity<FilterResponseDto> getFilteredPosition(@RequestParam List<String> cities,
                                                          @RequestParam List<Integer> officeIds);

    @GetMapping("/employees/filter/office_id")
    ResponseEntity<FilterResponseDto> getFilteredOfficeId(
            @RequestParam(required = false) List<String> cities,
            @RequestParam(required = false) List<String> namePosition);

    @PostMapping
    ResponseEntity<String> getEmployeeForRegister(@RequestBody EmployeeRegisterDto employeeRegisterDto,
                                                  @RequestHeader("Authorization") String token);


    @PatchMapping("/employees/{employee_id}")
    ResponseEntity<?> updateEmployee(@PathVariable("employee_id") UUID employeeId,
                                     @RequestBody UpdateEmployeeInfoDto updateEmployeeInfoDto,
                                     @RequestHeader("Authorization")String token);

    @PatchMapping("/employees/change_level/{employee_id}")
    ResponseEntity<?> changeAccessLevel(@PathVariable("employee_id") UUID employeeId,
                                        @RequestBody ChangeAccessRequestDto changeAccessRequestDto);
}

