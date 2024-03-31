package vttp.proj2.backend.exceptions;

public class UserAmendDetailsException extends RuntimeException{
    public UserAmendDetailsException() {
        super("🔴 Failed to update user details");
    }

    public UserAmendDetailsException(String message) {
        super(message);
    }
    
}
