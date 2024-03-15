package vttp.proj2.backend.controllers;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpServletResponse;
import vttp.proj2.backend.exceptions.UserRegistrationException;
import vttp.proj2.backend.models.AccountInfo;
import vttp.proj2.backend.models.AccountInfoPrincipal;
import vttp.proj2.backend.services.AuthService;
import vttp.proj2.backend.services.AuthUserDetailsService;
import vttp.proj2.backend.services.SecurityTokenService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AuthController {

    @Autowired
    AuthService authSvc;
    @Autowired
    AuthUserDetailsService authDetailsSvc;

    @Autowired
    SecurityTokenService tokenSvc;

    @PostMapping("/user/login")
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
            AccountInfo user = authSvc.getUserByEmail(email);

            //create JWT
            UserDetails userDetails = authDetailsSvc.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            String tokenDetails = tokenSvc.generateToken(authentication);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "🟢 User " + email + " authenticated successfully");
            responseMap.put("user", user);
            responseMap.put("authenticated", true);
            responseMap.put("token", tokenDetails);
            System.out.println("Generated JWT: " + tokenDetails);
            // responseMap.put("token", token); 

            return ResponseEntity.ok(responseMap);
                    // .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    // .body(responseMap);
        } else {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "🔴 Login failed");
            responseMap.put("authenticated", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

    }

    @PostMapping("/user/register")
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

        boolean isRegistered = false;
        try {
            isRegistered = authSvc.registerNewUser(firstName, lastName, email, rawPassword);

        } catch (UserRegistrationException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed");
        }
        return ResponseEntity.ok("Successfully registered user: " + email);
    }

    @GetMapping("/user/loaduser")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String email = jwt.getClaimAsString("sub"); // Example: get the subject claim to use as username
            AccountInfo acc = authSvc.getUserByEmail(email);
            if (acc==null){
                return ResponseEntity.badRequest().body("User not found");
            }
            return ResponseEntity.ok(acc);
        } else {
            return ResponseEntity.badRequest().body("JWT error");
        }
        // AccountInfoPrincipal principal = (AccountInfoPrincipal) authentication.getPrincipal();
        
        
    }
}
