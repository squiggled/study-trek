package vttp.proj2.backend.models;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoPrincipal implements UserDetails{

    private AccountInfo accInfo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
       return accInfo.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return accInfo.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return accInfo!=null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accInfo!=null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return accInfo != null;
    }

    @Override
    public boolean isEnabled() {
        return accInfo != null;
    }
    
    public String getUserId() {
        return accInfo.getUserId();
    }
}
