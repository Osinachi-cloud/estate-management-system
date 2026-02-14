package com.cymark.estatemanagementsystem.util;

import com.cymark.estatemanagementsystem.model.dto.UserDto;
//import com.cymark.estatemanagementsystem.model.dto.hierarchy.DetailedUserViewResponse;
//import com.cymark.estatemanagementsystem.model.dto.hierarchy.HierarchyStatistics;
//import com.cymark.estatemanagementsystem.model.dto.hierarchy.UserHierarchyResponse;
import com.cymark.estatemanagementsystem.model.dto.response.DetailedUserViewResponse;
import com.cymark.estatemanagementsystem.model.dto.response.HierarchyStatistics;
import com.cymark.estatemanagementsystem.model.dto.response.UserHierarchyResponse;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.enums.Designation;
import com.cymark.estatemanagementsystem.repository.UserRepository;
//import com.cymark.estatemanagementsystem.service.UserHierarchyService;
import com.cymark.estatemanagementsystem.service.UserHierarchyService;
import com.cymark.estatemanagementsystem.service.impl.UserHierarchyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserHierarchyServiceImpl implements UserHierarchyService {

    private final UserRepository userRepository;
    private final UserHierarchyMapper mapper;

    @Override
    public UserHierarchyResponse getUserHierarchy(String userId) {
        // 1. Get the user
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Determine user role based on designation or role name
        String roleName = user.getRole() != null ? user.getRole().getName() : "";
        String hierarchyType = determineHierarchyType(roleName, user.getDesignation());

        // 3. Build hierarchy response
        UserHierarchyResponse.UserHierarchyResponseBuilder builder = UserHierarchyResponse.builder()
                .user(mapper.toUserDto(user))
                .hierarchyType(hierarchyType);

        // 4. Populate based on role
        switch (hierarchyType) {
            case "landlord":
                populateLandlordHierarchy(builder, user);
                break;
            case "tenant":
                populateTenantHierarchy(builder, user);
                break;
            case "occupant":
                populateOccupantHierarchy(builder, user);
                break;
            case "external":
                populateExternalHierarchy(builder, user);
                break;
            default:
                // No hierarchy relationships
                builder.parentUser(null)
                        .landlord(null)
                        .tenants(new ArrayList<>())
                        .occupants(new ArrayList<>())
                        .children(new ArrayList<>());
                break;
        }

        return builder.build();
    }

    @Override
    public DetailedUserViewResponse getDetailedUserView(String userId) {
        UserEntity user = userRepository.findUserEntityByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        String roleName = user.getRole() != null ? user.getRole().getName() : "";
        String hierarchyType = determineHierarchyType(roleName, user.getDesignation());

        DetailedUserViewResponse.DetailedUserViewResponseBuilder builder = DetailedUserViewResponse.builder()
                .user(mapper.toUserDto(user));

        List<UserDto> allRelatedUsers = new ArrayList<>();

        switch (hierarchyType) {
            case "landlord":
                // Get all tenants under this landlord
                List<UserEntity> tenants = userRepository.findByLandlordId(user.getUserId());
                List<UserDto> tenantDtos = mapper.toUserDtoList(tenants);
                builder.tenants(tenantDtos);
                allRelatedUsers.addAll(tenantDtos);

                // Get all occupants under these tenants
                List<UserEntity> allOccupants = new ArrayList<>();
                for (UserEntity tenant : tenants) {
                    List<UserEntity> occupants = userRepository.findByTenantId(tenant.getUserId());
                    allOccupants.addAll(occupants);
                }
                List<UserDto> occupantDtos = mapper.toUserDtoList(allOccupants);
                builder.occupants(occupantDtos);
                allRelatedUsers.addAll(occupantDtos);
                break;

            case "tenant":
                // Get landlord
                if (user.getLandlordId() != null) {
                    userRepository.findByUserId(user.getLandlordId())
                            .ifPresent(landlord -> {
                                builder.landlord(mapper.toUserDto(landlord));
                                allRelatedUsers.add(mapper.toUserDto(landlord));
                            });
                }

                // Get occupants
                List<UserEntity> occupants = userRepository.findByTenantId(user.getUserId());
                List<UserDto> occupantDtos1 = mapper.toUserDtoList(occupants);
                builder.occupants(occupantDtos1);
                allRelatedUsers.addAll(occupantDtos1);
                break;

            case "occupant", "external":
                // Get tenant
                if (user.getTenantId() != null) {
                    userRepository.findByUserId(user.getTenantId())
                            .ifPresent(tenant -> {
                                builder.landlord(mapper.toUserDto(tenant));
                                allRelatedUsers.add(mapper.toUserDto(tenant));

                                // Get landlord through tenant
                                if (tenant.getLandlordId() != null) {
                                    userRepository.findByUserId(tenant.getLandlordId())
                                            .ifPresent(landlord -> {
                                                builder.landlord(mapper.toUserDto(landlord));
                                                allRelatedUsers.add(mapper.toUserDto(landlord));
                                            });
                                }
                            });
                }
                break;
        }

        // Calculate statistics
        HierarchyStatistics statistics = calculateStatistics(user, hierarchyType);

        return builder
                .allRelatedUsers(allRelatedUsers)
                .statistics(statistics)
                .build();
    }

    private String determineHierarchyType(String roleName, Designation designation) {
        if (designation == Designation.LANDLORD) {
            return "landlord";
        } else if (designation == Designation.TENANT) {
            return "tenant";
        } else if (designation.equals(Designation.OCCUPANT)) {
            return "occupant";
        } else if (designation.equals(Designation.EXTERNAL)) {
            return "external";
        }
        return null;
    }

    private void populateLandlordHierarchy(UserHierarchyResponse.UserHierarchyResponseBuilder builder, UserEntity landlord) {
        // Get all tenants under this landlord
        List<UserEntity> tenants = userRepository.findByLandlordId(landlord.getUserId());
        List<UserDto> tenantDtos = mapper.toUserDtoList(tenants);

        builder.parentUser(null)
                .landlord(null)
                .tenants(tenantDtos)
                .occupants(new ArrayList<>())
                .children(tenantDtos);
    }

    private void populateTenantHierarchy(UserHierarchyResponse.UserHierarchyResponseBuilder builder, UserEntity tenant) {
        // Get landlord
        UserDto landlordDto = null;
        if (tenant.getLandlordId() != null) {
            Optional<UserEntity> landlord = userRepository.findByUserId(tenant.getLandlordId());
            landlordDto = landlord.map(mapper::toUserDto).orElse(null);
        }

        // Get occupants
        List<UserEntity> occupants = userRepository.findByTenantId(tenant.getUserId());
        List<UserDto> occupantDtos = mapper.toUserDtoList(occupants);

        builder.parentUser(landlordDto)
                .landlord(landlordDto)
                .tenants(new ArrayList<>())
                .occupants(occupantDtos)
                .children(occupantDtos);
    }

    private void populateOccupantHierarchy(UserHierarchyResponse.UserHierarchyResponseBuilder builder, UserEntity occupant) {
        // Get tenant
        UserDto tenantDto = null;
        UserDto landlordDto = null;

        if (occupant.getTenantId() != null) {
            Optional<UserEntity> tenant = userRepository.findByUserId(occupant.getTenantId());
            tenantDto = tenant.map(mapper::toUserDto).orElse(null);

            // Get landlord through tenant
            if (tenant.isPresent() && tenant.get().getLandlordId() != null) {
                Optional<UserEntity> landlord = userRepository.findByUserId(tenant.get().getLandlordId());
                landlordDto = landlord.map(mapper::toUserDto).orElse(null);
            }
        }

        builder.parentUser(tenantDto)
                .landlord(landlordDto)
                .tenants(new ArrayList<>())
                .occupants(new ArrayList<>())
                .children(new ArrayList<>());
    }

    private void populateExternalHierarchy(UserHierarchyResponse.UserHierarchyResponseBuilder builder, UserEntity external) {
        // Get tenant
        UserDto tenantDto = null;
        UserDto landlordDto = null;

        if (external.getTenantId() != null) {
            Optional<UserEntity> tenant = userRepository.findByUserId(external.getTenantId());
            tenantDto = tenant.map(mapper::toUserDto).orElse(null);

            // Get landlord through tenant
            if (tenant.isPresent() && tenant.get().getLandlordId() != null) {
                Optional<UserEntity> landlord = userRepository.findByUserId(tenant.get().getLandlordId());
                landlordDto = landlord.map(mapper::toUserDto).orElse(null);
            }
        }

        builder.parentUser(tenantDto)
                .landlord(landlordDto)
                .tenants(new ArrayList<>())
                .occupants(new ArrayList<>())
                .children(new ArrayList<>());
    }

    private HierarchyStatistics calculateStatistics(UserEntity user, String hierarchyType) {
        HierarchyStatistics.HierarchyStatisticsBuilder builder = HierarchyStatistics.builder();

        switch (hierarchyType) {
            case "landlord":
                List<UserEntity> tenants = userRepository.findByLandlordId(user.getUserId());
                long tenantCount = tenants.size();
                long occupantCount = 0;

                for (UserEntity tenant : tenants) {
                    occupantCount += userRepository.findByTenantId(tenant.getUserId()).size();
                }

                builder.totalTenants(tenantCount)
                        .totalOccupants(occupantCount)
                        .totalProperties(1L) // You can calculate this from estate/property entities
                        .activeLeases(tenantCount); // Assuming all tenants have active leases
                break;

            case "tenant":
                List<UserEntity> occupants = userRepository.findByTenantId(user.getUserId());
                builder.totalTenants(0L)
                        .totalOccupants((long) occupants.size())
                        .totalProperties(1L)
                        .activeLeases(1L);
                break;

            default:
                builder.totalTenants(0L)
                        .totalOccupants(0L)
                        .totalProperties(0L)
                        .activeLeases(0L);
                break;
        }

        return builder.build();
    }
}

//public enum Designation {
//    LANDLORD,
//    TENANT,
//    EXTERNAL,
//    DEFAULT,
//    OCCUPANT
//}
