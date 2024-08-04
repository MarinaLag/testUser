package ru.andersenlab.userservice.domain.dto;

import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter
public class EmployeeDto {

    List<String> cities;

    List<Integer> office_ids;

    List<String> name_positions;

    String search_string;

    Integer page = 1;

    Integer page_size = 8;
}
