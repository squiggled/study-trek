package vttp.proj2.backend.exceptions;

public class UserAddCourseException extends RuntimeException{

    public UserAddCourseException() {
        super("🔴 Failed to save registered course");
    }

    public UserAddCourseException(String message) {
        super(message);
    }
    
}
