package vttp.proj2.backend.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vttp.proj2.backend.exceptions.UserChangeSubscriptionStatusException;
import vttp.proj2.backend.repositories.SubscriptionRepository;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepo;


    @Transactional(rollbackFor = UserChangeSubscriptionStatusException.class)
    public boolean updateSubscribeStatus(String userId, String email, String role) throws UserChangeSubscriptionStatusException{
        System.out.println(">> subscription service: this endpoint was reached");
        boolean isStatusChanged = subscriptionRepo.updateSubscriptionStatus(userId, "ROLE_SUBSCRIBER");
        if (!isStatusChanged){
            throw new UserChangeSubscriptionStatusException("Failed to update subscription status");
        }

        boolean isSubscriptionAdded = subscriptionRepo.addPremiumSubscription(userId);
        if (!isSubscriptionAdded){
            throw new UserChangeSubscriptionStatusException("Failed to insert to subscriptions");
        }

        return true;
    }

    public List<Map<String, Object>> getActivePremiumUserDetails() {
        return subscriptionRepo.findActivePremiumUserDetails();
    }
    
}
