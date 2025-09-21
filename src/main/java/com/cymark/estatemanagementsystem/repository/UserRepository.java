package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends RevisionRepository<UserEntity, Long, Integer>, JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    boolean existsByEmail(String emailAddress);

    Optional<UserEntity> findByEmail(String emailAddress);

    Optional<UserEntity> findByPhone(String phoneNumber);

    Optional<UserEntity> findByUserId(String customerId);
}
