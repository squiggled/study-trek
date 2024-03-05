package vttp.proj2.backend.models;

import java.util.List;

public class CourseDetails {
    private Platform platform;
    private Integer id;
    private String title;
    private String headline;
    private String imageUrl;
    private String urlToCourse;
    private boolean isPaid; 
    private String price;
    private String instructor;
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
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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

    
}
