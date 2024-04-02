package vttp.proj2.backend.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp.proj2.backend.models.CourseNote;
import vttp.proj2.backend.utils.Utils;

@Repository
public class CourseProgressRepository {
    @Autowired
    private JdbcTemplate template;

    //add note
    public CourseNote addNote(CourseNote note){
        System.out.println("courseid " + note.getCourseId() );
        int rowsAffected = template.update(Queries.SQL_USER_COURSE_ADD_NOTE, note.getCourseId(), note.getUserId(), note.getText());
        if (rowsAffected>0){
            CourseNote updatedNote = getLatestNoteForUserAndCourse(note.getUserId(), note.getCourseId());
            return updatedNote;
        }
        return null;
    }

     public CourseNote getLatestNoteForUserAndCourse(String userId, int courseId) {
        SqlRowSet rs =  template.queryForRowSet(Queries.SQL_USER_COURSE_GET_LATEST, userId, courseId);
        if (rs.next()){
            return Utils.mapRowToCourseNote(rs);
        }
        return null;
    }

    //update note
    public CourseNote updateNote(CourseNote note){
        int rowsAffected = template.update(Queries.SQL_USER_COURSE_UPDATE_NOTE, note.getText(), note.getNoteId());
        if (rowsAffected==0) return null;
        return note;
    }

    //get latest note
    public CourseNote getLatest(String courseId){
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_USER_COURSE_GET_LATEST_NOTE, courseId);
        if (rs.next()){
            return Utils.mapRowToCourseNote(rs);
        }
        return null;
    }

    
}
