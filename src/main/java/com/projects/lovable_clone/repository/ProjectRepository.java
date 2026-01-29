package com.projects.lovable_clone.repository;

import com.projects.lovable_clone.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
        SELECT p from Project p
        WHERE p.deleteAt IS NULL 
        AND p.owner.id = :userId
        ORDER BY p.updatedAt DESC
    """)
    List<Project> findAllAccessibleByUser(@Param("userId") Long userId);



}
