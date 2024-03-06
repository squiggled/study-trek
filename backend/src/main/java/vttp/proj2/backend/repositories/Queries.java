package vttp.proj2.backend.repositories;

public class Queries {

    public static final String SQL_FIND_USER_BY_EMAIL = """
                SELECT 1
                FROM user_info
                WHERE email = ?
            """;

    public static final String SQL_GET_HASHED_PASSWORD_BY_EMAIL = """
                SELECT passwordHash
                FROM accountInfo
                WHERE email = ?
            """;
}
