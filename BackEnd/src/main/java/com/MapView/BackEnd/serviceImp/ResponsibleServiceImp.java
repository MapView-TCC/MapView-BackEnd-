package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.ClassesRepository;
import com.MapView.BackEnd.repository.ResponsibleRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.ResponsibleService;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleCrateDTO;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleDetailsDTO;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleUpdateDTO;
import com.MapView.BackEnd.entities.Responsible;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class ResponsibleServiceImp implements ResponsibleService {

    private final ResponsibleRepository responsibleRepository;
    private final ClassesRepository classesRepository;
    private final UserRepository userRepository;
    private final UserLogRepository userLogRepository;

    public ResponsibleServiceImp(ResponsibleRepository responsibleRepository, ClassesRepository classesRepository, UserRepository userRepository, UserLogRepository userLogRepository) {
        this.responsibleRepository = responsibleRepository;
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.userLogRepository = userLogRepository;
    }
    @Override
    public ResponsibleDetailsDTO createResposible(ResponsibleCrateDTO data, Long user_id) {
        Users users = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var user = userRepository.findById(data.id_user()).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        if(!user.isOperative()){
            throw new OperativeFalseException("The inactive  cannot be accessed.");
        }

        var classe = classesRepository.findById(data.id_classes()).orElseThrow(() -> new NotFoundException("Class Id Not Found"));
        if(!classe.isOperative()){

        }

        var responsible = new Responsible(data.responsible_name(), data.edv(), classe,user);
        Long id_responsible = responsibleRepository.save(responsible).getId_responsible();

        var userLog = new UserLog(users, "Responsible", id_responsible.toString(), "Create new Responsible", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new ResponsibleDetailsDTO(responsible);

    }

    @Override
    public ResponsibleDetailsDTO getResposible(Long id_Resposible, Long user_id) {


        var responsible = responsibleRepository.findById(id_Resposible).orElseThrow(() -> new NotFoundException("Responsible Id Not Found"));
        if (!responsible.isOperative()){
            throw new OperativeFalseException("The inactive responsible cannot be accessed.");
        }

        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user, "Resposible", id_Resposible.toString(), "Read Resposible", EnumAction.READ);
        userLogRepository.save(userLog);

        return new ResponsibleDetailsDTO(responsible);

    }

    @Override
    public List<ResponsibleDetailsDTO> getAllResposible(int page, int itens, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"Resposible","Read All Resposible", EnumAction.READ);
        userLogRepository.save(userLog);

        return responsibleRepository.findByOperativeTrue(PageRequest.of(page, itens)).stream().map(ResponsibleDetailsDTO::new).toList();
    }

    @Override
    public ResponsibleDetailsDTO updateResposible(Long id_responsible, ResponsibleUpdateDTO data, Long user_id ) {
        var responsible = responsibleRepository.findById(id_responsible).orElseThrow(() -> new NotFoundException("Responsible Id Not Found"));
        if(responsible.isOperative()){
            throw new OperativeFalseException("The inactive classes cannot be accessed.");
        }

        Users userLog = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userlog = new UserLog(userLog,"Resposible", id_responsible.toString(),null,"Infos update",EnumAction.UPDATE);

        if(data.responsible_name() != null){
            responsible.setResponsible_name(data.responsible_name());
            userlog.setField("Responsible name to: " + data.responsible_name());
        }

        if(data.edv() != null){
            responsible.setEdv(data.edv());
            userlog.setField(userlog.getField() + " ," + "Responsible edv to: " + data.edv());
        }

        if(data.id_classes() != null){
            var classeEntity = classesRepository.findById(data.id_classes()).orElseThrow(() -> new NotFoundException("Responsible Id Not Found"));
            if(classeEntity.isOperative()){
                throw new OperativeFalseException("The inactive classes cannot be accessed.");
            }
            responsible.setClasses(classeEntity);
            userlog.setField(userlog.getField() + " ," + "Responsible id classes to: " + data.id_classes());
        }

        if(data.id_user() != null){
            var user = userRepository.findById(data.id_user()).orElseThrow(() -> new NotFoundException("User Id Not Found"));
            if (user.isOperative()){
                throw new OperativeFalseException("The inactive classes cannot be accessed.");
            }
            responsible.setUser(user);
            userlog.setField(userlog.getField() + " ," + "Responsible id user to: " + data.id_user());
        }

        userLogRepository.save(userlog);
        responsibleRepository.save(responsible);
        return new ResponsibleDetailsDTO(responsible);

    }

    @Override
    public void activeResposible(Long id_resposible, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var responsibleClass = responsibleRepository.findById(id_resposible);
        if (responsibleClass.isPresent()){
            var responsible = responsibleClass.get();
            responsible.setOperative(true);
        }

        var userLog = new UserLog(user, "Resposible", id_resposible.toString(), "Operative", "Activated Resposible", EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }

    @Override
    public void inactivateResposible(Long id_resposible, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var responsibleClass = responsibleRepository.findById(id_resposible).orElseThrow(()-> new NotFoundException("Responsible id not found"));
        responsibleClass.setOperative(false);
        responsibleRepository.save(responsibleClass);

        var userLog = new UserLog(user, "Resposible", id_resposible.toString(), "Operative", "Inactivated Resposible", EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }
}
