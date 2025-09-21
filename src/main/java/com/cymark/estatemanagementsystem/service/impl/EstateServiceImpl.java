package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.exception.UserException;
import com.cymark.estatemanagementsystem.model.dto.EstateDto;
import com.cymark.estatemanagementsystem.model.entity.Estate;
import com.cymark.estatemanagementsystem.model.entity.Role;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.enums.ResponseStatus;
import com.cymark.estatemanagementsystem.repository.EstateRepository;
import com.cymark.estatemanagementsystem.repository.RoleRepository;
import com.cymark.estatemanagementsystem.repository.UserRepository;
import com.cymark.estatemanagementsystem.service.EstateService;
import com.cymark.estatemanagementsystem.service.PasswordService;
import com.cymark.estatemanagementsystem.util.NumberUtils;
import com.cymark.estatemanagementsystem.util.UserValidationUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstateServiceImpl implements EstateService {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final EstateRepository estateRepository;

    private final RoleRepository roleRepository;

    private final PasswordService passwordService;

    private final UserRepository userRepository;

    @Transactional
    @Override
    public EstateDto onboardEstate(EstateDto estateRequest) {
        log.info("Creating customer with request: {}", estateRequest);
        try {
            validatesByAdmin(estateRequest);

            String estateId = NumberUtils.generate(10);

            UserEntity user = new UserEntity();
            user.setFirstName(estateRequest.getFirstName());
            user.setLastName(estateRequest.getLastName());
            user.setEmail(estateRequest.getEmail());
            user.setPhone(estateRequest.getPhone());
            user.setOccupancyVerified(true);
            user.setDesignation(estateRequest.getDesignation());
            user.setEstateId(estateId);

            Optional<Role> optionalRole = roleRepository.findRoleByName("SUPER_ADMIN");
            log.info("optionalRole obj : {}", optionalRole);

            if(optionalRole.isPresent()){
                Role role = optionalRole.get();
                user.setRole(role);
            }
            user.setPassword(passwordService.encode("1234"));
            log.info("customer obj : {}", user);

            System.out.println("user id");
            System.out.println(user.getPhone() + estateId);
            user.setUserId(user.getPhone() + estateId);

            UserEntity newEstateAdmin = userRepository.save(user);

            Estate estate = new Estate();
            estate.setEstateAdminUser(newEstateAdmin);
            estate.setEstateId(estateId);
            estate.setCountry(estateRequest.getCountry());
            estate.setState(estateRequest.getState());
            estate.setCity(estateRequest.getCity());
            estate.setPostalCode(estateRequest.getPostalCode());

            estateRepository.save(estate);

            return new EstateDto(estate, newEstateAdmin);
        } catch (HttpMessageNotReadableException e){
            throw new CymarkException(e.getMessage());
        }catch (UserException e) {
            log.error("Custom error occurred while creating customer :: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("An Exception occurred in customer creation :: {}", e.getMessage());
            if (e.getMessage().contains("JDBC exception")){
                throw new CymarkException("Error inserting user in DB");
            }
            throw new CymarkException(e.getMessage());
        }
    }

    private void validatesByAdmin(EstateDto customerRequest) {
        log.info("customerRequest ===>: {}", customerRequest);

        if (!UserValidationUtils.isValidEmail(customerRequest.getEmail())) {
            throw new UserException(ResponseStatus.INVALID_EMAIL_ADDRESS);
        }

        if (!UserValidationUtils.isValidPhoneNumber(customerRequest.getPhone())) {
            throw new UserException(ResponseStatus.INVALID_PHONE_NUMBER);
        }
        Optional<UserEntity> existingCustomer = userRepository.findByEmail(customerRequest.getEmail());

        Optional<UserEntity> existingCustomerByPhone = userRepository.findByPhone(customerRequest.getPhone());

        if (existingCustomer.isPresent() && existingCustomer.get().isOccupancyVerified()) {
            log.info("customer exists with email ===>>> :{}", existingCustomer.get().getEmail());
            throw new UserException(ResponseStatus.EMAIL_ADDRESS_EXISTS);
        }

        if (existingCustomer.isPresent() && !existingCustomer.get().isOccupancyVerified()) {
            log.info("customer exists with email ===>>> :{}", existingCustomer.get().getEmail());
            throw new UserException(ResponseStatus.EMAIL_ADDRESS_UNVERIFIED);
        }

        if (existingCustomerByPhone.isPresent() && existingCustomerByPhone.get().isOccupancyVerified()) {
            log.info("customer exists with phone ===>>> :{}", existingCustomerByPhone.get().getPhone());
            throw new UserException(ResponseStatus.PHONE_NUMBER_EXISTS);
        }

        if (existingCustomerByPhone.isPresent() && !existingCustomerByPhone.get().isOccupancyVerified()) {
            log.info("customer exists with phone ===>>> :{}", existingCustomerByPhone.get().getPhone());
            throw new UserException(ResponseStatus.EMAIL_ADDRESS_UNVERIFIED);
        }
    }
}
