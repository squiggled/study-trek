package vttp.proj2.backend.models;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        return accInfo.getUserId();
    }

    public String getEmail() {
        return accInfo.getEmail();
    }

    public String getFirstName() {
        return accInfo.getFirstName();
    }

    public String getLastName() {
        return accInfo.getLastName();
    }

    public String getProfilePicUrl() {
        return accInfo.getProfilePicUrl();
    }

    public List<String> getInterests() {
        return accInfo.getInterests();
    }

    public List<CourseDetails> getRegisteredCourses() {
        return accInfo.getRegisteredCourses();
    }

    public List<String> getFriendIds() {
        return accInfo.getFriendIds();
    }

    public String getRole() {
        return accInfo.getRole();
    }

    public Date getLastPasswordResetDate() {
        return accInfo.getLastPasswordResetDate();
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
