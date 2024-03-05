package vttp.proj2.backend.models;

public class Curriculum {
    private Integer lectureNumber;
    private String title;
    private boolean completed;
    public Integer getLectureNumber() {
        return lectureNumber;
    }
    public void setLectureNumber(Integer lectureNumber) {
        this.lectureNumber = lectureNumber;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public Curriculum() {
    }

    
}
