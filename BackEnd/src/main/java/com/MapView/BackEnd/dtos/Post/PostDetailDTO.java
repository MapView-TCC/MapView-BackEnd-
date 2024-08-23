package com.MapView.BackEnd.dtos.Post;

import com.MapView.BackEnd.Repository.PostRepository;
import com.MapView.BackEnd.entities.Post;

public record PostDetailDTO(Long id_post,String post) {
    public PostDetailDTO(Post post){
        this(post.getId_post(), post.getPost());
    }
}
    