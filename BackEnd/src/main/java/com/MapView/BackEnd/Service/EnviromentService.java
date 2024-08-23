package com.MapView.BackEnd.Service;

import com.MapView.BackEnd.dtos.Enviroment.EnviromentCreateDTO;
import com.MapView.BackEnd.dtos.Enviroment.EnviromentDetailsDTO;
import com.MapView.BackEnd.dtos.Enviroment.EnviromentUpdateDTO;
import com.MapView.BackEnd.entities.Raspberry;

import java.util.List;

public interface EnviromentService {

    EnviromentDetailsDTO getEnvioment(Long enviroment_id);
    List<EnviromentDetailsDTO> getAllEnvioment();
    EnviromentDetailsDTO createEnviroment(EnviromentCreateDTO data);
    EnviromentDetailsDTO updateEnviroment(Long enviroment_id,EnviromentUpdateDTO data);
    void activateEnviroment(Long id_environment);
    void inactivateEnviroment(Long id_environment);
}
 