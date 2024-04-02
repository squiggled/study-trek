package vttp.proj2.backend.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import jakarta.json.JsonObject;
import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.models.CourseNote;
import vttp.proj2.backend.models.CourseSearch;
import vttp.proj2.backend.models.FriendRequest;
import vttp.proj2.backend.models.Notification;
import vttp.proj2.backend.models.Platform;
import vttp.proj2.backend.repositories.Queries;
import vttp.proj2.backend.repositories.UserRepository;

public class Utils {
    @Autowired
    static UserRepository userRepo;
    private static JdbcTemplate template;

    @Autowired
    public Utils(JdbcTemplate jdbcTemplate) {
        Utils.template = jdbcTemplate;
    }

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
        // System.out.println("course.getuserId " + course.getUserId());
        SqlRowSet cnRS = template.queryForRowSet(Queries.SQL_USER_COURSE_GET_ALL_BY_COURSEID, course.getUserId(), course.getCourseId());
        if (cnRS.next()){
            System.out.println("found a note ");
            course.setCourseNotes(mapRowToCourseNote(cnRS));
        } else {
            System.out.println("did not find note");
            course.setCourseNotes(new CourseNote());
        }
        // System.out.println("Notes for user in Utils: " + course.getCourseNotes());
        return course;
    }

    public static CourseNote mapRowToCourseNote(SqlRowSet rs){
        CourseNote note = new CourseNote();
        note.setCourseId(rs.getInt("courseId"));
        note.setNoteId(rs.getInt("noteId"));
        note.setText(rs.getString("note"));
        note.setUserId(rs.getString("userId"));
        return note;
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

    public static Notification parseNotification(JsonObject json) {
        Notification notif = new Notification();
        Integer id = json.getInt("notifId");
        notif.setNotifId(id);
        String message = json.getString("message");
        notif.setMessage(message);
        notif.setUserId(json.getString("userId"));
        notif.setRelatedId(json.getString("relatedId"));
        notif.setType(json.getString("type"));
        notif.setRead(json.getBoolean("read"));
        return notif;
    }

    public static FriendRequest parseFriendRequest(JsonObject json) {
        FriendRequest req = new FriendRequest();
        req.setReceiverId(json.getString("receiverId"));
        req.setRequestId(json.getString("requestId"));
        req.setSenderId(json.getString("senderId"));
        req.setStatus(json.getString("status"));
        return req;
    }

    public static String getUserIdFromJWT(HttpServletRequest request, JwtDecoder jwtDecoder) {
        final String TOKEN = request.getHeader(HttpHeaders.AUTHORIZATION).trim().replaceFirst("Bearer\\s+", "");
        if (!TOKEN.isEmpty()) {
            System.out.println("USER ID " + jwtDecoder.decode(TOKEN).getSubject());
            return jwtDecoder.decode(TOKEN).getSubject();
        }
        return null;
    }

   
    
    
}
