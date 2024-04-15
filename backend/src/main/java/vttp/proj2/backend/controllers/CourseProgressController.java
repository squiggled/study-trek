package vttp.proj2.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vttp.proj2.backend.models.CourseNote;
import vttp.proj2.backend.models.UserProgress;
import vttp.proj2.backend.services.CourseProgressService;

@RestController
@RequestMapping("/api")
public class CourseProgressController {

    @Autowired
    CourseProgressService courseProgressSvc;

    //course notes
    @PostMapping("/courses/{courseId}/notes")
    public ResponseEntity<?> addNote(@PathVariable String courseId, @RequestBody CourseNote courseNote) {
        System.out.println("Course Prog Controller: got to this end point " + courseNote);
        int noteId = courseNote.getNoteId();
        if (noteId == 0) {
            // new note, create note
            CourseNote newNote = courseProgressSvc.addNote(courseNote);
            if (null == newNote) {
                return ResponseEntity.badRequest().build();
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("note", newNote);
            System.out.println(newNote);
            return ResponseEntity.ok(resp);
        } else {
            // existing note, update note
            CourseNote updatedNote = courseProgressSvc.updateNote(courseNote);
            if (null == updatedNote) {
                return ResponseEntity.badRequest().build();
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("note", updatedNote);
            return ResponseEntity.ok(resp);
        }
    }

    @GetMapping("/courses/{courseId}/notes/latest")
    public ResponseEntity<CourseNote> getLatestNoteForCourse(@PathVariable String courseId) {
        System.out.println("CourseProgressController: Retrieving latest note for courseId " + courseId );
        CourseNote latestNote = courseProgressSvc.getLatest(courseId);
        if (latestNote != null) {
            return ResponseEntity.ok(latestNote);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //curr item
    @GetMapping("/courses/{courseId}/curriculum/{userId}")
    public ResponseEntity<?> getCurriculumByCourse(@PathVariable String courseId, @PathVariable String userId) {
        System.out.println("CourseProgressController: getCurriculumByCourse endpoint was reached");
        try {
            List<UserProgress> progressList = courseProgressSvc.getCurriculumItemsForUser(courseId, userId);
            if (progressList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(progressList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user progress");
        }
    }
    @PutMapping("/courses/toggle/{userId}/{curriculumId}")
    public ResponseEntity<?> toggleCompletion(@PathVariable String userId, @PathVariable Integer curriculumId) {
        System.out.println("CourseProgressController: toggleCompletion endpoint was reached");
        try {
            UserProgress updatedProgress = courseProgressSvc.toggleCompletionStatus(userId, curriculumId);
            if (updatedProgress == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedProgress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error toggling completion status");
        }
    }

}
