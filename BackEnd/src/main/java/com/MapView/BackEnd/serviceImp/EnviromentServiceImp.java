package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.EnviromentRepository;
import com.MapView.BackEnd.repository.RaspberryRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.EnviromentService;
import com.MapView.BackEnd.dtos.Enviroment.EnviromentCreateDTO;
import com.MapView.BackEnd.dtos.Enviroment.EnviromentDetailsDTO;
import com.MapView.BackEnd.dtos.Enviroment.EnviromentUpdateDTO;
import com.MapView.BackEnd.entities.Enviroment;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class EnviromentServiceImp implements EnviromentService {

    private final EnviromentRepository enviromentRepository;
    private final RaspberryRepository raspberryRepository;
    private final UserLogRepository userLogRepository;

    private final UserRepository userRepository;

    public EnviromentServiceImp(EnviromentRepository enviromentRepository, RaspberryRepository raspberryRepository, UserLogRepository userLogRepository, UserRepository userRepository) {
        this.enviromentRepository = enviromentRepository;
        this.raspberryRepository = raspberryRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EnviromentDetailsDTO getEnviroment(Long enviroment_id, Long user_id) {
        var enviroment = enviromentRepository.findById(enviroment_id).orElseThrow(() -> new NotFoundException("Enviroment Id Not Found"));
        if(!enviroment.isOperative()){
            throw new OperativeFalseException("C");
        }
        var userLog = new UserLog(null,"Enviroment",enviroment_id.toString(),"Read Enviroment", EnumAction.READ);
        userLogRepository.save(userLog);

        return new EnviromentDetailsDTO(enviroment);
    }

    @Override
    public List<EnviromentDetailsDTO> getAllEnviroment(int page, int itens, Long user_id) {
        var userLog = new UserLog(null,"Enviroment","Read All Enviroment", EnumAction.READ);
        userLogRepository.save(userLog);

        return this.enviromentRepository.findEnviromentByOperativeTrue(PageRequest.of(page, itens)).stream().map(EnviromentDetailsDTO::new).toList();
    }

    @Override
    public EnviromentDetailsDTO createEnviroment(EnviromentCreateDTO data, Long user_id) {
        var rasp = raspberryRepository.findById(data.id_raspberry()).orElseThrow(()->new NotFoundException("Id Raspberry Not Found"));
        var enviroment = new Enviroment(data, rasp);

        Long id_enviroment = enviromentRepository.save(enviroment).getId_environment();

        Users users = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found!"));

        var userLog = new UserLog(users,"Enviroment", id_enviroment.toString(),"Create new Enviroment", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new EnviromentDetailsDTO(enviroment);
    }

    @Override
    public EnviromentDetailsDTO updateEnviroment(Long enviroment_id,EnviromentUpdateDTO data, Long user_id) {
        var user = userRepository.findById(user_id).orElseThrow(()->new NotFoundException("Id Enviroment Not Found"));

        var enviroment = enviromentRepository.findById(enviroment_id).orElseThrow(()->new NotFoundException("Id Enviroment Not Found"));
        if(!enviroment.isOperative()){
            throw new OperativeFalseException("The inactive enviroment cannot be updated.");
        }

        var userlog = new UserLog(user,"Enviroment",enviroment_id.toString(),null,"Update Enviroment: ",EnumAction.UPDATE);

        if (data.environment_name() != null){
            enviroment.setEnvironment_name(data.environment_name());
            userlog.setField("enviroment_name");
            userlog.setDescription("enviroment_name to: " + data.environment_name());
        }
        if (data.id_raspberry() != null){
            var rasp = raspberryRepository.findById(data.id_raspberry()).orElseThrow(()->new NotFoundException("Id Raspberry Not Found"));
            if(rasp.isOperative()){
                throw new OperativeFalseException("The inactive raspberry cannot be updated.");
            }
            enviroment.setRaspberry(rasp);
            userlog.setField("id_raspberry");
            userlog.setDescription("id_raspberry to: ");
        }

        enviromentRepository.save(enviroment);
        userLogRepository.save(userlog);


        return new EnviromentDetailsDTO(enviroment);
    }

    @Override
    public void activateEnviroment(Long id_environment, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var enviroment = enviromentRepository.findById(id_environment).orElseThrow(()->new NotFoundException("Id Enviroment Not Found"));
        if(!enviroment.isOperative()){
            enviroment.setOperative(true);
        }

        enviromentRepository.save(enviroment);

        var userLog = new UserLog(user,"Enviroment",id_environment.toString(),"Operative","Activated Enviroment",EnumAction.UPDATE);
        userLogRepository.save(userLog);

    }

    @Override
    public void inactivateEnviroment(Long id_environment, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var enviroment = enviromentRepository.findById(id_environment).orElseThrow(()->new NotFoundException("Id Enviroment Not Found"));
        if(enviroment.isOperative()){
            enviroment.setOperative(false);
        }
        enviromentRepository.save(enviroment);

        var userLog = new UserLog(user,"Enviroment",id_environment.toString(),"Operative","Inactivated Enviroment",EnumAction.UPDATE);
        userLogRepository.save(userLog);

    }

}
