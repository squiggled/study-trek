package vttp.proj2.backend.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vttp.proj2.backend.exceptions.UserAddCourseException;
import vttp.proj2.backend.models.AccountInfo;
import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.models.Curriculum;
import vttp.proj2.backend.utils.Utils;

@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate template;

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
        while (rs.next()) {
            CourseDetails newCourse = Utils.mapRowToCourseDetails(rs);
            userCourses.add(newCourse);
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

    public List<String> getAllFriends(String userId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_FRIENDS_FOR_USER, userId, userId);
        List<String> friendIds = new ArrayList<>();
        while (rs.next()) {
            friendIds.add(rs.getString("friendId"));
        }
        return friendIds;
    }

    public String getFirstNameByEmail(String email) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_FIRSTNAME_BY_EMAIL, email);
        if (rs.next())
            return rs.getString("firstName");
        return null;
    }

    @Transactional(rollbackFor = UserAddCourseException.class)
    public CourseDetails addCourseAndInitializeProgress(String userId, CourseDetails courseDetails,
            List<Curriculum> curriculumList) throws UserAddCourseException{
                System.out.println("platform " + courseDetails.getPlatform().toString());
        template.update(Queries.SQL_USER_ADD_REGISTERED_COURSE, userId, courseDetails.getPlatform().toString(),
                courseDetails.getPlatformId(), courseDetails.getTitle(),
                courseDetails.getHeadline(), courseDetails.getImageUrl(), courseDetails.getUrlToCourse(),
                courseDetails.isPaid(), courseDetails.getPrice(), courseDetails.getInstructor(), true);

        // get newly added course's id
        Integer courseId = template.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        if (courseId==0){
            throw new UserAddCourseException("Cannot save registered course");
        }
        for (Curriculum curriculum : curriculumList) {
            int curriculumId = addCurriculumItem(courseId, curriculum);
            if (curriculumId==0){
                throw new UserAddCourseException("Cannot save curriculum for registered course");
            }
            template.update(Queries.SQL_USER_UPDATE_COURSE_PROGRESS, userId, curriculumId, false);
        }
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_USER_GET_NEW_REGISTERED_COURSE, userId);
        if (rs.next()){
            CourseDetails newCourse = Utils.mapRowToCourseDetails(rs);
            return newCourse;
        } else {
            return null;
        }
    }

    private int addCurriculumItem(int courseId, Curriculum curriculum) {
        template.update(Queries.SQL_USER_ADD_CURRICULUM_FOR_COURSE, courseId, curriculum.getLectureNumber(),
                curriculum.getTitle());
        return template.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }
}
