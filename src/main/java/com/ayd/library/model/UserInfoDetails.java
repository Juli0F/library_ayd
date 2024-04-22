package com.ayd.library.model;

import com.ayd.library.enums.Rol;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserInfoDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Rol rol;
    private final Boolean status;

    public UserInfoDetails(User user) {
        username = user.getUsername();
        password = user.getPassword();
        rol = user.getRole();
        status = user.getStatus() == 1;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
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
        return status;
    }
}
