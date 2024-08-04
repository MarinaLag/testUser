package ru.andersenlab.userservice.domain.dto;

import java.util.List;

public record FilterRequestDto(List<Integer> officeIds, List<String> namePositions, List<String> cities) {
}