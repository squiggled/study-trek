package vttp.proj2.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vttp.proj2.backend.repositories.AuthRepository;

@Service
public class AuthService {
    
    @Autowired
    AuthRepository authRepo;

    private PasswordEncoder passwordEncoder;

    public void UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String hashPassword(String username, String rawPassword) {
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


}
