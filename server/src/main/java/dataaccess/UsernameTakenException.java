package dataaccess;

public class UsernameTakenException extends DataAccessException {
    public UsernameTakenException(String message) {
        super(message);
    }
}