package com.MapView.BackEnd.repository;

import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.enums.EnumModelEquipment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment,String> {
    List<Equipment> findAllByOperativeTrue(Pageable pageable);
    List<Equipment> findAllByOperativeTrue();

    Optional<Equipment> findByValidity(String validity);
    List<Equipment> findByModel(EnumModelEquipment equipment);
}
