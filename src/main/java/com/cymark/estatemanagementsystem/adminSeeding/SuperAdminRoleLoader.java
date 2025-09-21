package com.cymark.estatemanagementsystem.adminSeeding;

import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.model.entity.Permission;
import com.cymark.estatemanagementsystem.model.entity.Role;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.repository.PermissionRepository;
import com.cymark.estatemanagementsystem.repository.RoleRepository;
import com.cymark.estatemanagementsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Service
public class SuperAdminRoleLoader {
    private final Logger log = LoggerFactory.getLogger(SuperAdminRoleLoader.class);

    @Autowired
    private UserRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Transactional
    public void addSuperAdminRole(UserEntity userEntity) {

        Permission createAdmin
                = createPrivilegeIfNotFound("CREATE_ADMIN", "create admin", "admin");

        Permission createUser
                = createPrivilegeIfNotFound("CREATE_USER", "create user", "user_management");

        Permission fetchAllCustomers
                = createPrivilegeIfNotFound("FETCH_ALL_CUSTOMERS", "fetch all customers", "customers");

        Permission assignRole
                = createPrivilegeIfNotFound("ASSIGN_ROLE", "assign role", "role");

        Permission assignPermission
                = createPrivilegeIfNotFound("ASSIGN_PERMISSION", "assign permission", "permission");

        Permission createRole
                = createPrivilegeIfNotFound("CREATE_ROLE", "create role", "role");

        Permission deleteAdmin
                = createPrivilegeIfNotFound("DELETE_ADMIN", "delete admin", "admin");

        Permission deleteRole
                = createPrivilegeIfNotFound("DELETE_ROLE", "delete role", "role");

        Permission updateAdmin
                = createPrivilegeIfNotFound("UPDATE_ADMIN", "update admin", "admin");

        Permission fetchAdmins
                = createPrivilegeIfNotFound("FETCH_ADMINS", "fetch admins", "admins");

        Permission changeActiveStatus
                = createPrivilegeIfNotFound("CHANGE_ACTIVE_STATUS", "change active status", "status");

        Permission changeAnAdminActiveStatus
                = createPrivilegeIfNotFound("CHANGE_ADMIN_ACTIVE_STATUS", "change an admin active status", "status");

        Permission fetchCustomerStats
                = createPrivilegeIfNotFound("FETCH_CUSTOMER_STATS", "fetch customer stats", "customer");

        Permission fetchCustomer
                = createPrivilegeIfNotFound("FETCH_CUSTOMER", "fetch customer", "customer");

        Permission createCountry
                = createPrivilegeIfNotFound("CREATE_COUNTRY", "create country", "country");

        Permission getCountries
                = createPrivilegeIfNotFound("GET_COUNTRIES", "get countries", "countries");

        Permission verifyContact
                = createPrivilegeIfNotFound("VERIFY_CONTACT", "verify contact", "contact");

        Permission editCurrencyDetails
                = createPrivilegeIfNotFound("EDIT_CURRENCY_DETAILS", "edit currency details", "currency details");

        Permission createCurrencyDetails
                = createPrivilegeIfNotFound("CREATE_CURRENCY_DETAILS", "create currency details", "currency details");

        Permission getNairaEquivalence
                = createPrivilegeIfNotFound("GET_NAIRA_EQUIVALENCE", "get Naira equivalence", "equivalence");

        Permission getFxRate
                = createPrivilegeIfNotFound("GET_FX_RATE", "get FX rate", "rate");

        Permission justTesting
                = createPrivilegeIfNotFound("JUST_TESTING", "just testing", "test");

        Permission getsAllRole
                = createPrivilegeIfNotFound("GET_ALL_ROLES", "Gets all Role", "role");

        Permission findAllPermissionByCategory
                = createPrivilegeIfNotFound("GET_ALL_PERMISSIONS", "Gets all Permission ", "permission");

        Permission removePermissionsAndUpdate
                = createPrivilegeIfNotFound("UPDATE_ROLE_REMOVE_PERMISSION", "Removes some Permission and Update", "permission");

        Permission addPermissionsAndUpdate
                = createPrivilegeIfNotFound("UPDATE_ROLE_ADD_PERMISSION", "Adds some Permission and Update", "permission");

        Permission updateTransFeeConfig
                = createPrivilegeIfNotFound("UPDATE_TRANS_FEE_CONFIG", "update transaction fee config", "trans-config");

        Permission createTransFeeConfig
                = createPrivilegeIfNotFound("CREATE_TRANS_FEE_CONFIG", "Create transaction fee config", "trans-config");

        Permission findAllTransFeeConfig
                = createPrivilegeIfNotFound("FIND_ALL_TRANS_FEE_CONFIG", "find all transaction fee config", "trans-config");

        Permission fetchAdminStats
                = createPrivilegeIfNotFound("FETCH_ADMIN_STATS", "fetch admin stats", "stats");

        Permission updateRoleAddPermission
                = createPrivilegeIfNotFound("UPDATE_ROLE_ADD_PERMISSION", "update role add permission", "permission");

        Permission toggleEnableUser
                = createPrivilegeIfNotFound("TOGGLE_ENABLE_USER", "Enable User", "user_management");

        createRoleIfNotFound("SUPER_ADMIN", "Overseas other admin within and estate", Arrays.asList(
                createUser,
                createAdmin,
                createRole,
                assignRole,
                assignPermission,
                deleteAdmin,
                deleteRole,
                updateAdmin,
                fetchAdmins,
                fetchAllCustomers,
                changeActiveStatus,
                fetchCustomerStats,
                fetchCustomer,
                createCountry,
                getCountries,
                verifyContact,
                createCurrencyDetails,
                editCurrencyDetails,
                getFxRate,
                getNairaEquivalence,
                justTesting,
                getsAllRole,
                findAllPermissionByCategory,
                removePermissionsAndUpdate,
                addPermissionsAndUpdate,
                updateTransFeeConfig,
                createTransFeeConfig,
                findAllTransFeeConfig,
                fetchAdminStats,
                updateRoleAddPermission,
                changeAnAdminActiveStatus,
                toggleEnableUser
        ));

        Optional<Role> adminRole = roleRepository.findByName("SUPER_ADMIN");
        Optional<UserEntity> existingSuperAdmin = adminRepository.findByEmail(userEntity.getEmail());

        if(existingSuperAdmin.isEmpty()){
            throw new CymarkException("This Super admin does not exists");
        }

        if(adminRole.isEmpty()){
            throw new CymarkException("This Super admin role does not exists");
        }

        UserEntity superAdmin = existingSuperAdmin.get();

        superAdmin.setRole(adminRole.get());
        superAdmin.setEnabled(true);
        adminRepository.save(superAdmin);

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
