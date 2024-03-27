package vttp.proj2.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;

import jakarta.json.Json;
import vttp.proj2.backend.dtos.SubscriptionRequestDTO;
import vttp.proj2.backend.exceptions.UserChangeSubscriptionStatusException;

import vttp.proj2.backend.services.AuthService;
import vttp.proj2.backend.services.AuthUserDetailsService;
import vttp.proj2.backend.services.SecurityTokenService;
import vttp.proj2.backend.services.StripeService;
import vttp.proj2.backend.services.SubscriptionService;
import vttp.proj2.backend.services.UserService;

@RestController
@RequestMapping("/api/subscription")
@CrossOrigin
public class PaymentController {

    @Autowired
    StripeService stripeSvc;
    @Autowired
    SubscriptionService subscriptionSvc;
    @Autowired
    AuthService authSvc;
    @Autowired
    AuthUserDetailsService authDetailsSvc;
    @Autowired
    SecurityTokenService tokenSvc;
    @Autowired
    UserService userSvc;

    // create payment link
    @PostMapping("/create-link")
    public ResponseEntity<?> createPaymentIntent(@RequestBody SubscriptionRequestDTO dto) throws StripeException {
        // System.out.println(">> PaymentController: this endpoint was reached");
        System.out.println(dto);
        // String sessionId = stripeSvc.createPaymentLink(dto);
        String sessionId = stripeSvc.createCheckoutSession(dto);
        System.out.println(">> PaymentController: SessionId created is " + sessionId);
        return ResponseEntity.ok(Json.createObjectBuilder()
                .add("sessionId", sessionId)
                .build()
                .toString());
    }

    // update BE and DB
    @PostMapping("/process")
    public ResponseEntity<?> updateSubscribeStatus(@RequestBody SubscriptionRequestDTO dto){
        System.out.println(">> PaymentController: update subscribe endpoint was reached "+ dto);
        String userId = dto.getUserId();
        String email = dto.getEmail();
        try {
            subscriptionSvc.updateSubscribeStatus(userId, email, "ROLE_SUBSCRIBER");
            //create new JWT w updated status
            UserDetails userDetails = authDetailsSvc.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            String tokenDetails = tokenSvc.generateTokenWithUpdatedRoles(authentication);
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("token", tokenDetails);
            System.out.println("Generated JWT: " + tokenDetails);
            return ResponseEntity.ok(responseMap);
        } catch (UserChangeSubscriptionStatusException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

}
