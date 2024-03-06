package vttp.proj2.backend.controllers;

import java.io.Reader;
import java.io.StringReader;

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

        if (isAuthenticated) return ResponseEntity.ok("User authenticated");
        return ResponseEntity.badRequest().body("Login failed");

    }

    @PostMapping("/user/register")
    public ResponseEntity<?> registerNewUser(){
        return null;
    }
}
