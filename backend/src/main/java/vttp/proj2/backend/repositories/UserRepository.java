package vttp.proj2.backend.repositories;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vttp.proj2.backend.exceptions.UserAddCourseException;
import vttp.proj2.backend.exceptions.UserAddFriendException;
import vttp.proj2.backend.models.AccountInfo;
import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.models.Curriculum;
import vttp.proj2.backend.models.FriendInfo;
import vttp.proj2.backend.models.FriendRequest;
import vttp.proj2.backend.models.Notification;
import vttp.proj2.backend.utils.Utils;

@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate template;

    // finding user for login
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

    //get notifs on login
    public List<Notification> getUserNotifications(String userId) {
        List<Notification> notifications = new ArrayList<>();
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_NOTIF_GET_ALL, userId);
        while (rs.next()) {
            Notification notif = new Notification();
            notif.setUserId(userId);
            notif.setNotifId(rs.getInt("notificationId"));
            notif.setType(rs.getString("type"));
            notif.setMessage(rs.getString("message")); 
            notif.setType(rs.getString("type"));  
            notif.setRelatedId(String.valueOf(rs.getInt("relatedId")));
            //look for friendreq with the id
            SqlRowSet rs1 = template.queryForRowSet(Queries.SQL_FRIENDS_FIND_REQUEST, notif.getRelatedId());
            notif.setFriendRequest(convertRsToFriendRequest(rs1));

            notif.setTimestamp(rs.getTimestamp("timestamp"));
            notif.setRead(rs.getBoolean("readStatus"));
            notifications.add(notif);
            System.out.println("notif found " + notif);
        }
        return notifications;
    }

    public FriendRequest convertRsToFriendRequest (SqlRowSet rs){
        if (rs.next()){
            FriendRequest fr = new FriendRequest();
            fr.setRequestId(Integer.toString(rs.getInt("requestId")));
            fr.setReceiverId(rs.getString("receiverId"));
            fr.setSenderId(rs.getString("senderId"));
            fr.setStatus(rs.getString("status"));
            return fr;
        }
        return null; 
    }

    // get first and last name from userid
    public String findNameByUserId(String userId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_FIND_USER_BY_USERID, userId);
        if (rs.next()) {
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            return firstName + " " + lastName;
        }
        return null;
    }

    // retrieving user details
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

    // adding course to user
    @Transactional(rollbackFor = UserAddCourseException.class)
    public CourseDetails addCourseAndInitializeProgress(String userId, CourseDetails courseDetails,
            List<Curriculum> curriculumList) throws UserAddCourseException {
        System.out.println("platform " + courseDetails.getPlatform().toString());
        template.update(Queries.SQL_USER_ADD_REGISTERED_COURSE, userId, courseDetails.getPlatform().toString(),
                courseDetails.getPlatformId(), courseDetails.getTitle(),
                courseDetails.getHeadline(), courseDetails.getImageUrl(), courseDetails.getUrlToCourse(),
                courseDetails.isPaid(), courseDetails.getPrice(), courseDetails.getInstructor(), true);

        // get newly added course's id
        Integer courseId = template.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        if (courseId == 0) {
            throw new UserAddCourseException("Cannot save registered course");
        }
        for (Curriculum curriculum : curriculumList) {
            int curriculumId = addCurriculumItem(courseId, curriculum);
            if (curriculumId == 0) {
                throw new UserAddCourseException("Cannot save curriculum for registered course");
            }
            template.update(Queries.SQL_USER_UPDATE_COURSE_PROGRESS, userId, curriculumId, false);
        }
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_USER_GET_NEW_REGISTERED_COURSE, userId);
        if (rs.next()) {
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

    // friends feature
    public FriendInfo convertRsToFriendInfo(SqlRowSet rs, String userId) {
        FriendInfo friend = new FriendInfo();
        friend.setUserId(rs.getString("userId"));
        friend.setEmail(rs.getString("email"));
        friend.setFirstName(rs.getString("firstName"));
        friend.setLastName(rs.getString("lastName"));
        friend.setProfilePicUrl(rs.getString("profilePicUrl"));
        friend.setInterests(getUserInterests(friend.getUserId()));
        friend.setRegisteredCourses(getAllRegisteredCoursesForUser(friend.getUserId()));
        boolean isFriend = isFriend(userId, friend.getUserId());
        friend.setFriend(isFriend);
        return friend;
    }

    public FriendInfo friendSearchByEmail(String friendEmail, String userId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_FIND_FRIENDS_BY_EMAIL, friendEmail);
        if (rs.next()) {
            System.out.println("found friend");
            FriendInfo friend = convertRsToFriendInfo(rs, userId);
            boolean isFriend = isFriend(userId, friend.getUserId());
            if (isFriend) {
                friend.setFriend(true);
                boolean isPendingReq = isPending(userId, friend.getUserId());
                if (isPendingReq) {
                    friend.setStatus("PENDING");
                }
                return friend;
            } else {
                friend.setFriend(false);
            }
            return friend;
        } else {
            System.out.println("did not find friend");
            return null;
        }
    }

    public FriendInfo friendSearchById(String friendId, String userId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_FIND_USER_BY_EMAIL, friendId);
        if (rs.next()) {
            System.out.println("found friend");
            FriendInfo friend = convertRsToFriendInfo(rs, userId);
            boolean isFriend = isFriend(userId, friend.getUserId());
            if (isFriend) {
                friend.setFriend(true);
                return friend;
            } else {
                friend.setFriend(false);
            }
            boolean isPendingReq = isPending(userId, friend.getUserId());
            if (isPendingReq) {
                System.out.println("is pending");
                friend.setStatus("PENDING");
            }
            return friend;
        } else {
            System.out.println("did not find friend");
            return null;
        }
    }

    // check if is already a friend
    public boolean isFriend(String userId, String friendId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_IS_ALREADY_FRIENDS, userId, friendId, userId, friendId);
        if (rs.next())
            return true;
        return false;
    }

    // check if there is already a pending request
    public boolean isPending(String userId, String friendId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_FRIENDS_IS_PENDING, userId, friendId);
        if (rs.next()){
            int count = rs.getInt(1); // Assuming COUNT(*) is the first column
            System.out.println("Pending count: " + count);
            return count > 0;
        }
        return false;
    }

    // add friendrequest for sender
    public int addFriendRequest(FriendRequest friendRequest) {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        int rowsAffected = template.update(Queries.SQL_FRIENDS_CREATE_NEW_FRIENDREQUEST,
                friendRequest.getSenderId(), friendRequest.getReceiverId(),
                friendRequest.getStatus(), currentTimestamp, null);
        if (rowsAffected == 0) {
            throw new UserAddFriendException("Cannot create new friend request");
        } else {
            int insertedId = template.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            return insertedId;
        }
    }

    // add notif for recipient
    public int addNotification(Notification notif) {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        int rowsAffected = template.update(Queries.SQL_NOTIF_ADD_NEW, notif.getUserId(), notif.getType(),
                notif.getMessage(), notif.getRelatedId(),
                notif.isRead(), currentTimestamp);
        if (rowsAffected == 0) {
            throw new UserAddFriendException("Cannot create new notification");
        } else {
            int insertedId = template.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            return insertedId;
        }
    }

    public boolean deleteNotification(Notification notif){
        int rowsAffected = template.update(Queries.SQL_NOTIF_DELETE, notif.getNotifId());
        if (rowsAffected==0) return false;
        return true;
    }

}
