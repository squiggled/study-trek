package vttp.proj2.backend.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseNote {
    private int noteId;
    private String courseId;
    private String userId;
    private String text;
    
}
