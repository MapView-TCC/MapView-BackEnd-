package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.Environment;
import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.Exception.*;
import com.MapView.BackEnd.repository.EnvironmentRepository;
import com.MapView.BackEnd.repository.RaspberryRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.EnvironmentService;
import com.MapView.BackEnd.dtos.Environment.EnvironmentCreateDTO;
import com.MapView.BackEnd.dtos.Environment.EnvironmentDetailsDTO;
import com.MapView.BackEnd.dtos.Environment.EnvironmentUpdateDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class EnvironmentServiceImp implements EnvironmentService {

    private final EnvironmentRepository environmentRepository;
    private final RaspberryRepository raspberryRepository;
    private final UserLogRepository userLogRepository;

    private final UserRepository userRepository;

    public EnvironmentServiceImp(EnvironmentRepository environmentRepository, RaspberryRepository raspberryRepository, UserLogRepository userLogRepository, UserRepository userRepository) {
        this.environmentRepository = environmentRepository;
        this.raspberryRepository = raspberryRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EnvironmentDetailsDTO getEnvironment(Long environment_id, Long userLog_id) {
        var environment = environmentRepository.findById(environment_id).orElseThrow(() -> new NotFoundException("Environment Id Not Found"));

        if(!environment.isOperative()){
            throw new OperativeFalseException("Id Inactive");
        }

        var id_user = userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        var userLog = new UserLog(id_user,"Environment",environment_id.toString(),"Read Environment", EnumAction.READ);
        userLogRepository.save(userLog);

        return new EnvironmentDetailsDTO(environment);
    }

    @Override
    public List<EnvironmentDetailsDTO> getAllEnvironment(Long userLog_id) {
        var id_user = userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        var userLog = new UserLog(id_user,"Environment","Read All Environment", EnumAction.READ);
        userLogRepository.save(userLog);

        return this.environmentRepository.findEnvironmentByOperativeTrue().stream().map(EnvironmentDetailsDTO::new).toList();
    }

    @Override
    public EnvironmentDetailsDTO createEnvironment(EnvironmentCreateDTO data, Long userLog_id) {
        try{

            var rasp = raspberryRepository.findById(data.raspberry()).orElseThrow(()->new NotFoundException("Id Raspberry Not Found"));
            var environment = new Environment(data, rasp);

            Long id_environment = environmentRepository.save(environment).getId_environment();

            Users users = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found!"));

            var userLog = new UserLog(users,"Environment", id_environment.toString(),"Create new Environment", EnumAction.CREATE);
            userLogRepository.save(userLog);

            return new EnvironmentDetailsDTO(environment);

        }catch (DataIntegrityViolationException e){
            throw new ExistingEntityException("This Environment: "+data.environment_name()+" already exist");
        }
    }

    @Override
    public EnvironmentDetailsDTO updateEnvironment(Long environment_id, EnvironmentUpdateDTO data, Long userLog_id) {
        var user = userRepository.findById(userLog_id).orElseThrow(()->new NotFoundException("Id Environment Not Found"));

        var environment = environmentRepository.findById(environment_id).orElseThrow(()->new NotFoundException("Id Environment Not Found"));
        if(!environment.isOperative()){
            throw new OperativeFalseException("The inactive environment cannot be updated.");
        }

        var userlog = new UserLog(user,"Environment",environment_id.toString(),null,"Update Environment: ",EnumAction.UPDATE);

        if (data.environment_name() != null){
            if(data.environment_name().isBlank()){
                throw new BlankErrorException("Environment name cannot be blank");
            }
            environment.setEnvironment_name(data.environment_name());
            userlog.setField("environment_name");
            userlog.setDescription("environment_name to: " + data.environment_name());
        }
        if (data.raspberry() != null){
            var rasp = raspberryRepository.findById(data.raspberry()).orElseThrow(() -> new NotFoundException("Id Raspberry Not Found"));
            if(!rasp.isOperative()){
                throw new OperativeFalseException("The inactive raspberry cannot be updated.");
            }
            environment.setRaspberry(rasp);
            userlog.setField("id_raspberry");
            userlog.setDescription("id_raspberry to: ");
        }

        environmentRepository.save(environment);
        userLogRepository.save(userlog);


        return new EnvironmentDetailsDTO(environment);
    }

    @Override
    public void activateEnvironment(Long id_environment, Long userLog_id) {
        var environment = environmentRepository.findById(id_environment).orElseThrow(()->new NotFoundException("Id Environment Not Found"));
        if(environment.isOperative()){
            throw new OpetativeTrueException("It is already activate");
        }
        environment.setOperative(true);
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"Environment",id_environment.toString(),"Operative","Activated Environment",EnumAction.UPDATE);
        environmentRepository.save(environment);
        userLogRepository.save(userLog);


    }

    @Override
    public void inactivateEnvironment(Long id_environment, Long userLog_id) {
        var environment = environmentRepository.findById(id_environment).orElseThrow(()->new NotFoundException("Id Environment Not Found"));
        if(!environment.isOperative()){
            throw new OperativeFalseException("It is already inactivate");
        }
        environment.setOperative(false);
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"Environment",id_environment.toString(),"Operative","Inactivated Environment",EnumAction.UPDATE);
        environmentRepository.save(environment);
        userLogRepository.save(userLog);




    }

}
