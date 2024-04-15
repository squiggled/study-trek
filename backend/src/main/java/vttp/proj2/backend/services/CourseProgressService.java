package vttp.proj2.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.proj2.backend.models.CourseNote;
import vttp.proj2.backend.models.Curriculum;
import vttp.proj2.backend.models.UserProgress;
import vttp.proj2.backend.repositories.CourseProgressRepository;

@Service
public class CourseProgressService {

    @Autowired
    CourseProgressRepository courseProgressRepo;

    public CourseNote addNote(CourseNote note){
        CourseNote newNote = courseProgressRepo.addNote(note);
        System.out.println("New note in service " + newNote);
        if (null==newNote) return null;
        return newNote;
    }

    public CourseNote updateNote(CourseNote note){
        CourseNote updatedNote = courseProgressRepo.updateNote(note);
        System.out.println("Updated note in service " + updatedNote);
        if (updatedNote==null) return null;
        return updatedNote;
    }

    public CourseNote getLatest(String courseId){
        System.out.println("CourseProgressService: Endpoint reached, retrieving latest note");
        CourseNote lastNote = courseProgressRepo.getLatest(courseId);
        if (lastNote==null) return null;
        return lastNote;
    }

    //user progress
    public List<UserProgress> getCurriculumItemsForUser(String courseId, String userId) {
        return courseProgressRepo.findByCourseIdAndUserId(courseId, userId);
    }

    public UserProgress toggleCompletionStatus(String userId, Integer curriculumId) {
        return courseProgressRepo.toggleCompletion(userId, curriculumId);
    }
    
}
