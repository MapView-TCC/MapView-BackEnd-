package com.MapView.BackEnd.service;

import com.MapView.BackEnd.dtos.Classes.ClassesCreateDTO;
import com.MapView.BackEnd.dtos.Classes.ClassesDetaiLDTO;
import com.MapView.BackEnd.dtos.Classes.ClassesUpdateDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ClassesService {

    ClassesDetaiLDTO getClasseById(Long id, Long userLog_id);
    ClassesDetaiLDTO getClasseByName(String class_name, Long userLog_id);
    ClassesDetaiLDTO createClasses(ClassesCreateDTO data, Long userLog_id);
    ClassesDetaiLDTO updateClasses(Long classes_id, ClassesUpdateDTO data, Long userLog_id);
    List<ClassesDetaiLDTO> getAllClasses(int page, int itens, Long userLog_id);
    void activeClass(Long class_id, Long userLog_id);
    void inactiveClass(Long class_id, Long userLog_id);
}
