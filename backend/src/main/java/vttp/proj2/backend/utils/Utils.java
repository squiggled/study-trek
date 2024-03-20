package vttp.proj2.backend.utils;

import java.util.Optional;

import org.bson.Document;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.models.CourseSearch;
import vttp.proj2.backend.models.Platform;

public class Utils {
     public static Platform stringToPlatform(String platformStr) {
        for (Platform platform : Platform.values()) {
            if (platform.name().equalsIgnoreCase(platformStr)) {
                return platform;
            }
        }
        return Platform.OTHER; 
    }
    public static CourseDetails mapRowToCourseDetails(SqlRowSet rs) {
        CourseDetails course = new CourseDetails();
        course.setCourseId(rs.getInt("courseId"));
        course.setUserId(rs.getString("userId"));
        course.setPlatformId(rs.getString("platformId"));
        course.setPlatform(Utils.stringToPlatform(rs.getString("platform")));
        course.setTitle(rs.getString("title"));
        course.setHeadline(rs.getString("headline"));
        course.setImageUrl(rs.getString("imageUrl"));
        course.setUrlToCourse(rs.getString("urlToCourse"));
        course.setPaid(rs.getBoolean("isPaid"));
        course.setPrice(rs.getString("price"));
        course.setInstructor(rs.getString("instructor"));
        course.setEnrolled(rs.getBoolean("isEnrolled"));
        return course;
    }

    public static CourseSearch documentToCourseSearch(Document document) {
        if (document==null) return null;
        CourseSearch courseSearch = new CourseSearch();
        courseSearch.setPlatform(Platform.valueOf(document.getString("platform").toUpperCase()));
        Object platformIdObj = document.get("platformId");
        String platformIdStr = Optional.ofNullable(platformIdObj)
                                       .map(Object::toString)
                                       .orElse(null);
        courseSearch.setPlatformId(platformIdStr);

        courseSearch.setTitle(document.getString("title"));
        courseSearch.setHeadline(document.getString("headline"));
        courseSearch.setImageUrl(document.getString("imageUrl"));
        courseSearch.setPrice(document.getString("price"));
        courseSearch.setInstructor(document.getString("instructor"));

        return courseSearch;
    }

    
}
