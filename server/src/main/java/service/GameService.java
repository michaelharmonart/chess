package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;


public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public record ListGamesResponse(GameData[] games) {
    }

    public ListGamesResponse listGames() {
        return new ListGamesResponse(new GameData[]{});
    }

    public record CreateGameRequest(String gameName) {
    }

    public record CreateGameResponse(int gameID) {
    }

    public CreateGameResponse createGame(CreateGameRequest createGameRequest, String authToken) throws BadRequestException, DataAccessException, UnauthorizedException {
        if (createGameRequest.gameName == null | authToken == null) {
            throw new BadRequestException("All fields must be non null");
        }
        var authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Invalid authToken");
        }
        var gameID = dataAccess.generateGameID();
        dataAccess.createGame(new GameData(gameID, null, null, createGameRequest.gameName, new ChessGame()));
        return new CreateGameResponse(gameID);
    }
}
