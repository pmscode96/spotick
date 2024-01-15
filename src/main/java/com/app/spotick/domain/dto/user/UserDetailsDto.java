package com.app.spotick.domain.dto.user;

import com.app.spotick.domain.entity.user.User;
import com.app.spotick.domain.entity.user.UserAuthority;
import com.app.spotick.domain.type.user.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor @Data
public class UserDetailsDto implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private String nickName;
    private String tel;
    private UserStatus userStatus;
    private List<UserAuthority> userAuthorities;

    public UserDetailsDto(User user, List<UserAuthority> userAuthorities) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.nickName = user.getNickName();
        this.tel = user.getTel();
        this.userStatus = user.getUserStatus();
        this.userAuthorities = userAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        userAuthorities.forEach(auth -> grantedAuthorities.add(
                (GrantedAuthority) () -> auth.getAuthorityType().name())
        );

        return grantedAuthorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // 계정이 만료되지 않았는지
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠긴건 아닌지?
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정 credential 이 만료된건 아닌지?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userStatus ==UserStatus.ACTIVATE;
    }
}