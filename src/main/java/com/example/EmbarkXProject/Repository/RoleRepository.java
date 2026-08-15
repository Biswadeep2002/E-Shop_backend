package com.example.EmbarkXProject.Repository;

import com.example.EmbarkXProject.Model.AppRole;
import com.example.EmbarkXProject.Model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByRoleName(AppRole appRole);
}
