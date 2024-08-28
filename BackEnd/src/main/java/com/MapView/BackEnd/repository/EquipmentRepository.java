package com.MapView.BackEnd.repository;

import com.MapView.BackEnd.entities.Equipment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment,String> {
    List<Equipment> findAllByOperativeTrue(Pageable pageable);

    Optional<Equipment> findByValidity(String validity);
}
