package vttp.proj2.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDTO {

    private String successUrl;
    private String cancelUrl;
    private String userId;
    private String email;
    
}