package vttp.proj2.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vttp.proj2.backend.models.AccountInfo;
import vttp.proj2.backend.models.AccountInfoPrincipal;
import vttp.proj2.backend.repositories.AuthRepository;

@Service
public class AuthUserDetailsService implements UserDetailsService  {

    @Autowired
    private AuthRepository authRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         AccountInfo user = authRepo.findUserByEmail(email);
         if (user == null) {
            throw new UsernameNotFoundException("User email not found!");
        }
        return new AccountInfoPrincipal(user);
    }
    
}
