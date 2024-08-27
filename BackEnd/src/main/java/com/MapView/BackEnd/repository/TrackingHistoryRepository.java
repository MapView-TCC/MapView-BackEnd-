package com.MapView.BackEnd.repository;

import com.MapView.BackEnd.entities.TrackingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingHistoryRepository extends JpaRepository<TrackingHistory,Long> {
}