package com.example.EmbarkXProject.Repository;

import com.example.EmbarkXProject.Model.AppRole;
import com.example.EmbarkXProject.Model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Users u JOIN u.roles r WHERE r.roleName = :role")
    Page<Users> findByRoleName(@Param("role") AppRole appRole, Pageable pageDetails);
}
