package vttp.proj2.backend.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AccountInfo {
    private String userId;

    private String email;
    private String passwordHash;
    private Date lastPasswordResetDate;

    private String firstName;
    private String lastName;
    private String profilePicUrl;
    
    private List<String> interests;
    private List<String> courseNotes;
    private List<CourseDetails> registeredCourses;
    private List<String> friendIds = new ArrayList<>();
    
    private List<String> roles;

    public AccountInfo() {
        UUID uuid = UUID.randomUUID();
        this.userId = uuid.toString().substring(0, 8);       
    }

    public String getUserId() {
        return userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<String> getCourseNotes() {
        return courseNotes;
    }

    public void setCourseNotes(List<String> courseNotes) {
        this.courseNotes = courseNotes;
    }

    public List<CourseDetails> getRegisteredCourses() {
        return registeredCourses;
    }

    public void setRegisteredCourses(List<CourseDetails> registeredCourses) {
        this.registeredCourses = registeredCourses;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public List<String> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(List<String> friendIds) {
        this.friendIds = friendIds;
    }

}
