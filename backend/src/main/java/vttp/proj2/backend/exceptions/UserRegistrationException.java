package vttp.proj2.backend.exceptions;

public class UserRegistrationException extends RuntimeException{
    public UserRegistrationException() {
        super("🔴 Failed to register new user");
    }

    public UserRegistrationException(String message) {
        super(message);
    }
}
