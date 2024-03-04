package vttp.proj2.backend.models;

import java.util.List;

public class SearchResult {
    private List<CourseSearch> foundCourses; 
    private String prevPageUrl;
    private String nextPageUrl;
    
    public SearchResult() {
    }
    public List<CourseSearch> getFoundCourses() {
        return foundCourses;
    }
    public void setFoundCourses(List<CourseSearch> foundCourses) {
        this.foundCourses = foundCourses;
    }
    public String getPrevPageUrl() {
        return prevPageUrl;
    }
    public void setPrevPageUrl(String prevPageUrl) {
        this.prevPageUrl = prevPageUrl;
    }
    public String getNextPageUrl() {
        return nextPageUrl;
    }
    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    
}
