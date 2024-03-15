package vttp.proj2.backend.models;

public class UserProgress {
    private Integer progressId;
    private String userId; //FK of accInfo's PK
    private Integer curriculumId; //FK of curriculum's PK
    private Boolean completed;
    
    public UserProgress() {
    }

    public Integer getProgressId() {
        return progressId;
    }
    public void setProgressId(Integer progressId) {
        this.progressId = progressId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Integer getCurriculumId() {
        return curriculumId;
    }
    public void setCurriculumId(Integer curriculumId) {
        this.curriculumId = curriculumId;
    }
    public Boolean getCompleted() {
        return completed;
    }
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    
}
