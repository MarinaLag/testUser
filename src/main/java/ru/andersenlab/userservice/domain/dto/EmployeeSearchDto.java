package ru.andersenlab.userservice.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class EmployeeSearchDto {
    @NotNull(message = "Поле firstName не может быть пустым")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+$", message = "Допустимы только латинские буквы и кириллица!")
    String firstName;
    @NotNull(message = "Поле lastName не может быть пустым")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+$", message = "Допустимы только латинские буквы и кириллица!")
    String lastName;
    @NotNull(message = "Поле middleName не может быть пустым")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+$", message = "Допустимы только латинские буквы и кириллица!")
    String middleName;
}
