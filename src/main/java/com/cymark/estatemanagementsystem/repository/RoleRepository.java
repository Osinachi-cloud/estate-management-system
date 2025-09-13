package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> findRoleByName(@Param("name") String name);

    Optional<Role> findByName(String s);
}
