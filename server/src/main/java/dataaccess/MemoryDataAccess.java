package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();

    private final Set<String> authTokens = new HashSet<>();

    public void createUser(UserData user) throws UsernameTakenException {
        if (users.containsKey(user.username())) {
            throw new UsernameTakenException("Username already taken.");
        }
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void createAuthToken(String authToken) {
        authTokens.add(authToken);
    }

    public boolean isValidAuthToken(String authToken) {
        return authTokens.contains(authToken);
    }

    public void clear() {
        users.clear();
        authTokens.clear();
    }

}
