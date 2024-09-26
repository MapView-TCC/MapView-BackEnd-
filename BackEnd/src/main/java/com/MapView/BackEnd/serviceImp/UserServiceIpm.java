package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.User.UserCreateDTO;
import com.MapView.BackEnd.dtos.User.UserDetailsDTO;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.UserService;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.RoleUser;
import com.MapView.BackEnd.infra.Exceptions.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public class UserServiceIpm implements UserService {

    private final UserRepository userRepository;

    public UserServiceIpm(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetailsDTO getUser(Long id_user) {
        Users user = this.userRepository.findById(id_user).orElseThrow(() -> new NotFoundException("User Id not found"));
        if(!user.status_check()){
            return null;

        }
        return new UserDetailsDTO(user);
    }

    @Override
    public List<UserDetailsDTO> getAllUser(int page, int itens) {
        return this.userRepository.findByOperativeTrue(PageRequest.of(page, itens)).stream().map(UserDetailsDTO::new).toList();
    }


    @Override
    public UserDetailsDTO createUser(UserCreateDTO data) {
        var user = new Users(data.email());
        userRepository.save(user);
        return new UserDetailsDTO(user);
    }



    @Override
    public void setPrivilege(Long user_id, RoleUser roleUser) {
        var user = userRepository.findById(user_id).orElseThrow(()-> new NotFoundException("User Id Not Found"));
        if(user.status_check()){
            user.setRole(roleUser);
            userRepository.save(user);
        }
    }


    @Override
    public void activeUser(Long id_user) {
       var userClass = this.userRepository.findById(id_user);
       if (userClass.isPresent()){
           var user = userClass.get();
           user.setOperative(true);
       }
    }
    @Override
    public void inactivateUser(Long id_user) {
        var userClass = this.userRepository.findById(id_user);
        if (userClass.isPresent()){
            var user = userClass.get();
            user.setOperative(false);
        }
    }
}
