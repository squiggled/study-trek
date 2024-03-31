package vttp.proj2.backend.exceptions;

public class UserWrongPasswordException extends RuntimeException {
    public UserWrongPasswordException() {
        super("🔴 Current password does not match");
    }

    public UserWrongPasswordException(String message) {
        super(message);
    }
    
}
