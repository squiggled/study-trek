package vttp.proj2.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.models.CourseSearch;
import vttp.proj2.backend.services.CourseraService;
import vttp.proj2.backend.services.UdemyService;
import vttp.proj2.backend.services.edXService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class CourseSearchController {
    
    @Autowired
    private edXService edXSvc;

    @Autowired
    private CourseraService courseraSvc;

    @Autowired
    private UdemyService udemySvc;

    @GetMapping(path="/courses/search")
    public ResponseEntity<?> search(@RequestParam String query, @RequestParam(required=false) String page){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("query", query);
        if (page != null) {
            paramMap.put("page", page);
        }
        List<CourseSearch> foundCourses = udemySvc.courseSearch(paramMap);
        if (null==foundCourses){
            return ResponseEntity.badRequest().body("Error - Bad Request");
        }
        return ResponseEntity.ok(foundCourses);
    }

    @GetMapping(path="/course")
    public ResponseEntity<?> getCourseById(@RequestParam String courseId, @RequestParam String platform){
        Object courseFound = null;
        switch (platform.toUpperCase()) {
            case "UDEMY":
                courseFound = udemySvc.getCourseById(courseId);
                break;
            case "EDX":
                break;
            case "COURSERA":
                break;
        }
        if (courseFound != null) {
            return ResponseEntity.ok(courseFound);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
