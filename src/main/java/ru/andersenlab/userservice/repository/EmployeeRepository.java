package ru.andersenlab.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.andersenlab.userservice.domain.model.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    @Query("SELECT e FROM Employee e " +
            "JOIN e.office o " +
            "LEFT JOIN e.position p " +
            "WHERE :searchString IS NOT NULL AND (" +
            "LOWER(CONCAT(e.firstName, ' ', e.middleName, ' ', e.lastName)) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(CONCAT(e.firstName, ' ', e.lastName, ' ', e.middleName)) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(CONCAT(e.lastName, ' ', e.firstName, ' ', e.middleName)) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(CONCAT(e.lastName, ' ', e.middleName, ' ', e.firstName)) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(CONCAT(e.middleName, ' ', e.firstName, ' ', e.lastName)) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(CONCAT(e.middleName, ' ', e.lastName, ' ', e.firstName)) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(CONCAT(e.lastName, ' ', e.middleName)) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(CONCAT(e.lastName, ' ', e.firstName)) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(e.middleName) LIKE LOWER(CONCAT('%', :searchString, '%'))" +
            ") AND (p.namePosition IN :namePositions OR NOT EXISTS (SELECT 1 FROM Position pos WHERE pos.namePosition IN :namePositions)) " +
            " AND (o.city IN :cities OR NOT EXISTS (SELECT 1 FROM Citizenship c WHERE c.name IN :cities)) " +
            "AND (o.id IN :officeIds OR NOT EXISTS (SELECT 1 FROM Office o WHERE o.id IN :officeIds))")
    Page<Employee> findBySearchString(@Param("searchString") String searchString,
                                      @Param("namePositions") List<String> namePositions,
                                      @Param("cities") List<String> cities,
                                      @Param("officeIds") List<Integer> officeIds,
                                      Pageable pageable);


    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(:firstName) IN (LOWER(e.firstName), LOWER(e.middleName), LOWER(e.lastName)) AND " +
            "LOWER(:middleName) IN (LOWER(e.firstName), LOWER(e.middleName), LOWER(e.lastName)) AND " +
            "LOWER(:lastName) IN (LOWER(e.firstName), LOWER(e.middleName), LOWER(e.lastName))"
    )
    List<Employee> searchByFullName(@Param("firstName") String firstName,
                                    @Param("lastName") String lastName,
                                    @Param("middleName") String middleName);


    @Query("UPDATE Employee e SET e.endDate = :endDate WHERE e.employeeId = :employeeId")
    void updateEndDate(@Param("employeeId") UUID employeeId, @Param("endDate") LocalDate endDate);


    @Query("SELECT DISTINCT o.city " +
            "FROM Employee e " +
            "JOIN e.office o " +
            "JOIN e.position p " +
            "WHERE o.id IN :officeIds AND p.namePosition IN :namePositions")
    List<String> getFilteredCities(@Param("officeIds") List<Integer> officeIds, @Param("namePositions") List<String> namePositions);

    @Query("SELECT DISTINCT p.namePosition " +
            "FROM Employee e " +
            "JOIN e.office o " +
            "JOIN e.position p " +
            "WHERE o.id IN :officeIds AND o.city IN :cities")
    List<String> getFilteredPosition(List<Integer> officeIds, List<String> cities);

    @Query("SELECT DISTINCT o.id " +
            "FROM Employee e " +
            "JOIN e.office o " +
            "JOIN e.position p " +
            "WHERE o.city IN :cities AND p.namePosition IN :namePositions")
    List<Integer> getFilteredOfficeId(List<String> namePositions, List<String> cities);
}