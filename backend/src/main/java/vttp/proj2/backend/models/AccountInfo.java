package vttp.proj2.backend.models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class AccountInfo {
    private String userId;
    private String username;
    private String passwordHash;
    private Date lastPasswordResetDate;

    private List<String> roles;
   
    private String firstName;
    private String lastName;
    private String email;
    private List<String> interests;
    private List<String> courseNotes;
    private List<CourseDetails> courseList;
    private String profilePicUrl;

    
}
