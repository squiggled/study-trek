package vttp.proj2.backend.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vttp.proj2.backend.models.AccountInfo;
import vttp.proj2.backend.repositories.AuthRepository;

@Service
public class AuthService {
    
    @Autowired
    AuthRepository authRepo;

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

    public boolean registerNewUser(String firstName, String lastName, String email, String rawPassword) {
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
        return isRegistered;
    }


}
