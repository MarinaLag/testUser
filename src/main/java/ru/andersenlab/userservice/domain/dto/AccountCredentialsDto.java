package ru.andersenlab.userservice.domain.dto;

import lombok.Data;

@Data
public class AccountCredentialsDto {
    private String email;
    private Integer access_level;
    private String password;
    private boolean is_blocked;
}
