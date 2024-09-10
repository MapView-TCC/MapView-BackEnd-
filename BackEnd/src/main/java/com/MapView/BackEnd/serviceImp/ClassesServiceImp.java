package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.ClassesRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.ClassesService;
import com.MapView.BackEnd.dtos.Classes.ClassesCreateDTO;
import com.MapView.BackEnd.dtos.Classes.ClassesDetaiLDTO;
import com.MapView.BackEnd.dtos.Classes.ClassesUpdateDTO;
import com.MapView.BackEnd.entities.Classes;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
@Service

public class ClassesServiceImp implements ClassesService {

    private final ClassesRepository classesRepository;
    private final UserRepository userRepository;
    private final UserLogRepository userLogRepository;


    public ClassesServiceImp(ClassesRepository classesRepository, UserRepository userRepository, UserLogRepository userLogRepository) {
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.userLogRepository = userLogRepository;
    }
    @Override
    public ClassesDetaiLDTO createClasses(ClassesCreateDTO data, Long user_id) {
        var user = userRepository.findById(data.user_id()).orElseThrow(() -> new NotFoundException("User Id Not Found"));

        var usuario_log = userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        var classe = new Classes(data,user);

        Long id_classes = classesRepository.save(classe).getId_classes();

        var userLog = new UserLog(usuario_log,"Classes", id_classes.toString(),"Create new classes", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new ClassesDetaiLDTO(classe);
    }

    @Override
    public ClassesDetaiLDTO getClasse(Long id, Long user_id) {
        var usuario_log = userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        var classe = classesRepository.findById(id).orElseThrow(() -> new NotFoundException("Classe id Not Found"));
        if (!classe.check_status()){
            throw new OperativeFalseException("The classe area cannot be read..");
        }

        var userLog = new UserLog(usuario_log,"Classe",id.toString(),"Read Area",EnumAction.READ);
        userLogRepository.save(userLog);

        return new ClassesDetaiLDTO(classe);
    }

    @Override
    public ClassesDetaiLDTO updateClasses(Long classes_id ,ClassesUpdateDTO data, Long user_id) {
        var usuario_log = userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        var classes = classesRepository.findById(data.user_id()).orElseThrow(() -> new NotFoundException("Class Id Not Found"));

        if(!classes.isOperative()){
            throw new OperativeFalseException("The inactive equipment cannot be updated.");
        }

        var userlog = new UserLog(usuario_log,"Classes",classes_id.toString(),null,"Update Classes: ",EnumAction.UPDATE);

        if(data.user_id() != null){
            var user = userRepository.findById(data.user_id()).orElseThrow(() -> new NotFoundException("User Id Not Found"));
            classes.setUser(user);
            userlog.setField("user_id");
            userlog.setDescription("user_id to: " + data.user_id());
        }

        if(data.classes() != null){
            classes.setClasses(data.classes());
            userlog.setField("classes");
            userlog.setDescription("classes to: " + data.classes());
        }
        classesRepository.save(classes);
        userLogRepository.save(userlog);

        return new ClassesDetaiLDTO(classes);
    }

    @Override
    public List<ClassesDetaiLDTO> getAllClasses(int page, int itens, Long user_id) {
        var usuario_log = userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        if(usuario_log.isOperative()) {
            var userLog = new UserLog(usuario_log, "Classes", "Read All Classes", EnumAction.READ);
            userLogRepository.save( userLog);
        }
        return this.classesRepository.findClassesByOperativeTrue(PageRequest.of(page, itens)).stream().map(ClassesDetaiLDTO::new).toList();
    }

    @Override
    public void activeClass(Long class_id, Long user_id) {
        var usuario_log = userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        if(usuario_log.isOperative()) {
            var classes = classesRepository.findById(class_id).orElseThrow(() -> new NotFoundException("User Id Not Found"));
            classes.setOperative(true);
            classesRepository.save(classes);
            var userLog = new UserLog(usuario_log,"Classes",class_id.toString(),"Operative","Activated Classes",EnumAction.UPDATE);
            userLogRepository.save(userLog);
        }


    }

    @Override
    public void inactiveClass(Long class_id, Long user_id) {
        var usuario_log = userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        if(usuario_log.isOperative()) {
            var classes = classesRepository.findById(class_id).orElseThrow(() -> new NotFoundException("User Id Not Found"));
            classes.setOperative(false);
            classesRepository.save(classes);
            var userLog = new UserLog(usuario_log,"Classes",class_id.toString(),"Operative","Inactivate Classes",EnumAction.UPDATE);
            userLogRepository.save(userLog);
        }

    }


}
