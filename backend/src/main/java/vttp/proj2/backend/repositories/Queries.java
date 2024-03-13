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
}
