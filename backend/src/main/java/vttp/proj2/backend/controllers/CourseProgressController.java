package vttp.proj2.backend.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vttp.proj2.backend.models.CourseNote;
import vttp.proj2.backend.services.CourseProgressService;

@RestController
@RequestMapping("/api")
public class CourseProgressController {

    @Autowired
    CourseProgressService courseProgressSvc;

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
        CourseNote latestNote = courseProgressSvc.getLatest(courseId);
        if (latestNote != null) {
            return ResponseEntity.ok(latestNote);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
