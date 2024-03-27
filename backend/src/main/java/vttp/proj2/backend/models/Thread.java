package vttp.proj2.backend.models;

import java.security.Timestamp;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Thread {

    private String userId;
    private String firstName;
    private Date createdDate;
    private List<ThreadMessage> messages;

    
}
