package vttp.proj2.backend.controllers;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.proj2.backend.services.AuthService;

@RestController
@RequestMapping("/api")
public class AuthController {
    
    @Autowired
    AuthService authSvc;

    @PostMapping("/user/login")
    public ResponseEntity<?> authenticateUser(@RequestBody String payload){
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
         Map<String, Object> response = new HashMap<>();
        if (isAuthenticated) {
            response.put("message", "User " + email+ " authenticated");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Login failed");
            return ResponseEntity.badRequest().body(response);
        }

    }

    @PostMapping("/user/register")
    public ResponseEntity<?> registerNewUser(@RequestBody String payload){
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
       
        boolean isRegistered = authSvc.registerNewUser(firstName, lastName, email, rawPassword);
        return null;
    }
}
