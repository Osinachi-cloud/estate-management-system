package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.entity.Estate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstateRepository extends JpaRepository<Estate, Long> {
    Optional<Estate> findByEstateId(String id);
}