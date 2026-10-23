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

    public static class InvalidGameException extends Exception {
        public InvalidGameException(String message) {
            super(message);
        }
    }

    public static class TeamTakenException extends Exception {
        public TeamTakenException(String message) {
            super(message);
        }
    }

    public record ListGamesResponse(GameData[] games) {
    }

    public ListGamesResponse listGames(String authToken) throws BadRequestException, DataAccessException, UnauthorizedException {
        if (authToken == null) {
            throw new BadRequestException("All fields must be non null");
        }
        var authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Invalid authToken");
        }
        return new ListGamesResponse(dataAccess.listGames());
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

    public record JoinGameRequest(String playerColor, int gameID) {
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws BadRequestException, DataAccessException, UnauthorizedException, InvalidGameException, TeamTakenException {
        if (joinGameRequest.playerColor == null | authToken == null) {
            throw new BadRequestException("All fields must be non null");
        }
        var authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Invalid authToken");
        }
        var game = dataAccess.getGame(joinGameRequest.gameID);
        if (game == null) {
            throw new InvalidGameException("Invalid gameID");
        }
        GameData newGameData;
        switch (joinGameRequest.playerColor) {
            case "WHITE" -> {
                if (game.whiteUsername() != null) {
                    throw new TeamTakenException("Team already taken");
                }
                newGameData = new GameData(game.gameID(), authData.username(), game.blackUsername(), game.gameName(), game.game());
            }
            case "BLACK" -> {
                if (game.blackUsername() != null) {
                    throw new TeamTakenException("Team already taken");
                }
                newGameData = new GameData(game.gameID(), game.whiteUsername(), authData.username(), game.gameName(), game.game());
            }
            default -> throw new BadRequestException("Invalid teamColor");
        }
        dataAccess.updateGame(game.gameID(), newGameData);
    }
}
