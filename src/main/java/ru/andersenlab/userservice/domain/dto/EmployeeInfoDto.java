package ru.andersenlab.userservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;


@Value
@Schema(description = "Просмотр карточки сотрудника банка")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class EmployeeInfoDto {

    @Schema(description = "Имя сотрудника")
    String firstName;

    @Schema(description = "Отчество сотрудника")
    String middleName;

    @Schema(description = "Фамилия сотрудника")
    String lastName;

    @Schema(description = "Должность")
    String jobTitle;


    @Schema(description = "Дата рождения пользователя")
    String birthday;

    @Schema(description = "Номер офиса")
    Integer office_id;

    @Schema(description = "Номер телефона")
    String workNumberPhone;

    @Schema(description = "Дата начала работы")
    String startDate;

    @Schema(description = "День рождения")
    String birthdayDate;

    @Schema(description = "Адрес")
    String address;

    @Schema(description = "Статус сотрудника")
    String status;

}

