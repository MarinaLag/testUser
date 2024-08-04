package ru.andersenlab.userservice.domain.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.andersenlab.userservice.domain.dto.UpdateEmployeeInfoDto;
import org.mapstruct.factory.Mappers;
import ru.andersenlab.userservice.domain.dto.EmployeeRegisterDto;
import ru.andersenlab.userservice.domain.model.Citizenship;
import ru.andersenlab.userservice.domain.model.Employee;
import ru.andersenlab.userservice.domain.model.Office;
import ru.andersenlab.userservice.domain.model.PersonalData;
import ru.andersenlab.userservice.domain.model.Position;

@Component
@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "middleName", target = "middleName")
    @Mapping(source = "birthdayDate", target = "birthday")
    @Mapping(source = "workNumber", target = "workNumber")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "position.id", target = "positionId")
    @Mapping(source = "office.id", target = "officeId")
    @Mapping(source = "personalData.passportSeries", target = "passportSeries")
    @Mapping(source = "personalData.passportNumber", target = "passportNumber")
    @Mapping(source = "personalData.dateIssued", target = "dateIssued")
    @Mapping(source = "personalData.issuedBy", target = "issuedBy")
    @Mapping(source = "personalData.citizenship.id", target = "citizenshipId")
    @Mapping(source = "personalData.address", target = "address")
    @Mapping(source = "personalData.phoneNumber", target = "phoneNumber")
    UpdateEmployeeInfoDto toDto(Employee employee);

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(source = "employeeRegisterDto.address", target = "address")
    @Mapping(source = "employeeRegisterDto.phoneNumber", target = "phoneNumber")
    @Mapping(source = "employeeRegisterDto.dataIssued", target = "dateIssued", dateFormat = "dd.MM.yyyy")
    @Mapping(source = "employeeRegisterDto.passportSeries", target = "passportSeries")
    @Mapping(source = "employeeRegisterDto.number", target = "passportNumber")
    @Mapping(source = "citizenship", target = "citizenship")
    PersonalData toPersonalData(EmployeeRegisterDto employeeRegisterDto, Citizenship citizenship);

    @Mapping(source = "employeeRegisterDto.firstName", target = "firstName")
    @Mapping(source = "employeeRegisterDto.middleName", target = "middleName")
    @Mapping(source = "employeeRegisterDto.lastName", target = "lastName")
    @Mapping(source = "employeeRegisterDto.birthDate", target = "birthdayDate", dateFormat = "dd.MM.yyyy")
    @Mapping(source = "employeeRegisterDto.startDate", target = "startDate", dateFormat = "dd.MM.yyyy")
    @Mapping(source = "employeeRegisterDto.endDate", target = "endDate", dateFormat = "dd.MM.yyyy")
    @Mapping(source = "personalData", target = "personalData")
    @Mapping(source = "position", target = "position")
    @Mapping(source = "office", target = "office")
    Employee toEmployee(EmployeeRegisterDto employeeRegisterDto, PersonalData personalData, Position position, Office office);
}