package vttp.proj2.backend.models;

import java.util.List;

public class FriendInfo {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicUrl;
    private List<String> interests;
    private List<CourseDetails> registeredCourses;
    private boolean isFriend;
    private String status; //e.g. if there is a pending request

    public FriendInfo() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<CourseDetails> getRegisteredCourses() {
        return registeredCourses;
    }

    public void setRegisteredCourses(List<CourseDetails> registeredCourses) {
        this.registeredCourses = registeredCourses;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
