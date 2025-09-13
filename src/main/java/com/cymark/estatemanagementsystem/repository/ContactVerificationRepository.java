package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.entity.ContactVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactVerificationRepository extends JpaRepository<ContactVerification, Long> {
    ContactVerification findFirstByEmailAddressOrderByDateCreatedDesc(String emailAddress);
}
