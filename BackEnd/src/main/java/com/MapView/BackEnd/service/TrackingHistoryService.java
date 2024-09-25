package com.MapView.BackEnd.service;

import com.MapView.BackEnd.dtos.Equipment.EquipmentDetailsDTO;
import com.MapView.BackEnd.dtos.TrackingHistory.TrackingHistoryCreateDTO;
import com.MapView.BackEnd.dtos.TrackingHistory.TrackingHistoryDetailsDTO;
import com.MapView.BackEnd.enums.EnumColors;
import com.MapView.BackEnd.enums.EnumTrackingAction;

import java.util.List;

public interface TrackingHistoryService {

    TrackingHistoryDetailsDTO getTrackingHistory(Long id_tracking);
    List<TrackingHistoryDetailsDTO> getAllTrackingHistory(int page, int itens);
    TrackingHistoryDetailsDTO createTrackingHistory(TrackingHistoryCreateDTO dados);
    List<TrackingHistoryDetailsDTO> FilterTracking(int page, int itens, EnumTrackingAction action,
                                                   Integer day, Integer month, Integer year, EnumColors colors,
                                                   String id_equipment);
    List<EquipmentDetailsDTO> findWrongLocationEquipments (Long id_enviromet);
}
