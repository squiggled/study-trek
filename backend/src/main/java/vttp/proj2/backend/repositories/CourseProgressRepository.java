package vttp.proj2.backend.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp.proj2.backend.models.CourseNote;
import vttp.proj2.backend.models.Curriculum;
import vttp.proj2.backend.models.UserProgress;
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

    //get all curr progress for a user and a course
    public List<UserProgress> findByCourseIdAndUserId(String courseId, String userId) {
        List<UserProgress> progresses = new ArrayList<>();
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_USER_CURR_GET_ALL_CURR_PROGRESS, courseId, userId);
        while (rs.next()){
            UserProgress progress = new UserProgress();
            progress.setProgressId(rs.getInt("progressId"));
            progress.setUserId(rs.getString("userId"));
            progress.setCurriculumId(rs.getInt("curriculumId"));
            progress.setCompleted(rs.getBoolean("completed"));
            progresses.add(progress);
        }
        return progresses;
    }

    public UserProgress toggleCompletion(String userId, Integer curriculumId) {
        //toggle completion
        String sqlUpdate = "UPDATE user_progress SET completed = NOT completed WHERE userId = ? AND curriculumId = ?";
        int updated = template.update(sqlUpdate, userId, curriculumId);
        if (updated == 0) {
            return null; // No rows updated, possibly no such entry
        }

        // Fetch the updated progress
        UserProgress updatedProg = new UserProgress();
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_USER_CURR_GET_PROGRESS, userId, curriculumId);
        if (rs.next()){
            updatedProg.setCompleted(rs.getBoolean("completed"));
            updatedProg.setCurriculumId(rs.getInt("curriculumId"));
            updatedProg.setProgressId(rs.getInt("progressId"));
            updatedProg.setUserId(rs.getString("userId"));
        } else {
            return null;
        }
        return updatedProg;
      
    }

    
}
