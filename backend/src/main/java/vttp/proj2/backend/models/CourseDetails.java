package vttp.proj2.backend.models;

import java.util.List;

public class CourseDetails {
    private Integer courseId; // sql primary key. null if user is not logged in
    private String userId; // for logged in users; for querying all courses for a user
    private Platform platform;
    private String platformId;
    private String title;
    private String headline;
    private String imageUrl;
    private String urlToCourse;
    private boolean isPaid;
    private String price;
    private String instructor;
    private CourseNote courseNotes;
    private List<Curriculum> curriculum;
    private boolean isEnrolled = false;

    public CourseDetails() {
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrlToCourse() {
        return urlToCourse;
    }

    public void setUrlToCourse(String urlToCourse) {
        this.urlToCourse = urlToCourse;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public List<Curriculum> getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(List<Curriculum> curriculum) {
        this.curriculum = curriculum;
    }

    public boolean isEnrolled() {
        return isEnrolled;
    }

    public void setEnrolled(boolean isEnrolled) {
        this.isEnrolled = isEnrolled;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CourseNote getCourseNotes() {
        return courseNotes;
    }

    public void setCourseNotes(CourseNote courseNotes) {
        this.courseNotes = courseNotes;
    }

    // @Override
    // public String toString() {
    //     return "CourseDetails{" +
    //             "courseId=" + courseId +
    //             ", title='" + title + '\'' +
    //             ", platformId='" + platformId + '\'' +
    //             '}';
    // }
}
