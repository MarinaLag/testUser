package ru.andersenlab.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andersenlab.userservice.domain.model.Position;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {
}
