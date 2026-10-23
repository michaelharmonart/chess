package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();

    private final Map<String, AuthData> authTokens = new HashMap<>();

    public void createUser(UserData user) throws UsernameTakenException {
        if (users.containsKey(user.username())) {
            throw new UsernameTakenException("Username already taken.");
        }
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void createAuth(AuthData authData) {
        authTokens.put(authData.authToken(), authData);
    }

    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }

    public void clear() {
        users.clear();
        authTokens.clear();
    }

}
