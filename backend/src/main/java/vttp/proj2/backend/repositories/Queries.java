package vttp.proj2.backend.repositories;

public class Queries {

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
    public static final String SQL_GET_FIRSTNAME_BY_EMAIL = """
                SELECT firstName
                FROM user_info
                WHERE email = ?
            """;

    public static final String SQL_GET_USER_ROLE_BY_ID = """
                SELECT role
                FROM roles
                WHERE userId = ?
            """;

    public static final String SQL_GET_HASHED_PASSWORD_BY_EMAIL = """
                SELECT passwordHash
                FROM user_info
                WHERE email = ?
            """;

    public static final String SQL_CREATE_NEW_USER = """
                INSERT into user_info(userId, email, passwordHash, lastPasswordResetDate, firstName, lastName, profilePicUrl)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    public static final String SQL_ASSIGN_ROLE_USER = """
                INSERT INTO roles(userId, role) VALUES (?, ?);
            """;

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
    //for updating user progress + user adding new courses
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
}
