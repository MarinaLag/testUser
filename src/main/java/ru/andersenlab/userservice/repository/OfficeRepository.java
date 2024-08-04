package ru.andersenlab.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andersenlab.userservice.domain.model.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
}
