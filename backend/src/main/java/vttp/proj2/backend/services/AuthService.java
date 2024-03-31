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

    @Autowired
    private MailSenderService mailSenderService; 

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
        String passwordHash = authRepo.getHashedPasswordByEmail(email);
        boolean isAuthenticated = checkPassword(rawPassword, passwordHash);
        return isAuthenticated;
    }

    //change password
    public boolean changeUserPassword(String userId, String currentPassword, String newPassword, String confirmNewPassword) {
       
        //verify current password
        String storedHashedPassword = authRepo.getHashedPasswordById(userId);
        System.out.println("Old pw hash: " + storedHashedPassword);
        if (!passwordEncoder.matches(currentPassword, storedHashedPassword)) {
            return false;
        }

        //checks passed, update password
        String newHashedPassword = passwordEncoder.encode(newPassword);
        System.out.println("New pw hash: " + newHashedPassword);
        return authRepo.updateUserPassword(userId, newHashedPassword);
    }

    @Transactional(rollbackFor = UserRegistrationException.class)
    public boolean registerNewUser(String firstName, String lastName, String email, String rawPassword) throws UserRegistrationException {
        AccountInfo newAcc = new AccountInfo();
        newAcc.setFirstName(firstName);
        newAcc.setLastName(lastName);
        newAcc.setEmail(email);
        newAcc.setPasswordHash(hashPassword(rawPassword));
        // System.out.println("test hashpw " + newAcc.getPasswordHash());
        // System.out.println("userid test " + newAcc.getUserId());
        newAcc.setLastPasswordResetDate(new Date());
        newAcc.setProfilePicUrl("/assets/logo-defaultuser.png");
        boolean doesEmailExist = authRepo.checkEmailExists(email);
        if (doesEmailExist){
            throw new UserRegistrationException("Email already exists: " + email);
        }
        boolean isRegistered = authRepo.createNewUser(newAcc);
        if (!isRegistered) {
            throw new UserRegistrationException("Cannot create new user: " + email);
        }
        boolean roleAssigned = authRepo.assignUserRole(newAcc.getUserId(), "ROLE_USER");
        if (!roleAssigned){
            throw new UserRegistrationException("Cannot assign role to new user: "+ email);
        }

        String subject = "Welcome to Study Trek!";
        String body = "<html>" +
                        "<body>" +
                        "<p>Hi " + firstName + ",</p>" +
                        "<p>Welcome to Study Trek. We're glad to have you on board!</p>" +
                        "<p>Click on the button below to embark on your journey.</p>" +
                        "<p><a href='https://study-trek.up.railway.app' style='display: inline-block; background-color: #007BFF; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Start learning</a></p>" +
                        "<p>Best Regards,<br>The Study Trek Team</p>" +
                        "</body>" +
                        "</html>";
        mailSenderService.sendNewMail(email, subject, body);
        return roleAssigned;
    }

}
