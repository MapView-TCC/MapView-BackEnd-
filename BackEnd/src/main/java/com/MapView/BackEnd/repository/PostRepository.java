package com.MapView.BackEnd.repository;

import com.MapView.BackEnd.entities.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByOperativeTrue(Pageable pageable);

    Post save (Post data);


}
