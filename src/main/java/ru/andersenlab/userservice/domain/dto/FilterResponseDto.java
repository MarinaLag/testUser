package ru.andersenlab.userservice.domain.dto;

import java.util.List;

public record FilterResponseDto(List<Integer> officeIds, List<String> namePositions, List<String> cities) {
}