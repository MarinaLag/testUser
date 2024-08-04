package ru.andersenlab.userservice.controller.impl;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.andersenlab.userservice.controller.EmployeeController;
import ru.andersenlab.userservice.domain.dto.ChangeAccessRequestDto;
import ru.andersenlab.userservice.domain.dto.EmployeeDto;
import ru.andersenlab.userservice.domain.dto.EmployeeInfoDto;
import ru.andersenlab.userservice.domain.dto.EmployeeRegisterDto;
import ru.andersenlab.userservice.domain.dto.EmployeeSearchDto;
import ru.andersenlab.userservice.domain.dto.FilterRequestDto;
import ru.andersenlab.userservice.domain.dto.FilterResponseDto;
import ru.andersenlab.userservice.domain.dto.UpdateEmployeeInfoDto;
import ru.andersenlab.userservice.domain.exception.ResourceNotFoundException;
import ru.andersenlab.userservice.service.EmployeeService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/userservice")
public class EmployeeControllerImpl implements EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeControllerImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @PostMapping("/employees")
    public List<EmployeeDto> getEmployeeList(@RequestBody EmployeeDto employeeDto) {
        return employeeService.getOrganizationAllUsers(employeeDto);
    }

    @Override
    @GetMapping("/{employee_id}")
    public ResponseEntity<?> getEmployee(@PathVariable("employee_id") UUID employeeId) {
        return Optional.ofNullable(employeeService.getEmployeeById(employeeId))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Не найдено"));
    }

    @Override
    @PostMapping("/search")
    public List<EmployeeInfoDto> getEmployeeByFullName(@Valid @RequestBody EmployeeSearchDto employeeSearchDto) {
        return employeeService.getEmployeeByFullName(employeeSearchDto);
    }

    @Override
    @GetMapping("/employees/filter/city")
    public ResponseEntity<FilterResponseDto> getFilteredCities(@RequestParam List<Integer> officeIds,
                                                               @RequestParam List<String> namePositions) {
        FilterRequestDto filterRequestDto = new FilterRequestDto(officeIds, namePositions, null);
        FilterResponseDto response = employeeService.getFilteredCities(filterRequestDto);
        return ResponseEntity.ok(response);
    }


    @Override
    @GetMapping("/employees/filter/position")
    public ResponseEntity<FilterResponseDto> getFilteredPosition(@RequestParam List<String> cities,
                                                                 @RequestParam List<Integer> officeIds) {
        FilterRequestDto filterRequestDto = new FilterRequestDto(officeIds, null, cities);
        FilterResponseDto filterResponseDto = employeeService.getFilteredPosition(filterRequestDto);
        return ResponseEntity.ok(filterResponseDto);

    }

    @Override
    @GetMapping("/employees/filter/office_id")
    public ResponseEntity<FilterResponseDto> getFilteredOfficeId(@RequestParam List<String> cities,
                                                                 @RequestParam List<String> namePosition) {
        FilterRequestDto filterRequestDto = new FilterRequestDto(null, cities, namePosition);
        FilterResponseDto filterResponseDto = employeeService.getFilteredOfficeId(filterRequestDto);
        return ResponseEntity.ok(filterResponseDto);
    }

    @Override
    @PatchMapping("/employees/{employee_id}")
    public ResponseEntity<?> updateEmployee(@PathVariable("employee_id") UUID employeeId,
                                            @RequestBody UpdateEmployeeInfoDto updateEmployeeInfoDto,
                                            @RequestHeader("Authorization") String token) {
        UpdateEmployeeInfoDto dto = employeeService.editEmployeeInfo(employeeId, updateEmployeeInfoDto, token);
        return ResponseEntity.ok(dto);
    }

    @Override
    @PatchMapping("/employees/change_level/{employee_id}")
    public ResponseEntity<?> changeAccessLevel(@PathVariable("employee_id") UUID employeeId,
                                               @RequestBody ChangeAccessRequestDto changeAccessRequestDto) {
        ChangeAccessRequestDto dto = employeeService.changeEmployeeAccessLevel(employeeId, changeAccessRequestDto.getLevelAccessId());
        return ResponseEntity.ok(dto);
    }
        @Override
        @PostMapping("/create")
        @PreAuthorize("hasAnyRole('ROLE_СуперЮзер')")
        public ResponseEntity<String> getEmployeeForRegister(@RequestBody EmployeeRegisterDto employeeRegisterDto,
                                                             @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(employeeService.createEmployee(employeeRegisterDto, token));

    }
}
