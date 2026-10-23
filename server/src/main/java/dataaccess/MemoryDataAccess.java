package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private int nextGameID = 1;
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();

    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Username already taken.");
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

    public int generateGameID() {
        var generatedID = nextGameID;
        nextGameID += 1;
        return generatedID;
    }

    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("gameID already in use.");
        }
        games.put(game.gameID(), game);
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Invalid gameID");
        }
        games.put(gameID, gameData);
    }

    public GameData[] listGames() {
        return games.values().toArray(new GameData[0]);
    }

    public void clear() {
        nextGameID = 1;
        users.clear();
        authTokens.clear();
        games.clear();
    }

}
