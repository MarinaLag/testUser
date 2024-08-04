package ru.andersenlab.userservice.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.andersenlab.userservice.domain.dto.*;
import ru.andersenlab.userservice.domain.model.Employee;
import ru.andersenlab.userservice.domain.model.Office;
import ru.andersenlab.userservice.domain.model.PersonalData;
import ru.andersenlab.userservice.domain.model.Position;
import ru.andersenlab.userservice.repository.*;
import ru.andersenlab.userservice.service.EmployeeService;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

public class EmployeeControllerImplTest {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CitizenshipRepository citizenshipRepository;
    @Autowired
    private OfficeRepository officeRepository;
    @Autowired
    private PersonalDataRepository personalDataRepository;
    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String secret;

    private UUID employeeId;
    private UpdateEmployeeInfoDto updateEmployeeInfoDto;
    private ChangeAccessRequestDto changeAccessRequestDto;

    @BeforeEach
    public void setUp() {
        employeeId = UUID.fromString("450e8400-e29b-41d4-a716-446655440000");
        updateEmployeeInfoDto = new UpdateEmployeeInfoDto();
        updateEmployeeInfoDto.setFirstName("Никита");
        updateEmployeeInfoDto.setLastName("Ефимцов");
        updateEmployeeInfoDto.setMiddleName("Сергеевич");
        updateEmployeeInfoDto.setBirthday(LocalDate.of(1990, 1, 1));
        updateEmployeeInfoDto.setWorkNumber("741241412");
        updateEmployeeInfoDto.setStartDate(LocalDate.of(2010, 1, 1));
        updateEmployeeInfoDto.setEndDate(LocalDate.of(2020, 2, 4));
        updateEmployeeInfoDto.setPositionId(1);
        updateEmployeeInfoDto.setOfficeId(2);
        updateEmployeeInfoDto.setPassportSeries("MP");
        updateEmployeeInfoDto.setPassportNumber("5671890");
        updateEmployeeInfoDto.setDateIssued(LocalDate.of(2010, 1, 1));
        updateEmployeeInfoDto.setIssuedBy("Рувд");
        updateEmployeeInfoDto.setCitizenshipId(1);
        updateEmployeeInfoDto.setAddress("Гомель, ул. Правды 1");
        updateEmployeeInfoDto.setPhoneNumber("375441234567");
        updateEmployeeInfoDto.setAccessLevelId(1);

        changeAccessRequestDto = new ChangeAccessRequestDto();
        changeAccessRequestDto.setEmployeeId(employeeId);
        changeAccessRequestDto.setLevelAccessId(1);

        employeeRepository.deleteAll();
        personalDataRepository.deleteAll();
        positionRepository.deleteAll();
        officeRepository.deleteAll();
        citizenshipRepository.deleteAll();
    }

    @Test
    void testGetEmployee() throws Exception {
        EmployeeInfoDto dto = new EmployeeInfoDto(
                "John",
                "M",
                "Doe",
                "Developer",
                "1990-01-01",
                1,
                "12345",
                "2020-01-01",
                "1990-01-01",
                "123 Main St",
                "Активен"
        );
        when(employeeService.getEmployeeById(employeeId)).thenReturn(dto);
        String token = generateTestJWT();
        mockMvc.perform(get("/api/v1/userservice/{employee_id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void testGetEmployeeNotFound() throws Exception {
        when(employeeService.getEmployeeById(employeeId)).thenReturn(null);
        String token = generateTestJWT();
        mockMvc.perform(get("/api/v1/userservice/{employee_id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetEmployeeList() throws Exception {
        EmployeeDto employee1 = new EmployeeDto();
        EmployeeDto employee2 = new EmployeeDto();
        EmployeeDto inputDto = new EmployeeDto();
        String token = generateTestJWT();
        List<EmployeeDto> expectedEmployees = Arrays.asList(employee1, employee2);

        when(employeeService.getOrganizationAllUsers(inputDto)).thenReturn(expectedEmployees);

        mockMvc.perform(post("/api/v1/userservice/employees")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedEmployees.size()))
                .andExpect(jsonPath("$[0]").value(objectMapper.convertValue(employee1, Map.class)))
                .andExpect(jsonPath("$[1]").value(objectMapper.convertValue(employee2, Map.class)));

        verify(employeeService, times(1)).getOrganizationAllUsers(inputDto);
    }

  /*  @Test
    void testGetEmployeeIdMy() throws Exception {
        EmployeeInfoDto employeeInfoDto = new EmployeeInfoDto(
                "Василий",
                "Петрович",
                "Пупкин",
                "Бухгалтер",
                "1990-05-21",
                1,
                "+78123215476",
                "2024-02-05",
                "1990-05-21",
                "Лахденпохья, ул. Советская д.43",
                "Активен"
        );
        when(employeeService.getEmployeeById(employeeId)).thenReturn(employeeInfoDto);

        mockMvc.perform(get("/api/v1/userservice/{employee_id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.employeeId").value("450e8400-e29b-41d4-a716-446655440000"))     //?????????????
                .andExpect(jsonPath("$.firstName").value("Василий"))
                .andExpect(jsonPath("$.middleName").value("Петрович"))
                .andExpect(jsonPath("$.lastName").value("Пупкин"))
                .andExpect(jsonPath("$.jobTitle").value("Бухгалтер"))
                .andExpect(jsonPath("$.birthday").value("1990-05-21"))
                .andExpect(jsonPath("$.office_id").value(1))
                .andExpect(jsonPath("$.workNumberPhone").value("+78123215476"))
                .andExpect(jsonPath("$.startDate").value("2024-02-05"))
                .andExpect(jsonPath("$.birthdayDate").value("1990-05-21"))
                .andExpect(jsonPath("$.address").value("Лахденпохья, ул. Советская д.43"))
                .andExpect(jsonPath("$.status").value("Активен"));

        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }
*/

    private String generateTestJWT() {
        return JWT.create()
                .withSubject("anna.smirnova@x-bank.com")
                .withClaim("employee_id", "550e8400-e29b-41d4-a716-446655440000")
                .withClaim("access_level_id", "Руководитель отделения")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 300_000))
                .sign(Algorithm.HMAC512(secret));
    }

    private String generateTestJWTSuperUser() {
        return JWT.create()
                .withSubject("alexey.kuznetsov@x-bank.com")
                .withClaim("employee_id", "450e8400-e29b-41d4-a716-446655440000")
                .withClaim("access_level_id", "СуперЮзер")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 300_000))
                .sign(Algorithm.HMAC512(secret));
    }

    @Test
    public void testUpdateEmployee() throws Exception {
        String token = generateTestJWTSuperUser();

        when(employeeService.editEmployeeInfo(employeeId, updateEmployeeInfoDto, token))
                .thenReturn(updateEmployeeInfoDto);

        mockMvc.perform(patch("/api/v1/userservice/employees/{employee_id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateEmployeeInfoDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testChangeAccessLevel() throws Exception {
        when(employeeService.changeEmployeeAccessLevel(employeeId, changeAccessRequestDto.getLevelAccessId())).thenReturn(changeAccessRequestDto);

        String token = generateTestJWT();
        mockMvc.perform(patch("/api/v1/userservice/employees/change_level/{employee_id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(changeAccessRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(changeAccessRequestDto.getEmployeeId().toString()))
                .andExpect(jsonPath("$.levelAccessId").value(changeAccessRequestDto.getLevelAccessId()));
    }

    @Test
    void testGetEmployeeByFullName() throws Exception {
        Office office = new Office()
                .setCity("Гомель")
                .setAddress("пр-т Ленина д.34");
        officeRepository.save(office);

        EmployeeInfoDto employeeInfoDto = new EmployeeInfoDto(
                "Василий",
                "Петрович",
                "Пупкин",
                "Бухгалтер",
                "1990-05-21",
                office.getId(),
                "+78123215476",
                "2024-02-05",
                "1990-05-21",
                "Лахденпохья, ул. Советская д.43",
                "Активен"
        );
        PersonalData personalData = new PersonalData()
                .setAddress(employeeInfoDto.getAddress());
        personalDataRepository.save(personalData);

        Position position = new Position().setNamePosition(employeeInfoDto.getJobTitle());
        positionRepository.save(position);


        Employee employee = new Employee();
        employee.setOffice(office);
        employee.setWorkNumber(employeeInfoDto.getWorkNumberPhone());
        employee.setPosition(position);
        employee.setPersonalData(personalData);
        employee.setFirstName(employeeInfoDto.getFirstName());
        employee.setMiddleName(employeeInfoDto.getMiddleName());
        employee.setLastName(employeeInfoDto.getLastName());
        employee.setStartDate(LocalDate.parse(employeeInfoDto.getStartDate()));
        employee.setBirthdayDate(LocalDate.parse(employeeInfoDto.getBirthdayDate()));
        employeeRepository.save(employee);

        EmployeeSearchDto employeeSearchDto = new EmployeeSearchDto();
        employeeSearchDto.setFirstName(employeeInfoDto.getFirstName());
        employeeSearchDto.setMiddleName(employeeInfoDto.getMiddleName());
        employeeSearchDto.setLastName(employeeInfoDto.getLastName());

        String employeeSearchDtoJson = new ObjectMapper().writeValueAsString(employeeSearchDto);
        String expectedResponse = new ObjectMapper().writeValueAsString(List.of(employeeInfoDto));

        String jwtToken = generateTestJWT();

        mockMvc.perform(post("/api/v1/userservice/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeSearchDtoJson)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }
}
