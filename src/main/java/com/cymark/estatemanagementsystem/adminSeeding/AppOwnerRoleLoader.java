package com.cymark.estatemanagementsystem.adminSeeding;

import com.cymark.estatemanagementsystem.model.entity.*;
import com.cymark.estatemanagementsystem.model.enums.Designation;
import com.cymark.estatemanagementsystem.repository.*;
import com.cymark.estatemanagementsystem.service.PasswordService;
import com.cymark.estatemanagementsystem.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static com.cymark.estatemanagementsystem.util.DtoMapper.mapRoleToDto;

@Component
public class AppOwnerRoleLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    private final Logger log = LoggerFactory.getLogger(AppOwnerRoleLoader.class);

    boolean alreadySetup = false;

    @Autowired
    private UserRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordService passwordService;


    @Autowired
    private EstateRepository estateRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        Permission createAdmin
                = createPrivilegeIfNotFound("CREATE_SUPER_ADMIN", "create super admin", "owner");

        Permission createUser
                = createPrivilegeIfNotFound("ONBOARD_ESTATE", "create an estate", "estate_management");

        Permission fetchAllCustomers
                = createPrivilegeIfNotFound("FETCH_ALL_CUSTOMERS", "fetch all customers", "customers");

        createRoleIfNotFound("APP_ADMIN", "All Permission", Arrays.asList(
                createUser,
                createAdmin,
                fetchAllCustomers

        ));

        Optional<Role> adminRole = roleRepository.findByName("APP_ADMIN");
        Role adR = new Role();
        if(adminRole.isPresent()){
            adminRole.get().setName("APP_ADMIN");

//       adminRole.setPermissions(List.of(readPrivilege, writePrivilege, deletePrivilege, createAdminPrivilege));
            adR = roleRepository.save(adminRole.get());
            log.info("appAdmin : {}", mapRoleToDto(adR));
        }

        Optional<Role> tenantRole = roleRepository.findByName("TENANT");

        if(tenantRole.isPresent()){
            log.info("Tenant role exist");
        }else {
            Role tR = new Role();
            tR.setName("TENANT");
            tR.setDescription("Set tenant role");
            roleRepository.save(tR);
        }

        Optional<UserEntity> existingSuperAdmin = adminRepository.findByEmail("appadmin@ems.com");

        if(existingSuperAdmin.isEmpty()){
            UserEntity admin = new UserEntity();
            admin.setFirstName("Test");
            admin.setLastName("Test");
            admin.setPhone("+23409876541");
            admin.setPassword(passwordService.encode("AB$12345"));
            admin.setEmail("appadmin@ems.com");
            admin.setDesignation(Designation.EXTERNAL);
            admin.setUserId("23409876541");
//            admin.setUsername("admin@ems.com");
//            admin.setUserId(NumberUtils.generate(9));
            admin.setRole(adR);

            Estate estate = new Estate();
            estate.setName("Dummy");
            estate.setPostalCode("******");
            estate.setCity("Dummy");
            estate.setCountry("Dummy");
            String estateId = NumberUtils.generate(10);
            estate.setEstateId(estateId);
            Estate savedEstate = estateRepository.save(estate);

            Address add = new Address();
            add.setStreet("Street");
            add.setEstate(savedEstate);
            addressRepository.save(add);

            admin.setEstateId(estateId);
            admin.setEnabled(true);
            adminRepository.save(admin);
        }

        alreadySetup = true;
    }

    @Transactional
    Permission createPrivilegeIfNotFound(String name, String description, String category) {

        Optional<Permission> privilege = permissionRepository.findByName(name);
        if (privilege.isEmpty()) {
            Permission permission = new Permission(name, description, category);
            return permissionRepository.save(permission);
        }else {
            return privilege.get();
        }
    }

    @Transactional
    Role createRoleIfNotFound(String name, String description, Collection<Permission> permissions) {

        Collection<Permission> permissionList = permissionRepository.saveAll(permissions);
        Optional<Role> roleExists = roleRepository.findByName(name);
        if (roleExists.isEmpty()) {
            Role role = new Role(name, description);
            role.setPermissions(permissionList);
            return roleRepository.save(role);
        }else {
            Role role = roleExists.get();
            role.setPermissions(permissionList);
            log.info("permissionList : {}", permissionList);
            return roleRepository.save(role);
        }
    }
}


