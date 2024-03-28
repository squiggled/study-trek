package vttp.proj2.backend.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThreadMessage {

    private String userId;
    private String firstName;
    private String userProfilePic;
    private Date postedDate;
    private String content;

}
