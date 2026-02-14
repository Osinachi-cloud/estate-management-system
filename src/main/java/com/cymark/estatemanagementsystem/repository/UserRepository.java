package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.enums.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends RevisionRepository<UserEntity, Long, Integer>, JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    boolean existsByEmail(String emailAddress);

    Optional<UserEntity> findByEmail(String emailAddress);

    Optional<UserEntity> findByPhone(String phoneNumber);

    Optional<UserEntity> findByUserId(String customerId);

    Optional<UserEntity> findUserEntityByUserId(String userId);

    List<UserEntity> findByEstateIdAndDesignation(String estateId, Designation designation);

    List<UserEntity> findByLandlordIdAndDesignation(String landlordId, Designation designation);


    @Query("SELECT u FROM UserEntity u WHERE " +
            "(u.landlordId = :userId OR u.tenantId = :userId) and u.estateId = :estateId")
    List<UserEntity> findByLandlordOrTenantIdWhereBothArePresent(@Param("userId") String userId, @Param("estateId") String estateId);

    @Query(value = "SELECT COUNT(*) FROM USER_ENTITY", nativeQuery = true)
    Long countAllUsers(boolean enabled);

    @Query(value = "SELECT COUNT(*) FROM USER_ENTITY WHERE IS_ENABLED = TRUE", nativeQuery = true)
    Long countAllActiveUsers();

    @Query(value = "SELECT COUNT(*) FROM USER_ENTITY WHERE IS_ENABLED = FALSE", nativeQuery = true)
    Long countAllInActiveUsers();

    @Query(value = "SELECT COUNT(*) FROM USER_ENTITY WHERE DESIGNATION = :designation", nativeQuery = true)
    Long countAllUsersByDesignation(@Param("designation") String designation);


    List<UserEntity> findByLandlordId(String userId);

    List<UserEntity> findByTenantId(String userId);
}
