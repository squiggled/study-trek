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

    //auth - retrieving password, change password
    public static final String SQL_AUTH_GET_HASHED_PASSWORD_BY_EMAIL = """
                SELECT passwordHash
                FROM user_info
                WHERE email = ?
            """;
    public static final String SQL_AUTH_GET_HASHED_PASSWORD_BY_ID = """
                SELECT passwordHash
                FROM user_info
                WHERE userId = ?
            """;
    public static final String SQL_AUTH_PASSWORD_CHANGE = """
                UPDATE user_info
                SET passwordHash = ?
                WHERE userId = ?
            """;

    //auth - registering new user
    public static final String SQL_CREATE_NEW_USER = """
                INSERT into user_info(userId, email, passwordHash, lastPasswordResetDate, firstName, lastName, profilePicUrl)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    public static final String SQL_ASSIGN_ROLE_USER = """
                INSERT INTO roles(userId, role) VALUES (?, ?);
            """;

    //auth - login, retrieving user details, update password
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
                ORDER BY lectureNumber ASC
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
   
            
    //user - updating user progress + adding new courses + details 
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
    public static final String SQL_USER_UPDATE_PROFILE_PICTURE = """
                UPDATE user_info
                SET profilePicUrl = ?
                WHERE userId = ?
            """;
    public static final String SQL_USER_UPDATE_PROFILE_NAMES = """
                UPDATE user_info
                SET firstName = ?, lastName = ?
                WHERE userId = ?
            """;
    public static final String SQL_USER_INTERESTS_DELETE = """
                DELETE FROM interests
                WHERE userId = ?
            """;
    public static final String SQL_USER_INTERESTS_INSERT = """
                INSERT INTO interests (userId, interest)
                VALUES (?, ?)
            """;
    
    //user - course progress
    public static final String SQL_USER_COURSE_GET_ALL_BY_COURSEID="""
                SELECT * FROM course_notes 
                WHERE userId = ? AND courseId = ? 
            """;
    public static final String SQL_USER_COURSE_ADD_NOTE = """
                INSERT into course_notes(courseId, userId, note)
                VALUES (?, ?, ?)
            """;
    public static final String SQL_USER_COURSE_UPDATE_NOTE="""
                UPDATE course_notes
                SET note = ?
                WHERE noteId = ?
            """;
    public static final String SQL_USER_COURSE_GET_LATEST = """
                SELECT * FROM course_notes 
                WHERE userId = ? AND courseId = ? 
                ORDER BY noteId 
                DESC LIMIT 1
            """;
    public static final String SQL_USER_COURSE_GET_LATEST_NOTE="""
                SELECT * FROM course_notes 
                WHERE courseId = ?
                ORDER BY noteId DESC LIMIT 1;
            """;

    //user - indiv curr progress
    public static final String SQL_USER_CURR_GET_ALL_CURR_PROGRESS = """
                SELECT up.* 
                FROM user_progress up 
                JOIN curriculum c 
                ON up.curriculumId = c.curriculumId 
                WHERE c.courseId = ? AND up.userId = ?
            """;
    public static final String SQL_USER_CURR_GET_PROGRESS="""
                SELECT * FROM user_progress 
                WHERE userId = ? AND curriculumId = ?
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

    //user - calendar
    public static final String SQL_USER_CALENDAR_GET_ALL = """
                SELECT *
                FROM calendar_events
                WHERE userId = ?
            """;
    public static final String SQL_USER_CALENDAR_CREATE_NEW = """
                INSERT into calendar_events(userId, title, text, selectedHour, date)
                VALUES (?, ?, ?, ?, ?)
            """;

    //subscription - amend user status
    public static final String SQL_SUBSCRIPTION_UPDATE_STATUS = """
                UPDATE roles 
                SET role = ? 
                WHERE userId = ?
            """;
    //add subscription
    public static final String SQL_SUBSCRIPTION_INSERT = """
                INSERT INTO subscriptions (userId, subscriptionType, startDate, endDate, status, autoRenew, lastPaymentDate, nextPaymentDate) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
    //subscription - find all active premium users
    public static final String SQL_SUBSCRIPTION_GET_ACTIVE_PREMIUM = """
                SELECT s.userId, t.chatId
                FROM subscriptions s
                JOIN telegram_chats t ON s.userId = t.userId
                WHERE s.subscriptionType = 'PREMIUM' AND s.status = 'ACTIVE'
            """;
    
    //telegram chats 
    //insert - new tele chatId
    public static final String SQL_TELEGRAM_FIND_CHATID_BY_USERID = """
                SELECT chatId 
                FROM telegram_chats 
                WHERE userId = ?
            """;
    public static final String SQL_TELEGRAM_CREATE_NEW_ID= """
                INSERT INTO telegram_chats (userId, chatId) 
                VALUES (?, ?)
            """;
    public static final String SQL_TELEGRAM_UPDATE_ID = """
                UPDATE telegram_chats SET chatId = ? 
                WHERE userId = ?
            """;
    public static final String SQL_TELEGRAM_SET_LINKCODE = """
                UPDATE user_info
                SET linkCode = ? 
                WHERE userId = ?
            """;
    public static final String SQL_TELEGRAM_VERIFY_LINKCODE = """
                SELECT userId, email, telegram_user_id 
                FROM user_info 
                WHERE email = ? AND linkCode = ?
            """;
    public static final String SQL_TELEGRAM_FIND_USERID_BY_CHATID = """
                SELECT userId 
                FROM telegram_chats 
                WHERE chatId = ?
            """;
    public static final String SQL_TELEGRAM_GET_COURSES = """
                SELECT title 
                FROM registered_courses 
                WHERE userId = ?
            """;
}

