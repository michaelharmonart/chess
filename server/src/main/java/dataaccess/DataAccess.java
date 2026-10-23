package dataaccess;

import model.UserData;

public interface DataAccess {

    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clear() throws DataAccessException;
}
