package vttp.proj2.backend.models;

import java.sql.Timestamp;

public class Notification {
    private int notifId; //PK
    private String userId; //for the user
    private String type; // e.g., "FRIEND_REQUEST"
    private String message; // e.g., "John Doe sent you a friend request"
    private String relatedId; // e.g., ID of the friend request
    private boolean read;
    private Timestamp timestamp;
    private FriendRequest friendRequest;
    
    public Notification() {
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getRelatedId() {
        return relatedId;
    }
    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }
    public boolean isRead() {
        return read;
    }
    public void setRead(boolean read) {
        this.read = read;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public FriendRequest getFriendRequest() {
        return friendRequest;
    }
    public void setFriendRequest(FriendRequest friendRequest) {
        this.friendRequest = friendRequest;
    }
    public int getNotifId() {
        return notifId;
    }
    public void setNotifId(int notifId) {
        this.notifId = notifId;
    }
    
}
