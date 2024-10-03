package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.BlankErrorException;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.infra.OpetativeTrueException;
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
    public ResponsibleDetailsDTO createResposible(ResponsibleCrateDTO data, Long userLog_id) {
        Users users = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var user = userRepository.findById(data.id_user()).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        if(!user.isOperative()){
            throw new OperativeFalseException("The inactive User cannot be accessed.");
        }

        var classe = classesRepository.findById(data.id_classes()).orElseThrow(() -> new NotFoundException("Class Id Not Found"));
        if(!classe.isOperative()){
            throw new OperativeFalseException("The inactive Class cannot be accessed.");
        }

        var responsible = new Responsible(data.responsible_name(), data.edv(), classe,user);
        Long id_responsible = responsibleRepository.save(responsible).getId_responsible();

        var userLog = new UserLog(users, "Responsible", id_responsible.toString(), "Create new Responsible", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new ResponsibleDetailsDTO(responsible);

    }

    @Override
    public ResponsibleDetailsDTO getResposibleById(Long id_Resposible, Long userLog_id) {


        var responsible = responsibleRepository.findById(id_Resposible).orElseThrow(() -> new NotFoundException("Responsible Id Not Found"));
        if (!responsible.isOperative()){
            throw new OperativeFalseException("The inactive responsible cannot be accessed.");
        }

        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user, "Resposible", id_Resposible.toString(), "Read Resposible", EnumAction.READ);
        userLogRepository.save(userLog);

        return new ResponsibleDetailsDTO(responsible);

    }

    @Override
    public ResponsibleDetailsDTO getResposibleByEdv(String edv, Long userLog_id) {
        var responsible = responsibleRepository.findByEdv(edv).orElseThrow(() -> new NotFoundException("Responsible Id Not Found"));
        if (!responsible.isOperative()){
            throw new OperativeFalseException("The inactive responsible cannot be accessed.");
        }

        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user, "Resposible", edv, "Read Resposible", EnumAction.READ);
        userLogRepository.save(userLog);

        return new ResponsibleDetailsDTO(responsible);

    }

    @Override
    public List<ResponsibleDetailsDTO> getAllResposible(int page, int itens, Long userLog_id) {
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"Resposible","Read All Resposible", EnumAction.READ);
        userLogRepository.save(userLog);

        return responsibleRepository.findByOperativeTrue(PageRequest.of(page, itens)).stream().map(ResponsibleDetailsDTO::new).toList();
    }

    @Override
    public ResponsibleDetailsDTO updateResposible(Long id_responsible, ResponsibleUpdateDTO data, Long userLog_id) {

        var responsible = responsibleRepository.findById(id_responsible)
                .orElseThrow(() -> new NotFoundException("Responsible Id Not Found"));

        if (!responsible.isOperative()) {
            throw new OperativeFalseException("The inactive responsible cannot be accessed.");
        }

        Users userLog = this.userRepository.findById(userLog_id)
                .orElseThrow(() -> new NotFoundException("User Log Id Not Found"));
        var userlog = new UserLog(userLog, "Responsible", id_responsible.toString(), null, "Info update", EnumAction.UPDATE);


        if (data.responsible_name() != null) {
            if(data.edv().isBlank()){
                throw new BlankErrorException("Responsible name cannot be blank");
            }
            responsible.setResponsible_name(data.responsible_name());
            userlog.setField("Responsible name updated to: " + data.responsible_name());
        }


        if (data.edv() != null) {
            if(data.edv().isBlank()){
                throw new BlankErrorException("Edv cannot be blank");
            }
            responsible.setEdv(data.edv());
            userlog.setField(userlog.getField() + " ,Responsible EDV updated to: " + data.edv());
        }


        if (data.id_classes() != null) {
            var classEntity = classesRepository.findById(data.id_classes())
                    .orElseThrow(() -> new NotFoundException("Class Id Not Found"));
            if (!classEntity.isOperative()) {
                throw new OperativeFalseException("The inactive classes cannot be accessed.");
            }
            responsible.setClasses(classEntity);
            userlog.setField(userlog.getField() + " ,Responsible class updated to: " + data.id_classes());
        }


        if (data.id_user() != null) {
            var user = userRepository.findById(data.id_user())
                    .orElseThrow(() -> new NotFoundException("User Id Not Found"));
            if (!user.isOperative()) {
                throw new OperativeFalseException("The inactive user cannot be accessed.");
            }
            responsible.setUser(user);
            userlog.setField(userlog.getField() + " ,Responsible user updated to: " + data.id_user());
        }

        userLogRepository.save(userlog);
        responsibleRepository.save(responsible);

        return new ResponsibleDetailsDTO(responsible);
    }


    @Override
    public void activeResposible(Long id_resposible, Long userLog_id) {

        var responsible = responsibleRepository.findById(id_resposible).orElseThrow(() -> new NotFoundException("Id not found"));
        if (responsible.isOperative()){
            throw new OpetativeTrueException("It is already activate");
        }
        responsible.setOperative(true);
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user, "Resposible", id_resposible.toString(), "Operative", "Activated Resposible", EnumAction.UPDATE);
        responsibleRepository.save(responsible);
        userLogRepository.save(userLog);
    }

    @Override
    public void inactivateResposible(Long id_resposible, Long userLog_id) {


        var responsibleClass = responsibleRepository.findById(id_resposible).orElseThrow(()-> new NotFoundException("Responsible id not found"));
        if (!responsibleClass.isOperative()){
            throw new OperativeFalseException("It is already inactivate");
        }
        responsibleClass.setOperative(false);
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user, "Resposible", id_resposible.toString(), "Operative", "Inactivated Resposible", EnumAction.UPDATE);
        responsibleRepository.save(responsibleClass);
        userLogRepository.save(userLog);
    }
}
