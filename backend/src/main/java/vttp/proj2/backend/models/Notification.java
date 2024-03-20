package vttp.proj2.backend.models;

import java.sql.Date;

public class Notification {
    private String id;
    private String type; // e.g., "FRIEND_REQUEST"
    private String message; // e.g., "John Doe sent you a friend request"
    private String relatedId; // e.g., ID of the friend request
    private boolean read;
    private Date timestamp;
    private FriendRequest friendRequest;
    
}
