package vttp.proj2.backend.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vttp.proj2.backend.exceptions.UserRegistrationException;
import vttp.proj2.backend.models.AccountInfo;
import vttp.proj2.backend.repositories.AuthRepository;
import vttp.proj2.backend.repositories.UserRepository;

@Service
public class AuthService {
    
    @Autowired
    AuthRepository authRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String hashPassword(String rawPassword) {
        String hashedPassword = passwordEncoder.encode(rawPassword);
        return hashedPassword;
    }

    public boolean checkPassword(String rawPassword, String storedHashedPassword) {
        return passwordEncoder.matches(rawPassword, storedHashedPassword);
    }

    public boolean authenticateLogin(String email, String rawPassword){
        boolean emailExists = authRepo.checkEmailExists(email);
        if (!emailExists) return false;
        String passwordHash = authRepo.getHashedPassword(email);
        boolean isAuthenticated = checkPassword(rawPassword, passwordHash);
        return isAuthenticated;
    }

    @Transactional(rollbackFor = UserRegistrationException.class)
    public boolean registerNewUser(String firstName, String lastName, String email, String rawPassword) throws UserRegistrationException {
        AccountInfo newAcc = new AccountInfo();
        newAcc.setFirstName(firstName);
        newAcc.setLastName(lastName);
        newAcc.setEmail(email);
        newAcc.setPasswordHash(hashPassword(rawPassword));
        System.out.println("test hashpw " + newAcc.getPasswordHash());
        System.out.println("userid test " + newAcc.getUserId());
        newAcc.setLastPasswordResetDate(new Date());
        newAcc.setProfilePicUrl("/assets/logo-defaultuser.png");

        boolean isRegistered = authRepo.createNewUser(newAcc);
        if (!isRegistered) {
            throw new UserRegistrationException("Cannot create new user: " + email);
        }
        boolean roleAssigned = authRepo.assignUserRole(newAcc.getUserId(), "ROLE_USER");
        if (!roleAssigned){
            throw new UserRegistrationException("Cannot assign role to new user: "+ email);
        }
        return roleAssigned;
    }

}
