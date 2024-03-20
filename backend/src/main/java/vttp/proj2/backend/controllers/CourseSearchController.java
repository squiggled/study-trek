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

import vttp.proj2.backend.models.CourseSearch;
import vttp.proj2.backend.services.CourseHomepageService;
import vttp.proj2.backend.services.CourseSearchService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class CourseSearchController {
    
    @Autowired
    private CourseSearchService courseSearchSvc;

    @Autowired
    private CourseHomepageService courseHomepageSvc;

    //for search
    @GetMapping(path="/courses/search")
    public ResponseEntity<?> search(@RequestParam String query, @RequestParam(required=false) String page, @RequestParam(required=false) String platform, 
                    @RequestParam(required=false) boolean byRating){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("query", query);
        if (page != null) {
            paramMap.put("page", page);
        }
        if (platform!=null){
            paramMap.put("platform", platform);
        }
        if (byRating){
            paramMap.put("byRating", "byRating");
        }
        List<CourseSearch> foundCourses = courseSearchSvc.courseSearch(paramMap);
        if (null==foundCourses){
            return ResponseEntity.badRequest().body("Error - Bad Request");
        }
        return ResponseEntity.ok(foundCourses);
    }

    @GetMapping(path="/course")
    public ResponseEntity<?> getCourseById(@RequestParam String courseId, @RequestParam String platform){
        System.out.println("course id" + courseId);
        Object courseFound = null;
        switch (platform.toUpperCase()) {
            case "UDEMY":
                courseFound = courseSearchSvc.getUdemyCourseById(courseId);
                break;
            case "EDX":
                break;
            case "COURSERA":
                courseFound = courseSearchSvc.getCourseraCourseById(courseId);
                break;
        }
        if (courseFound != null) {
            return ResponseEntity.ok(courseFound);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //for home page
    @GetMapping(path="/courses/loadhomepage")
    public ResponseEntity<?> getHomepageCourses(){
        List<CourseSearch> homepageCourses = courseHomepageSvc.loadCoursesForHomepage();
        return ResponseEntity.ok(homepageCourses);
    }
}
