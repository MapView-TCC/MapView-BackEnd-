package com.MapView.BackEnd.repository;

import com.MapView.BackEnd.entities.Enviroment;
import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.entities.Raspberry;
import com.MapView.BackEnd.entities.TrackingHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingHistoryRepository extends JpaRepository<TrackingHistory,Long> {
    Page<TrackingHistory> findAll(Pageable pageable);
    TrackingHistory findTopByEquipmentOrderByDatetimeDesc(Equipment id_equipment);
    List<TrackingHistory> findByEnvironment(Enviroment enviroment);
}
