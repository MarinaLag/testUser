package ru.andersenlab.userservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@Schema(description = "Просмотр карточки сотрудника банка")
@NoArgsConstructor(force = true)
public class UpdateEmployeeInfoDto {

    @Schema(description = "Имя сотрудника")
    @JsonProperty("first_name")
    String firstName;

    @Schema(description = "Отчество сотрудника")
    @JsonProperty("middle_Name")
    String middleName;

    @Schema(description = "Фамилия сотрудника")
    @JsonProperty("last_Name")
    String lastName;

    @Schema(description = "Серия паспорта")
    @JsonProperty("passport_series")
    String passportSeries;

    @Schema(description = "Номер паспорта")
    @JsonProperty("passport_number")
    String passportNumber;

    @Schema(description = "Дата выдачи паспорта")
    @JsonProperty("date_issued")
    LocalDate dateIssued;

    @Schema(description = "Кем выдан паспорт")
    @JsonProperty("issued_by")
    String issuedBy;

    @Schema(description = "Идентификатор гражданства сотрудника")
    @JsonProperty("citizenship_id")
    Integer citizenshipId;

    @Schema(description = "Адрес регистрации сотрудника")
    @JsonProperty("address")
    String address;

    @Schema(description = "Дата рождения сотрудника")
    @JsonProperty("birthday_date")
    LocalDate birthday;

    @Schema(description = "Личный номер телефона сотрудника")
    @JsonProperty("phone_number")
    String phoneNumber;

    @Schema(description = "Рабочий номер телефона сотрудника")
    @JsonProperty("work_number")
    String workNumber;

    @Schema(description = "Идентификатор должности сотрудника")
    @JsonProperty("position_id")
    Integer positionId;

    @Schema(description = "Дата начала работы в банке")
    @JsonProperty("start_date")
    LocalDate startDate;

    @Schema(description = "Дата увольнения сотрудника банка")
    @JsonProperty("end_date")
    LocalDate endDate;

    @Schema(description = "Идентификатор офиса банка")
    @JsonProperty("office_id")
    Integer officeId;

    @Schema(description = "Роль")
    @JsonProperty("access_level_id")
    Integer accessLevelId;
}
