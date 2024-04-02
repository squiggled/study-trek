package vttp.proj2.backend.controllers;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpServletResponse;
import vttp.proj2.backend.dtos.PasswordChangeRequestDTO;
import vttp.proj2.backend.exceptions.UserRegistrationException;
import vttp.proj2.backend.exceptions.UserWrongPasswordException;
import vttp.proj2.backend.models.AccountInfo;
import vttp.proj2.backend.models.FriendInfo;
import vttp.proj2.backend.models.Notification;
import vttp.proj2.backend.services.AuthService;
import vttp.proj2.backend.services.AuthUserDetailsService;
import vttp.proj2.backend.services.SecurityTokenService;
import vttp.proj2.backend.services.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authSvc;
    @Autowired
    AuthUserDetailsService authDetailsSvc;
    @Autowired
    UserService userSvc;

    @Autowired
    SecurityTokenService tokenSvc;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody String payload, HttpServletResponse response) {
        Reader reader = new StringReader(payload);
        JsonReader jsonReader = Json.createReader(reader);
        JsonObject authObj = jsonReader.readObject();

        String email = null;
        String rawPassword = null;

        if (authObj.containsKey("email") && !authObj.isNull("email")) {
            email = authObj.getString("email");
        }
        if (authObj.containsKey("password") && !authObj.isNull("password")) {
            rawPassword = authObj.getString("password");
        }

        boolean isAuthenticated = authSvc.authenticateLogin(email, rawPassword);
        if (isAuthenticated) {
            //get user info
            AccountInfo user = userSvc.getUserByEmail(email);

            //create JWT
            UserDetails userDetails = authDetailsSvc.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            String userRole = user.getRole();
            String tokenDetails;
            if (userRole.equals("ROLE_SUBSCRIBER")){
                System.out.println("Generating JWT for subscriber");
                tokenDetails = tokenSvc.generateTokenWithUpdatedRoles(authentication);

            } else {
                tokenDetails = tokenSvc.generateToken(authentication);
            }
            
            //get notifs
            List<Notification> notifs = userSvc.getNotifications(user.getUserId());
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "🟢 User " + email + " authenticated successfully");
            responseMap.put("user", user);
            responseMap.put("notifications", notifs);
            List<FriendInfo> friends = userSvc.getAllFriends(user.getUserId());
            responseMap.put("friendList", friends);
            System.out.println(notifs);
            responseMap.put("authenticated", true);
            responseMap.put("token", tokenDetails);
            System.out.println("Generated JWT: " + tokenDetails); 

            return ResponseEntity.ok(responseMap);
        } else {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "🔴 Login failed");
            responseMap.put("authenticated", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@RequestBody String payload) {
        System.out.println(payload);

        Reader reader = new StringReader(payload);
        JsonReader jsonReader = Json.createReader(reader);
        JsonObject authObj = jsonReader.readObject();

        String firstName = null;
        String lastName = null;
        String email = null;
        String rawPassword = null;

        if (authObj.containsKey("firstName") && !authObj.isNull("firstName")) {
            firstName = authObj.getString("firstName");
        }
        if (authObj.containsKey("lastName") && !authObj.isNull("lastName")) {
            lastName = authObj.getString("lastName");
        }
        if (authObj.containsKey("email") && !authObj.isNull("email")) {
            email = authObj.getString("email");
        }
        if (authObj.containsKey("password") && !authObj.isNull("password")) {
            rawPassword = authObj.getString("password");
        }

        try {
            authSvc.registerNewUser(firstName, lastName, email, rawPassword);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully registered user: " + email);
            return ResponseEntity.ok(response);
        } catch (UserRegistrationException e) {
            System.out.println("Registration failed: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Registration failed");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
    }

    @GetMapping("/loaduser")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String userId = jwt.getClaimAsString("sub"); // get the subject claim to use as username
            AccountInfo acc = userSvc.getUserById(userId);
            // System.out.println(acc.getRegisteredCourses());
            if (acc==null){
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "🔴 User not found");
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(acc);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "🔴 JWT authentication error");
            return ResponseEntity.badRequest().body(errorResponse);
        }        
    }

    //change password
    @PostMapping("/password/{userId}")
    public ResponseEntity<?> changePassword(@PathVariable String userId, @RequestBody PasswordChangeRequestDTO dto){
        // System.out.println("this end point was reached " + dto);
        try {
            String newHashedPw = authSvc.changeUserPassword(userId, dto.getCurrentPassword(), dto.getNewPassword());
            if (null==newHashedPw){
                //cannot find current user in db
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("notFound", "🔴 Current user does not exist");
                // System.out.println("not found");
                return ResponseEntity.notFound().build();
            } 
            Map<String, String> response = new HashMap<>();
            response.put("newHashedPassword", newHashedPw);
            return ResponseEntity.ok(response);
        } catch (UserWrongPasswordException e){
                // user entered wrong password in form
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "🔴 Current passwords do not match");
                // System.out.println("wrong pw");
                return ResponseEntity.badRequest().body(errorResponse);
        }
        
    }
}
