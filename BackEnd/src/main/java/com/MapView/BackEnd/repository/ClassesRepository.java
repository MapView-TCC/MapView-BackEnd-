package com.MapView.BackEnd.repository;

import com.MapView.BackEnd.entities.Classes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ClassesRepository extends JpaRepository<Classes,Long> {
    List<Classes> findClassesByOperativeTrue(Pageable pageable);

}
