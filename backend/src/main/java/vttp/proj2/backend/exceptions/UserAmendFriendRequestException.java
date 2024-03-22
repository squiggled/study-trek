package vttp.proj2.backend.exceptions;

public class UserAmendFriendRequestException extends RuntimeException{

    public UserAmendFriendRequestException() {
        super("🔴 Failed to amend friend request");
    }

    public UserAmendFriendRequestException(String message) {
        super(message);
    }
}
