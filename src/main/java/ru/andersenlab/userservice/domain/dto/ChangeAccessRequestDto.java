package ru.andersenlab.userservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeAccessRequestDto {
    private UUID employeeId;

    private Integer levelAccessId;
}
