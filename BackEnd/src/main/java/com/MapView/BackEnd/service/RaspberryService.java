package com.MapView.BackEnd.service;

import com.MapView.BackEnd.dtos.Raspberry.RaspberryCreateDTO;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryDetailsDTO;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryUpdateDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RaspberryService {

    RaspberryDetailsDTO getRaspberry(Long id_Raspberry, Long user_id);
    List<RaspberryDetailsDTO> getAllRaspberry(int page, int itens, Long user_id);
    RaspberryDetailsDTO createRaspberry(RaspberryCreateDTO raspberryCreateDTO, Long user_id);
    RaspberryDetailsDTO updateRaspberry(Long id_raspberry, RaspberryUpdateDTO dados, Long user_id);
    void activeRaspberry(Long id_Raspberry, Long user_id);
    void inactivateRaspberry(Long id_Raspberry,Long user_id);
}
