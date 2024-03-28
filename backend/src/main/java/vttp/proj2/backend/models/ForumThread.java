package vttp.proj2.backend.models;

import java.security.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ForumThread {
    @Id
    private String id;
    private String userId;
    private String firstName;
    private String userProfilePic;
    private Date createdDate;
    private String title;
    private String content;
    private List<ThreadMessage> messages;

    
}
