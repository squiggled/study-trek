package vttp.proj2.backend.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;

import jakarta.annotation.PostConstruct;
import vttp.proj2.backend.dtos.SubscriptionRequestDTO;
import vttp.proj2.backend.repositories.SubscriptionRepository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.secret.test}")
    private String stripeSecretTest;

    @Value("${stripe.subscription.url}")
    private String stripeURL;

    @PostConstruct
    public void init() {
        // Stripe.apiKey = this.stripeSecretKey;
        Stripe.apiKey = this.stripeSecretTest;
    }

    @Autowired
    SubscriptionRepository stripeRepo;

    // public String createPaymentLink(SubscriptionRequestDTO subscriptionRequestDTO) {

    //     System.out.println(">> StripeService: successUrl: " + stripeURL + subscriptionRequestDTO.getSuccessUrl());

    //     try {
    //         CustomerCreateParams customerParams = CustomerCreateParams.builder()
    //             .setEmail(subscriptionRequestDTO.getEmail()) 
    //             .setMetadata(Map.of("userId", subscriptionRequestDTO.getUserId()))
    //             .build();

    //         Customer customer = Customer.create(customerParams);

    //         // Create subscription
    //         SubscriptionCreateParams subscriptionParams = SubscriptionCreateParams.builder()
    //             .setCustomer(customer.getId())
    //             .addItem(SubscriptionCreateParams.Item.builder()
    //                 .setPrice("price_1OyGWhRovknRUrZ7YpWqQpuK")
    //                 .build())
    //             .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.ERROR_IF_INCOMPLETE)
    //             .build();

    //         Subscription subscription = Subscription.create(subscriptionParams);
    //         System.out.println(">> StripeService: Created subscription with ID " + subscription.getId());
    //         return subscription.getId();

    //     } catch (StripeException ex) {
    //         System.out.println(ex);
    //     }

    //     return "";
    // }
    public String createCheckoutSession(SubscriptionRequestDTO dto) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                //live id: price_1OyGWhRovknRUrZ7YpWqQpuK 
                    .setPrice("price_1OypdkRovknRUrZ7r04LzNL6") 
                    .setQuantity(1L)
                    .build())
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .setSuccessUrl(dto.getSuccessUrl())
            .setCancelUrl(dto.getCancelUrl())
            .setCustomerEmail(dto.getEmail()) 
            .build();
    
        Session session = Session.create(params);
        return session.getId();
    }


}

