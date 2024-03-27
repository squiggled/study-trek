package vttp.proj2.backend.repositories;

public class Queries {

    //finding a user
    public static final String SQL_FIND_USER_BY_EMAIL = """
                SELECT 1
                FROM user_info
                WHERE email = ?
            """;
    public static final String SQL_GET_USER_BY_EMAIL = """
                SELECT *
                FROM user_info
                WHERE email = ?
            """;
    public static final String SQL_GET_USER_ROLE_BY_ID = """
                SELECT role
                FROM roles
                WHERE userId = ?
            """;
    public static final String SQL_FIND_USER_BY_USERID = """
                SELECT *
                FROM user_info
                WHERE userId = ?
            """;

    //auth - retrieving password
    public static final String SQL_GET_HASHED_PASSWORD_BY_EMAIL = """
                SELECT passwordHash
                FROM user_info
                WHERE email = ?
            """;

    //auth - registering new user
    public static final String SQL_CREATE_NEW_USER = """
                INSERT into user_info(userId, email, passwordHash, lastPasswordResetDate, firstName, lastName, profilePicUrl)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    public static final String SQL_ASSIGN_ROLE_USER = """
                INSERT INTO roles(userId, role) VALUES (?, ?);
            """;

    //auth - login, retrieving user details
    public static final String SQL_GET_USER_INTERESTS = """
                SELECT * 
                FROM interests
                WHERE userId = ?
            """;
    public static final String SQL_GET_USER_COURSE_NOTES = """
                SELECT * 
                FROM course_notes
                WHERE userId = ?
            """;
    public static final String SQL_GET_USER_REGISTERED_COURSES = """
                SELECT * 
                FROM registered_courses 
                WHERE userId = ?
            """;
    public static final String SQL_GET_CURRICULUM_FOR_COURSE = """
                SELECT * 
                FROM curriculum 
                WHERE courseId = ? 
                AND curriculumId = ?
            """;
    public static final String SQL_GET_FRIENDS_FOR_USER = """
                SELECT friendUserId as friendId
                FROM friends
                WHERE userId = ?
                UNION
                SELECT userId as friendId
                FROM friends
                WHERE friendUserId = ?
            """;
            
    //user - updating user progress + user adding new courses
    public static final String SQL_USER_ADD_REGISTERED_COURSE = """
                INSERT INTO registered_courses(userId, platform, platformId, title, headline, imageUrl, urlToCourse, isPaid, price, instructor, isEnrolled) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
    public static final String SQL_USER_ADD_CURRICULUM_FOR_COURSE = """
                INSERT INTO curriculum (courseId, lectureNumber, title) 
                VALUES (?, ?, ?)
            """;
    public static final String SQL_USER_UPDATE_COURSE_PROGRESS = """
                INSERT INTO user_progress (userId, curriculumId, completed) 
                VALUES (?, ?, ?)
            """;
    public static final String SQL_USER_GET_NEW_REGISTERED_COURSE = """
                SELECT * FROM registered_courses 
                WHERE courseId = ?
            """;
    
    //finding friends
    public static final String SQL_FIND_FRIENDS_BY_EMAIL = """
                SELECT *
                FROM user_info
                WHERE email = ?
            """;
    
    public static final String SQL_IS_ALREADY_FRIENDS = """
                SELECT EXISTS (
                    SELECT 1 FROM friends 
                    WHERE 
                    (userId = ? AND friendUserId = ?) 
                    OR 
                    (userId = ? AND friendUserId = ?)
                ) AS IsFriend;
            """;
    
    //add friendrequest
    public static final String SQL_FRIENDS_CREATE_NEW_FRIENDREQUEST = """
                INSERT into friend_requests(senderId, receiverId, status, sentTimeStamp, responseTimeStamp)
                VALUES (?, ?, ?, ?, ?)
            """;                
    //amend status of friend request
    public static final String SQL_FRIENDS_UPDATE_FRIEND_REQUEST = """
                UPDATE friend_requests
                SET status = ?, responseTimeStamp = CURRENT_TIMESTAMP
                WHERE requestId = ?;
            """;
    public static final String SQL_FRIENDS_IS_PENDING = """
                SELECT COUNT(*) 
                FROM friend_requests
                WHERE senderId = ? AND receiverId = ? AND status = 'PENDING';
            """;
    public static final String SQL_FRIENDS_FIND_REQUEST = """
                SELECT * 
                FROM friend_requests
                WHERE requestId = ?
            """;
    //add friends to friend table
    public static final String SQL_FRIENDS_ADD_TO_FRIEND_LIST = """
                INSERT into friends(userId, friendUserId)
                VALUES (?, ?)
            """;      
    //notifications
    public static final String SQL_NOTIF_ADD_NEW ="""
                INSERT into notifications(userId, type, message, relatedId, readStatus, timestamp)
                VALUES (?, ?, ?, ?, ?, ?)
            """;
    public static final String SQL_NOTIF_DELETE ="""
                DELETE from notifications
                WHERE notificationId = ?
            """;
    public static final String SQL_NOTIF_GET_ALL ="""
                SELECT * 
                FROM notifications 
                WHERE userId = ? 
                ORDER BY timestamp DESC;
            """;
    public static final String SQL_NOTIF_MARK_AS_READ = """
                UPDATE notifications 
                SET readStatus = TRUE 
                WHERE notificationId = ?;
            """;

    //subscription - amend user status
    public static final String SQL_SUBSCRIPTION_UPDATE_STATUS = """
                UPDATE roles 
                SET role = ? 
                WHERE userId = ?
            """;
    //ADD SUBSCRIPTION
    public static final String SQL_SUBSCRIPTION_INSERT = """
                INSERT INTO subscriptions (userId, subscriptionType, startDate, endDate, status, autoRenew, lastPaymentDate, nextPaymentDate) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
}

