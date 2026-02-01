package com.projects.lovable_clone.repository;

import com.projects.lovable_clone.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
        SELECT p from Project p
        WHERE p.deleteAt IS NULL
        AND p.owner.id = :userId
        ORDER BY p.updatedAt DESC
        """)
    List<Project> findAllAccessibleByUser(@Param("userId") Long userId);


   /// “Give me a project with this ID, owned by this user,
   /// that is not deleted — and also load the owner details immediately.”

    @Query("""
            SELECT p from Project p
            LEFT JOIN FETCH p.owner
            WHERE p.id = :projectId
            AND p.deleteAt IS NULL
            AND p.owner.id = :userId
            """)
    Optional<Project> findAccessibleProjectsById(@Param("projectId")Long projectId,
                                                 @Param("userId") Long userId);

    @Query("""
    SELECT p FROM Project p
    WHERE p.id = :projectId
    AND p.owner.id = :userId
    """)
    Optional<Project> findByIdIncludingDeleted(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );


}
