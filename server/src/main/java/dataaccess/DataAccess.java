package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {

    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;


    void clear() throws DataAccessException;
}
