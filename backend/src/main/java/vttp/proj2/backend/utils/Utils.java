package vttp.proj2.backend.utils;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import vttp.proj2.backend.models.CourseDetails;
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
}
