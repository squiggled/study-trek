package vttp.proj2.backend.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp.proj2.backend.models.AccountInfo;
import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.models.Curriculum;
import vttp.proj2.backend.utils.Utils;

@Repository
public class AuthRepository {

    @Autowired
    private JdbcTemplate template;

    public boolean createNewUser(AccountInfo userInfo) {
        return template.update(Queries.SQL_CREATE_NEW_USER, userInfo.getUserId(), userInfo.getEmail(),
                userInfo.getPasswordHash(), userInfo.getLastPasswordResetDate(), userInfo.getFirstName(),
                userInfo.getLastName(), userInfo.getProfilePicUrl()) > 0;
    }

    public boolean assignUserRole(String userId, String roleName) {
        return template.update(Queries.SQL_ASSIGN_ROLE_USER, userId, roleName) > 0;
    }

    public boolean checkEmailExists(String email) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_FIND_USER_BY_EMAIL, email);
        if (rs.next())
            return true;
        return false;
    }

    public String getFirstNameByEmail(String email) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_FIRSTNAME_BY_EMAIL, email);
        if (rs.next())
            return rs.getString("firstName");
        return null;
    }

    public String getHashedPassword(String email) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_HASHED_PASSWORD_BY_EMAIL, email);
        if (rs.next())
            return rs.getString("passwordHash");
        return null;
    }

    public AccountInfo findUserByEmail(String email) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_USER_BY_EMAIL, email);
        if (rs.next()) {
            AccountInfo acc = new AccountInfo();
            String userRole = getUserRole(rs.getString("userId"));
            if (null == userRole) {
                return null;
            }
            acc.setUserId(rs.getString("userId"));
            acc.setEmail(rs.getString("email"));
            acc.setPasswordHash(rs.getString("passwordHash"));
            acc.setRole(userRole);
            acc.setLastPasswordResetDate(rs.getDate("lastPasswordResetDate"));
            acc.setFirstName(rs.getString("firstName"));
            acc.setLastName(rs.getString("lastName"));
            acc.setProfilePicUrl(rs.getString("profilePicUrl"));
            acc.setFriendIds(getAllFriends(acc.getUserId()));
            acc.setRegisteredCourses(getAllRegisteredCoursesForUser(acc.getUserId()));
            acc.setInterests(getUserInterests(acc.getUserId()));
            return acc;
        } else {
            return null;
        }
    }

    public String getUserRole(String userId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_USER_ROLE_BY_ID, userId);
        if (rs.next()) {
            return rs.getString("role");
        }
        return null;
    }

    public List<String> getUserInterests(String userId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_USER_INTERESTS, userId);
        List<String> interests = new ArrayList<>();
        while (rs.next()) {
            interests.add(rs.getString("interest"));
        }
        return interests;
    }

    public List<String> getUserCourseNotes(String userId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_USER_COURSE_NOTES, userId);
        List<String> courseNotes = new ArrayList<>();
        while (rs.next()) {
            courseNotes.add(rs.getString("note"));
        }
        return courseNotes;
    }

    public List<CourseDetails> getAllRegisteredCoursesForUser(String userId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_USER_REGISTERED_COURSES, userId);
        List<CourseDetails> userCourses = new ArrayList<>();
        while (rs.next()){
            CourseDetails course = new CourseDetails();
            course.setCourseId(rs.getInt("courseId"));
            course.setUserId(rs.getString("userId"));
            course.setPlatformId(rs.getString("platformId"));
            course.setPlatform(Utils.stringToPlatform(rs.getString("platform")));
            course.setPlatformId(rs.getString("platformId"));
            course.setTitle(rs.getString("title"));
            course.setHeadline(rs.getString("headline"));
            course.setImageUrl(rs.getString("imageUrl"));
            course.setUrlToCourse(rs.getString("urlToCourse"));
            course.setPaid(rs.getBoolean("isPaid"));
            course.setPrice(rs.getString("price"));
            course.setInstructor(rs.getString("instructor"));
            course.setEnrolled(rs.getBoolean("isEnrolled"));
            userCourses.add(course);
        }
        return userCourses;
    }

    public List<Curriculum> getCurriculumForCourse(String courseId, Integer curriculumId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_CURRICULUM_FOR_COURSE, courseId, curriculumId);
        List<Curriculum> curriculumList = new ArrayList<>();
        while (rs.next()) {
            Curriculum curriculum = new Curriculum();
            curriculum.setCurriculumId(rs.getInt("curriculumId"));
            curriculum.setCourseId(rs.getInt("courseId"));
            curriculum.setLectureNumber(rs.getInt("lectureNumber"));
            curriculum.setTitle(rs.getString("title"));
            curriculumList.add(curriculum);
        }
        return curriculumList;
    }

    public List<String> getAllFriends(String userId){
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_FRIENDS_FOR_USER, userId, userId);
        List<String> friendIds = new ArrayList<>();
        while (rs.next()){
            friendIds.add(rs.getString("friendId"));
        }
        return friendIds;
    }

}
