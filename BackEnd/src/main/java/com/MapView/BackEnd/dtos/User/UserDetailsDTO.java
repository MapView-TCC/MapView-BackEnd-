package com.MapView.BackEnd.dtos.User;

import com.MapView.BackEnd.entities.Role;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.RoleUser;

public record UserDetailsDTO(Long user_id, String email, String name, Role role) {
    public UserDetailsDTO(Users user){
        this(user.getId_user(), user.getEmail(), user.getName(),user.getRole());
    }
}
