package vttp.proj2.backend.exceptions;

public class UserAddFriendException extends RuntimeException{
    public UserAddFriendException() {
        super("🔴 Failed to add friend");
    }

    public UserAddFriendException(String message) {
        super(message);
    }
}
