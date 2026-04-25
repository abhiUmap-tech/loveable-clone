package com.projects.lovable_clone.repository;

import com.projects.lovable_clone.entity.Project;
import com.projects.lovable_clone.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
        SELECT p as project, pm.projectRole as role
        FROM Project p
        JOIN ProjectMember pm ON pm.id.projectId = p.id
        WHERE pm.id.userId = :userId
        AND p.deletedAt IS NULL
        ORDER BY p.updatedAt DESC
        """)
    List<ProjectWithRole> findAllAccessibleByUser(@Param("userId") Long userId);

    //    ----------------------------------------------------------------------------------------------
    @Query("""
            SELECT p from Project p
            WHERE p.id = :projectId
            AND p.deletedAt IS NULL
            AND EXISTS (
                        SELECT 1 FROM ProjectMember pm
               WHERE pm.id.userId = :userId
               AND pm.id.projectId = p.id)
            """)
    Optional<Project> findAccessibleProjectsById(@Param("projectId") Long projectId,
                                                 @Param("userId") Long userId);

//    ----------------------------------------------------------------------------------------------

    @Query("""
            SELECT p as project, pm.projectRole as role
            FROM Project p
            JOIN ProjectMember pm ON pm.id.projectId = p.id
            WHERE p.id = :projectId
            AND pm.id.userId = :userId
            AND p.deletedAt IS NULL
            """)
    Optional<ProjectWithRole> findAccessibleProjectsByIdWithRole(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );

    interface ProjectWithRole {
        Project getProject();

        ProjectRole getRole();
    }

    @Query("""
            SELECT p FROM Project p
            WHERE p.id = :projectId
              AND EXISTS (
                       SELECT 1 FROM ProjectMember pm
                       WHERE pm.id.userId = :userId
                       AND pm.id.projectId = p.id)
            """)
    Optional<Project> findByIdIncludingDeleted(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );


}
