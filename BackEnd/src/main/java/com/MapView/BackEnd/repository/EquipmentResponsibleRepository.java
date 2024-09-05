package com.MapView.BackEnd.repository;

import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.entities.EquipmentResponsible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentResponsibleRepository extends JpaRepository<EquipmentResponsible,Long> {
    List<EquipmentResponsible> findByOperativeTrueByIdEquipment(String id);
}
