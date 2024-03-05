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
    private Float price;
    private String instructor;
    private List<Curriculum> curriculum;
}
