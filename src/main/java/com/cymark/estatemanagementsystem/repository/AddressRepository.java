package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.entity.Address;
import com.cymark.estatemanagementsystem.model.entity.Estate;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

//    List<Address> findAddressByUserEntities(List<UserEntity> userEntity);

    List<Address> findAddressesByUserEntities(Collection<UserEntity> userEntities);

    Page<Address> findAddressesByEstate(Estate estate, Pageable pageable);

    Address findAddressById(Long estate);
}