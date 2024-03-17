package vttp.proj2.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vttp.proj2.backend.exceptions.UserAddCourseException;
import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.services.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserCourseController {

    @Autowired
    UserService userSvc;

    @PostMapping("/{userId}/courses")
    public ResponseEntity<?> addCourse(@PathVariable String userId, @RequestBody CourseDetails courseDetails) {
        try {
            CourseDetails addedCourseDetails = userSvc.addCourseAndInitializeProgress(userId, courseDetails,
                    courseDetails.getCurriculum());
                    System.out.println("course details added " + courseDetails);
            return ResponseEntity.ok(addedCourseDetails);
        } catch (UserAddCourseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
