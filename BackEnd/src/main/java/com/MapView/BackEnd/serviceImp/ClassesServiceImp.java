package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.repository.ClassesRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.ClassesService;
import com.MapView.BackEnd.dtos.Classes.ClassesCreateDTO;
import com.MapView.BackEnd.dtos.Classes.ClassesDetaiLDTO;
import com.MapView.BackEnd.dtos.Classes.ClassesUpdateDTO;
import com.MapView.BackEnd.entities.Classes;
import com.MapView.BackEnd.infra.NotFoundException;
import com.MapView.BackEnd.service.UserLogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Service

public class ClassesServiceImp implements ClassesService {

    private final ClassesRepository classesRepository;
    private final UserRepository userRepository;
    private final UserLogRepository userLogService;

    public ClassesServiceImp(ClassesRepository classesRepository, UserRepository userRepository, UserLogRepository userLogService) {
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.userLogService = userLogService;
    }
    @Override
    public ClassesDetaiLDTO createClasses(ClassesCreateDTO data, Long id_user) {
        var user = userRepository.findById(data.user_id()).orElseThrow(() -> new NotFoundException("User Id Not Found"));
        var classe = new Classes(data,user);
        Long id_classes = classesRepository.save(classe).getId_classes();

        var userLog = new UserLog(null,"Classes", id_classes,"Create new classes", EnumAction.CREATE);
        userLogService.save(userLog);

        return new ClassesDetaiLDTO(classe);
    }

    @Override
    public ClassesDetaiLDTO getClasse(Long id, Long user_id) {
        var classe = classesRepository.findById(id).orElseThrow(() -> new NotFoundException("Classe id Not Found"));
        if (!classe.check_status()){
            return null;
        }

        var userLog = new UserLog(null,"Classe",id,"Read Classes",EnumAction.READ);
        userLogService.save(userLog);

        return new ClassesDetaiLDTO(classe);
    }

    @Override
    public ClassesDetaiLDTO updateClasses(Long classes_id ,ClassesUpdateDTO data, Long user_id) {
        var classes = classesRepository.findById(data.user_id()).orElseThrow(() -> new NotFoundException("Class Id Not Found"));
        var userlog = new UserLog(null,"Classes",classes_id,null,"Update Classes: ",EnumAction.UPDATE);

        if(data.user_id() != null){
            var user = userRepository.findById(data.user_id()).orElseThrow(() -> new NotFoundException("User Id Not Found"));
            classes.setId_user(user);
            userlog.setField("user_id");
            userlog.setDescription("user_id to: " + data.user_id());
        }

        if(data.classes() != null){
            classes.setClasses(data.classes());
            userlog.setField("classes");
            userlog.setDescription("classes to: " + data.classes());
        }
        classesRepository.save(classes);
        userLogService.save(userlog);

        return new ClassesDetaiLDTO(classes);
    }

    @Override
    public List<ClassesDetaiLDTO> getAllClasses(int page, int itens, Long user_id) {
        var userLog = new UserLog(null,"Classes","Read All Classes", EnumAction.READ);
        userLogService.save(userLog);
        return this.classesRepository.findClassesByOperativeTrue(PageRequest.of(page, itens)).stream().map(ClassesDetaiLDTO::new).toList();
    }

    @Override
    public void activeClass(Long class_id, Long user_id) {
        var classesClass = classesRepository.findById(class_id);
        if (classesClass.isPresent()){
            var classes = classesClass.get();
            classes.setOperative(true);
        }

        var userLog = new UserLog(null,"Classes",class_id,"Operative","Activated Classes",EnumAction.UPDATE);
        userLogService.save(userLog);
    }

    @Override
    public void inactiveClass(Long class_id, Long user_id) {
        var classesClass = classesRepository.findById(class_id);
        if (classesClass.isPresent()){
            var classes = classesClass.get();
            classes.setOperative(false);
        }

        var userLog = new UserLog(null,"Classes",class_id,"Operative","Inactivated Classes",EnumAction.UPDATE);
        userLogService.save(userLog);

    }


}
