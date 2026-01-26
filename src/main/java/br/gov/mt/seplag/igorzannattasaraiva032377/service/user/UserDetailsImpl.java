package br.gov.mt.seplag.igorzannattasaraiva032377.service.user;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.user.AppUserEntity;

public class UserDetailsImpl implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String name;
    private final String username;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(UUID id, String name, String username, String password, boolean active,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(AppUserEntity user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getEmail(), // email é usado como username
                user.getPasswordHash(),
                user.isActive(),
                Collections.emptyList()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return active;
    }
}
