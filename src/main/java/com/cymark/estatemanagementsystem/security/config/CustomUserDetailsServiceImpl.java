package com.cymark.estatemanagementsystem.security.config;

import com.cymark.estatemanagementsystem.model.dto.CustomerDto;
import com.cymark.estatemanagementsystem.model.dto.PermissionDto;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.repository.UserRepository;
import com.cymark.estatemanagementsystem.security.model.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;



@Slf4j
@Service("customUserDetailsService")
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    @Autowired
    private UserRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> customer = customerRepository.findByEmailAddress(username);
        log.info(" email val {} :", customer);

        if (customer.isEmpty()) {
            throw new UsernameNotFoundException(String.format("customer not found for email address=%s", username));
        }

        CustomerDto customerDto = new CustomerDto(customer.get());
        customerDto.setPassword(customer.get().getPassword());

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        if(customerDto.getRole().getPermissionsDto() != null){
            for(PermissionDto permissionDto: customerDto.getRole().getPermissionsDto()){
                grantedAuthorities.add(new SimpleGrantedAuthority(permissionDto.getName()));
            }
        }

        log.info("grantedAuthorities : {}", grantedAuthorities);

        return new CustomUserDetails(customerDto, grantedAuthorities);
    }
}
