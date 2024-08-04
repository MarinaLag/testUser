package ru.andersenlab.userservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeRegisterDto {
    @NotNull(message = "Номер не может быть пустым")
    private String number;

    @JsonProperty("passport_series")
    @NotNull(message = "Серия паспорта не может быть пустым")
    private String passportSeries;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @JsonProperty("data_issued")
    @NotNull(message = "Поле не может быть пустым")
    private String dataIssued;

    @JsonProperty("citizenship_id")
    @NotNull(message = "Город не может быть пустым")
    private Integer citizenshipId;

    @JsonProperty("last_name")
    @NotNull(message = "Фамилия не может быть пустым")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+$",message = "Допустимы только латинские буквы и кириллица!")
    private String lastName;

    @JsonProperty("first_name")
    @NotNull(message = "Имя не может быть пустым")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+$",message = "Допустимы только латинские буквы и кириллица!")
    private String firstName;

    @JsonProperty("middle_name")
    @NotNull(message = "Отчество не может быть пустым")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+$",message = "Допустимы только латинские буквы и кириллица!")
    private String middleName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @NotNull(message = "Дата рождения не может быть пустым")
    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @JsonProperty("work_email")
    @Email(message = "Email should be valid")
    @NotNull(message = "Email не может быть пустым")
    private String workEmail;

    @JsonProperty("phone_number")
    @NotNull(message = "Номер телефона не может быть пустым")
    private String phoneNumber;

    @NotNull(message = "Адрес не может быть пустым")
    public String address;

    @NotNull(message = "ID офиса не может быть пустым")
    @JsonProperty("office_id")
    private Long officeId;

    @JsonProperty("position_id")
    @NotNull(message = "ID позиции не может быть пустым")
    private Long positionId;

    @JsonProperty("start_date")
    @NotNull(message = "Время не может быть пустым")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private String startDate;

    @JsonProperty("end_date")
    @NotNull(message = "Время не может быть пустым")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private String endDate;

    @JsonProperty("access_level_id")
    @NotNull(message = "Уровень доступа не может быть пустым")
    private Integer accessLevelId;

    @NotNull
    private String password;

    @JsonProperty("is_blocked")
    @NotNull
    private boolean isBlocked;
}
