package vttp.proj2.backend.exceptions;

public class UserChangeSubscriptionStatusException extends RuntimeException {
    public UserChangeSubscriptionStatusException() {
        super("🔴 Failed to amend subscription status");
    }

    public UserChangeSubscriptionStatusException(String message) {
        super(message);
    }
    
}
