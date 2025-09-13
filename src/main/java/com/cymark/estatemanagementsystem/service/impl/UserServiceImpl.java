package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.exception.ContactVerificationException;
import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.exception.UserException;
import com.cymark.estatemanagementsystem.exception.UserNotFoundException;
import com.cymark.estatemanagementsystem.model.dto.*;
import com.cymark.estatemanagementsystem.model.entity.ContactVerification;
import com.cymark.estatemanagementsystem.model.entity.Role;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.enums.ResponseStatus;
import com.cymark.estatemanagementsystem.model.request.AdminCustomerRequest;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.repository.ContactVerificationRepository;
import com.cymark.estatemanagementsystem.repository.UserRepository;
import com.cymark.estatemanagementsystem.service.PasswordService;
import com.cymark.estatemanagementsystem.service.RoleService;
import com.cymark.estatemanagementsystem.service.UserService;
import com.cymark.estatemanagementsystem.specification.UserSpecification;
import com.cymark.estatemanagementsystem.util.NumberUtils;
import com.cymark.estatemanagementsystem.util.ResponseUtils;
import com.cymark.estatemanagementsystem.util.UserValidationUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.cymark.estatemanagementsystem.util.DtoMapper.convertUserListToDto;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ContactVerificationRepository verificationRepository;
    private final PasswordService passwordService;

    private final RoleService roleService;

    public UserServiceImpl(
            UserRepository userRepository,
            ContactVerificationRepository verificationRepository,
            PasswordService passwordService, RoleService roleService) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.passwordService = passwordService;
        this.roleService = roleService;
    }

    @Transactional
    @Override
    public CustomerDto createCustomer(CustomerRequest customerRequest) {

        log.info("Creating customer with request: {}", customerRequest);
        try {

            validate(customerRequest);

            UserEntity customer = new UserEntity();
            customer.setFirstName(customerRequest.getFirstName());
            customer.setLastName(customerRequest.getLastName());
            customer.setEmailAddress(customerRequest.getEmailAddress());
            customer.setPhoneNumber(customerRequest.getPhoneNumber());
            customer.setOccupancyVerified(false);
            customer.setEnabled(false);
            customer.setDesignation(customerRequest.getDesignation());


//            customer.setShortBio(getStr(customerRequest.getShortBio()));

            Optional<Role> optionalRole = roleService.findRoleByName("CUSTOMER");
            log.info("optionalRole obj : {}", optionalRole);

            if(optionalRole.isPresent()){
                Role role = optionalRole.get();
                customer.setRole(role);
            }

            customer.setPassword(passwordService.encode(customerRequest.getPassword()));
            log.info("customer obj 1 : {}", customer);

//            if (Objects.nonNull(customerRequest.getProfileImage())) {
//                log.info("customer obj 2 : {}", customer);
//
//                byte[] imageBytes = Base64.decodeBase64(customerRequest.getProfileImage());
//                String base64EncodedImage = Base64.encodeBase64String(imageBytes);
//                customer.setProfileImage(base64EncodedImage);
//            }
//

            log.info("customer obj : {}", customer);

            UserEntity newCustomer = userRepository.save(customer);

            return new CustomerDto(newCustomer);
        } catch (UserException e) {
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

    @Transactional
    @Override
    public CustomerDto adminCreateCustomer(AdminCustomerRequest customerRequest) {

        log.info("Creating customer with request: {}", customerRequest);
        try {

            validatesByAdmin(customerRequest);

            UserEntity customer = new UserEntity();
            customer.setFirstName(customerRequest.getFirstName());
            customer.setLastName(customerRequest.getLastName());
            customer.setEmailAddress(customerRequest.getEmailAddress());
            customer.setPhoneNumber(customerRequest.getPhoneNumber());
            customer.setOccupancyVerified(true);
            customer.setDesignation(customerRequest.getDesignation());

            Optional<Role> optionalRole = roleService.findRoleByName("CUSTOMER");
            log.info("optionalRole obj : {}", optionalRole);

            if(optionalRole.isPresent()){
                Role role = optionalRole.get();
                customer.setRole(role);
            }

            customer.setPassword(passwordService.encode("1234abcd"));
            log.info("customer obj : {}", customer);

            UserEntity newCustomer = userRepository.save(customer);

            return new CustomerDto(newCustomer);
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

    @Override
    public CustomerDto updateCustomer(CustomerUpdateRequest customerRequest, String emailAddress) {
        log.info("Updating customer with email : {} ,  request: {}", emailAddress, customerRequest);
        try {
            UserEntity customer = userRepository.findByEmailAddress(emailAddress)
                    .orElseThrow(() -> new UserException(ResponseStatus.EMAIL_ADDRESS_NOT_FOUND));
            BeanUtils.copyProperties(customerRequest, customer, getNullPropertyNames(customerRequest));
            UserEntity newCustomer = userRepository.saveAndFlush(customer);

//            log.info("Updated customer with ID: {}", newCustomer.getUserId());
            return new CustomerDto(newCustomer);
        } catch (UserException e) {
            log.error("Custom error occurred while updating customer :: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating customer {}", e.getMessage());
            throw new UserException("Failed to create customer", 500);
        }

    }

    private String[] getNullPropertyNames(Object source){
        System.err.println("I entered in here ...");
        final BeanWrapper wrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
        List<String> nullProperties = new ArrayList<>();
        for (PropertyDescriptor descriptor: descriptors) {
            Object propertyValue = wrapper.getPropertyValue(descriptor.getName());
            if (propertyValue == null || (propertyValue instanceof String && ((String) propertyValue).trim().isEmpty())) {
                nullProperties.add(descriptor.getName());
            }
        }
        return nullProperties.toArray(new String[0]);
    }

    @Override
    public Response updateCustomerProfileImage(String profileImage, String emailAddress) {
        log.info("email address value: {}", emailAddress);
        Optional<UserEntity> existingCustomer = userRepository.findByEmailAddress(emailAddress);

        if (existingCustomer.isEmpty()) {
            throw new UserException("customer does not exist");
        }
        log.info("existingCustomer: {}", existingCustomer.get().getFirstName());

        UserEntity customer = existingCustomer.get();

        if (profileImage != null && !profileImage.isEmpty()) {
            byte[] imageBytes = Base64.decodeBase64(profileImage);
            String base64EncodedImage = Base64.encodeBase64String(imageBytes);
            customer.setProfileImage(base64EncodedImage);
            UserEntity newCustomer = userRepository.saveAndFlush(customer);

//            log.info("Updated customer with ID: {}", newCustomer.getUserId());

            return ResponseUtils.createSuccessResponse("profile image updated successfully");
        } else {
            throw new CymarkException("image field can not be empty");
        }
    }

    private void validate(CustomerRequest customerRequest) {
        log.info("customerRequest ===>: {}", customerRequest);

//        if (StringUtils.isBlank(customerRequest.getFirstName()) ||
//                StringUtils.isBlank(customerRequest.getLastName()) ||
//                StringUtils.isBlank(customerRequest.getEmailAddress()) ||
//                StringUtils.isBlank(customerRequest.getPhoneNumber()) ||
////                StringUtils.isBlank(customerRequest.getCountry()) ||
//                StringUtils.isBlank(customerRequest.getPassword())) {
//            throw new UserException(ResponseStatus.EMPTY_FIELD_VALUES);
//        }

        if (!UserValidationUtils.isValidEmail(customerRequest.getEmailAddress())) {
            throw new UserException(ResponseStatus.INVALID_EMAIL_ADDRESS);
        }

        if (!UserValidationUtils.isValidPhoneNumber(customerRequest.getPhoneNumber())) {
            throw new UserException(ResponseStatus.INVALID_PHONE_NUMBER);
        }
        Optional<UserEntity> existingCustomer = userRepository.findByEmailAddress(customerRequest.getEmailAddress());

        Optional<UserEntity> existingCustomerByPhone = userRepository.findByPhoneNumber(customerRequest.getPhoneNumber());

        if (existingCustomer.isPresent() && existingCustomer.get().isOccupancyVerified()) {
            log.info("customer exists with email ===>>> :{}", existingCustomer.get().getEmailAddress());
            throw new UserException(ResponseStatus.EMAIL_ADDRESS_EXISTS);
        }

        if (existingCustomer.isPresent() && !existingCustomer.get().isOccupancyVerified()) {
            log.info("customer exists with email ===>>> :{}", existingCustomer.get().getEmailAddress());
            throw new UserException(ResponseStatus.EMAIL_ADDRESS_UNVERIFIED);
        }

        if (existingCustomerByPhone.isPresent() && existingCustomerByPhone.get().isOccupancyVerified()) {
            log.info("customer exists with phone ===>>> :{}", existingCustomerByPhone.get().getPhoneNumber());
            throw new UserException(ResponseStatus.PHONE_NUMBER_EXISTS);
        }

        if (existingCustomerByPhone.isPresent() && !existingCustomerByPhone.get().isOccupancyVerified()) {
            log.info("customer exists with phone ===>>> :{}", existingCustomerByPhone.get().getPhoneNumber());
            throw new UserException(ResponseStatus.EMAIL_ADDRESS_UNVERIFIED);
        }
        passwordService.validateNewPassword(customerRequest.getPassword());
    }

    private void validatesByAdmin(AdminCustomerRequest customerRequest) {
        log.info("customerRequest ===>: {}", customerRequest);

//        if (StringUtils.isBlank(customerRequest.getFirstName()) ||
//                StringUtils.isBlank(customerRequest.getLastName()) ||
//                StringUtils.isBlank(customerRequest.getEmailAddress()) ||
//                StringUtils.isBlank(customerRequest.getPhoneNumber()) ||
////                StringUtils.isBlank(customerRequest.getCountry()) ||
//                StringUtils.isBlank("1234")) {
//            throw new UserException(ResponseStatus.EMPTY_FIELD_VALUES);
//        }

        if (!UserValidationUtils.isValidEmail(customerRequest.getEmailAddress())) {
            throw new UserException(ResponseStatus.INVALID_EMAIL_ADDRESS);
        }

        if (!UserValidationUtils.isValidPhoneNumber(customerRequest.getPhoneNumber())) {
            throw new UserException(ResponseStatus.INVALID_PHONE_NUMBER);
        }
        Optional<UserEntity> existingCustomer = userRepository.findByEmailAddress(customerRequest.getEmailAddress());

        Optional<UserEntity> existingCustomerByPhone = userRepository.findByPhoneNumber(customerRequest.getPhoneNumber());

        if (existingCustomer.isPresent() && existingCustomer.get().isOccupancyVerified()) {
            log.info("customer exists with email ===>>> :{}", existingCustomer.get().getEmailAddress());
            throw new UserException(ResponseStatus.EMAIL_ADDRESS_EXISTS);
        }

        if (existingCustomer.isPresent() && !existingCustomer.get().isOccupancyVerified()) {
            log.info("customer exists with email ===>>> :{}", existingCustomer.get().getEmailAddress());
            throw new UserException(ResponseStatus.EMAIL_ADDRESS_UNVERIFIED);
        }

        if (existingCustomerByPhone.isPresent() && existingCustomerByPhone.get().isOccupancyVerified()) {
            log.info("customer exists with phone ===>>> :{}", existingCustomerByPhone.get().getPhoneNumber());
            throw new UserException(ResponseStatus.PHONE_NUMBER_EXISTS);
        }

        if (existingCustomerByPhone.isPresent() && !existingCustomerByPhone.get().isOccupancyVerified()) {
            log.info("customer exists with phone ===>>> :{}", existingCustomerByPhone.get().getPhoneNumber());
            throw new UserException(ResponseStatus.EMAIL_ADDRESS_UNVERIFIED);
        }
//        passwordService.validateNewPassword(customerRequest.getPassword());
    }

    @Override
    public CustomerDto getCustomer(String customerId) {

        log.debug("Getting customer with ID: {}", customerId);

        UserEntity customer = getCustomerEntity(customerId);

        CustomerDto customerDto = new CustomerDto();
//        customerDto.setUserId(customer.getUserId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setEmailAddress(customer.getEmailAddress());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setEnabled(customer.isEnabled());
        customerDto.setHasPin(customer.getPin() != null);
        customerDto.setPassword(customer.getPassword());
//        customerDto.setTier(customer.getTier() != null ? customer.getTier().name() : "");
        return customerDto;
    }

    @Override
    public UserEntity getCustomerEntity(String customerId) {
        return userRepository.findByPhoneNumber(customerId).orElseThrow(() -> new UserNotFoundException(String.format("Customer [%s] not found", customerId)));
    }

    @Override
    public CustomerDto getCustomerByEmail(String emailAddress) {

        System.out.println("got to login method" + emailAddress);

        log.debug("Getting customer with email address: {}", emailAddress);
        UserEntity customer = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new UserNotFoundException(String.format("Customer with this email [%s] not found", emailAddress)));

        CustomerDto customerDto = new CustomerDto();
//        customerDto.setUserId(customer.getUserId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setEmailAddress(customer.getEmailAddress());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setCountry(customer.getCountry());
        customerDto.setEnabled(customer.isEnabled());
        customerDto.setProfileImage(customer.getProfileImage());
        customerDto.setAccountLocked(customer.isAccountLocked());
        customerDto.setPassword(customer.getPassword());
        customerDto.setProfileImage(customer.getProfileImage());
        customerDto.setSaveCard(customer.isSaveCard());
        customerDto.setEnablePush(customer.isEnablePush());
        customerDto.setHasPin(customer.getPin() != null);

//        if (customer.getTier() != null) {
//            customerDto.setTier(customer.getTier().name());
//        }
        return customerDto;
    }

    @Override
    public void updateLastLogin(CustomerDto user) {
        UserEntity customer = getCustomerEntity(user.getPhoneNumber());
        customer.setLastLogin(Instant.now());
        customer.setLoginAttempts(0);
        userRepository.save(customer);
//        log.info("Updated customer [{}] last login", customer.getUserId());
    }

    @Override
    public void updateLoginAttempts(String emailAddress) {
        Optional<UserEntity> optionalCustomer = userRepository.findByEmailAddress(emailAddress);
        if (optionalCustomer.isEmpty()) {
            log.warn("Customer with email address [{}] does not exist", emailAddress);
            return;
        }
        UserEntity customer = optionalCustomer.get();
        int loginAttempts = customer.getLoginAttempts() + 1;
        customer.setLoginAttempts(loginAttempts);
        userRepository.save(customer);
        log.info("Updated customer [{}] login attempts to {}", customer.getEmailAddress(), loginAttempts);

    }

    @Override
    public Response requestPasswordReset(String emailAddress) {
        return passwordService.requestPasswordReset(emailAddress);
    }

    @Override
    public Response resetPassword(PasswordResetRequest passwordResetRequest) {
        return passwordService.resetPassword(passwordResetRequest);
    }

    @Override
    public Response validatePasswordResetCode(PasswordResetRequest passwordResetRequest) {
        return passwordService.validatePasswordResetCode(passwordResetRequest);

    }

    @Override
    public Response createPin(String customerId, String pin) {
        UserEntity customer = getCustomerEntity(customerId);

        if (customer.getPin() != null) {
            throw new UserException(ResponseStatus.INVALID_PIN_UPDATE);
        }

        if (!NumberUtils.isNumeric(pin) || pin.length() != 4) {
            throw new UserException(ResponseStatus.PIN_FORMAT_ERROR);
        }

        customer.setPin(passwordService.encode(pin));
        userRepository.saveAndFlush(customer);

        return ResponseUtils.createDefaultSuccessResponse();
    }

    @Override
    public Response checkPin(String customerId, String pin) {
        UserEntity customer = getCustomerEntity(customerId);
        int count = customer.getPinAttempts() == null ? 0 : customer.getPinAttempts();

        if (count >= 3) {
            throw new UserException(ResponseStatus.RESET_PIN_FLAG);
        }

        if (!passwordService.passwordMatch(pin, customer.getPin())) {
            customer.setPinAttempts(count + 1);
            userRepository.saveAndFlush(customer);
            throw new UserException(ResponseStatus.WRONG_PIN);
        }

        customer.setPinAttempts(0);
        userRepository.saveAndFlush(customer);

        return ResponseUtils.createDefaultSuccessResponse();
    }


    @Override
    public Response resetPinInitiateEmail(String customerId, String phoneNumber) {

        log.debug("OTP for Reset Pin for customer ID: {}", customerId);

        UserEntity customer = getCustomerEntity(customerId);

        String customerPhoneNumber = customer.getPhoneNumber();

        if (!customerPhoneNumber.substring(customerPhoneNumber.length() - 4).equals(phoneNumber)) {
            throw new UserException(ResponseStatus.PHONE_NUMBER_NOT_FOUND);
        }

        try {
            final ContactVerification contactVerification = new ContactVerification();
            contactVerification.setEmailAddress(customer.getEmailAddress());

            final String verificationCode = NumberUtils.generate(5);

            log.debug("Reset code [{}] for email address [{}]", verificationCode, contactVerification.getEmailAddress());

            contactVerification.setVerificationCode(verificationCode);
            contactVerification.setGeneratedOn(Instant.now());

            contactVerification.setExpiredOn(Instant.now().plus(15, ChronoUnit.MINUTES));
            verificationRepository.saveAndFlush(contactVerification);

            log.info("Email address [{}] successfully added for verification", contactVerification.getEmailAddress());
        } catch (Exception e) {
            log.error("Failed to send email to [{}] for reset pin", customer.getEmailAddress(), e);
            throw new ContactVerificationException(ResponseStatus.PROCESSING_ERROR);
        }
        return ResponseUtils.createDefaultSuccessResponse();
    }

    public Response verifyResetPinCode(String customerId, String code) {

        UserEntity customer = getCustomerEntity(customerId);

//        log.debug("Verifying Reset pin code for customer ID: {}", customer.getUserId());

        final ContactVerification contactVerification = verificationRepository
                .findFirstByEmailAddressOrderByDateCreatedDesc(customer.getEmailAddress());

        if (contactVerification == null) {
            log.error("Email address [{}] not found for verification", customer.getEmailAddress());
            throw new ContactVerificationException(ResponseStatus.EMAIL_ADDRESS_NOT_FOUND);
        }

        log.debug("Found contact verification: {}", contactVerification);

        if (!contactVerification.getVerificationCode().equals(code)) {
            log.error("Invalid verification code [{}] for email address {}", code, customer.getEmailAddress());
            throw new ContactVerificationException(ResponseStatus.INVALID_VERIFICATION_CODE);
        }

        if (Instant.now().isAfter(contactVerification.getExpiredOn())) {
            log.error("Verification code [{}] has expired", code);
            throw new ContactVerificationException(ResponseStatus.EXPIRED_VERIFICATION_CODE);
        }

        contactVerification.setVerified(true);
        verificationRepository.saveAndFlush(contactVerification);

        return ResponseUtils.createDefaultSuccessResponse();
    }

    @Override
    public Response resetPin(String customerId, String pin) {
        UserEntity customer = getCustomerEntity(customerId);

        if (!NumberUtils.isNumeric(pin) || pin.length() != 4) {
            throw new UserException(ResponseStatus.PIN_FORMAT_ERROR);
        }

        customer.setPin(passwordService.encode(pin));
        customer.setPinAttempts(0);
        userRepository.saveAndFlush(customer);

        return ResponseUtils.createDefaultSuccessResponse();
    }

    @Override
    public Response allowSaveCard(String customerId, Boolean saveCard) {
        UserEntity customer = getCustomerEntity(customerId);
        customer.setSaveCard(saveCard);
        userRepository.saveAndFlush(customer);
        return ResponseUtils.createDefaultSuccessResponse();
    }

    @Override
    public PaginatedResponse<List<UserDto>> fetchAllUsersBy(int page, int size, String firstName, String lastName, String email, Long roleId) {

        log.info("Request to fetch all users page: {}, size {}, firstName : {}, lastName : {}, email: {}, role Id : {} ", page,size,firstName,lastName, email,roleId);
        try {
            Specification<UserEntity> spec = Specification.where(
                            UserSpecification.firstNameEqual(firstName))
                    .and(UserSpecification.lastNameEqual(lastName))
                    .and(UserSpecification.roleIdEqual(roleId))
                    .and(UserSpecification.emailEqual(email));

            Page<UserEntity> users = userRepository.findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated")));

            System.err.println("users "+users);
            PaginatedResponse<List<UserDto>> paginatedResponse = new PaginatedResponse<>();
            paginatedResponse.setPage(users.getNumber());
            paginatedResponse.setSize(users.getSize());
            paginatedResponse.setTotal((int) userRepository.count());
            paginatedResponse.setData(convertUserListToDto(users.getContent()));
            return paginatedResponse;
        } catch (Exception e) {
            log.error("An error occurred while fetching all users : {}", e.getMessage());
            throw new UserException("Failed to get all users ", 400);
        }
    }

    private boolean findAppOwnerByEmail(String email) {
        return userRepository.existsByEmailAddress(email);

    }

//    @PostConstruct
//    public void initializeSuperAdmin() {
//
//        String appOwnerEmail = "app_owner@ems.com";
//        String phoneNumber = "01234567890";
//        if(findAppOwnerByEmail(appOwnerEmail)){
//            log.info("App owner found for email: {}", appOwnerEmail);
//        } else {
//            UserEntity customer = new UserEntity();
//            customer.setEmailAddress(appOwnerEmail);
//            customer.setPassword(passwordService.encode("1234"));
//            customer.setFirstName("Rotimi");
//            customer.setLastName("Ojo");
//            customer.setPhoneNumber(phoneNumber);
////            customer.setUserId(phoneNumber);
////            customer.setUsername(appOwnerEmail);
//            RoleDto roleDto = new RoleDto("APP_OWNER", "Master Admin");
//            Role role = roleService.createUserRole(roleDto);
//            customer.setRole(role);
//
//            userRepository.save(customer);
//        }
//    }

    public Response toggleEnableUser(String phone){
        Optional<UserEntity> userEntityOptional = userRepository.findByPhoneNumber(phone);
        if (userEntityOptional.isEmpty()) {
            log.error("User not found :{}", phone );
            throw new UserException(ResponseStatus.PHONE_NUMBER_NOT_FOUND + phone);
        }
        UserEntity user = userEntityOptional.get();
        boolean isEnabled = user.isEnabled();
        user.setEnabled(!isEnabled);
        userRepository.save(user);

        return ResponseUtils.createSuccessResponse(user.isEnabled() ? "successfully enabled" : "successfully disabled");
    }


}
