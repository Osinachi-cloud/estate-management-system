package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.entity.Estate;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstateRepository extends RevisionRepository<Estate, Long, Integer>, JpaRepository<Estate, Long>, JpaSpecificationExecutor<Estate> {
        Optional<Estate> findByEstateId(String id);
}
