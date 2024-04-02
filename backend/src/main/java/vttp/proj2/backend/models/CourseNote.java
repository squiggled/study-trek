package vttp.proj2.backend.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseNote {
    private int noteId;
    private int courseId; //matches auto inc pk for courses table
    private String userId;
    private String text;
    
}
