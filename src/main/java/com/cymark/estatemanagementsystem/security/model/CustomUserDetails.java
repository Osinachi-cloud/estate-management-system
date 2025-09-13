package com.cymark.estatemanagementsystem.security.model;

import com.cymark.estatemanagementsystem.model.dto.CustomerDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private CustomerDto user;

    private Collection<? extends GrantedAuthority> authorities;


    public CustomUserDetails(CustomerDto user, Set<GrantedAuthority> grantedAuthorities) {
        System.out.println("entered CustomUserDetails constructor");
        this.user = user;
        this.authorities = grantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("autho================");
        System.out.println(authorities);
        System.out.println("autho================");
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmailAddress();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public CustomerDto getUser() {
        return user;
    }

}
