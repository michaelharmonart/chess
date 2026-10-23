package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests {
    static DataAccess dataAccess;
    static UserService userService;
    static GameService gameService;

    @BeforeAll
    static void init() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
    }

    @BeforeEach
    void beforeEach() throws DataAccessException {
        dataAccess.clear();
    }

    @Test
    void listGames() throws DataAccessException, BadRequestException, UnauthorizedException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        dataAccess.createGame(new GameData(1, null, null, "game", new ChessGame()));
        gameService.listGames(registerResponse.authToken());
    }

    @Test
    void listGamesBadRequest() throws DataAccessException {
        dataAccess.createGame(new GameData(1, null, null, "game", new ChessGame()));
        assertThrows(BadRequestException.class, () -> gameService.listGames(null));
    }

    @Test
    void listGamesUnauthorized() throws DataAccessException {
        dataAccess.createGame(new GameData(1, null, null, "game", new ChessGame()));
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("badAuth"));
    }


    @Test
    void createGame() throws BadRequestException, DataAccessException, UnauthorizedException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        var createGameRequest = new GameService.CreateGameRequest("game");
        gameService.createGame(createGameRequest, registerResponse.authToken());
    }

    @Test
    void createGameBadRequest() {
        var createGameRequest = new GameService.CreateGameRequest(null);
        assertThrows(BadRequestException.class, () -> gameService.createGame(createGameRequest, null));
    }

    @Test
    void createGameUnauthorized() {
        var createGameRequest = new GameService.CreateGameRequest("game");
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(createGameRequest, "badAuth"));
    }

    @Test
    void joinGameWhite() throws BadRequestException, DataAccessException, UnauthorizedException, GameService.InvalidGameException, GameService.TeamTakenException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        var createGameRequest = new GameService.CreateGameRequest("game");
        var createGameResponse = gameService.createGame(createGameRequest, registerResponse.authToken());
        var joinGameRequest = new GameService.JoinGameRequest("WHITE", createGameResponse.gameID());
        gameService.joinGame(joinGameRequest, registerResponse.authToken());
    }

    @Test
    void joinGameBlack() throws BadRequestException, DataAccessException, UnauthorizedException, GameService.InvalidGameException, GameService.TeamTakenException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        var createGameRequest = new GameService.CreateGameRequest("game");
        var createGameResponse = gameService.createGame(createGameRequest, registerResponse.authToken());
        var joinGameRequest = new GameService.JoinGameRequest("BLACK", createGameResponse.gameID());
        gameService.joinGame(joinGameRequest, registerResponse.authToken());
    }

    @Test
    void joinGameBadRequest() throws BadRequestException, DataAccessException, UnauthorizedException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        var createGameRequest = new GameService.CreateGameRequest("game");
        var createGameResponse = gameService.createGame(createGameRequest, registerResponse.authToken());
        var joinGameRequest = new GameService.JoinGameRequest(null, createGameResponse.gameID());
        assertThrows(BadRequestException.class, () -> gameService.joinGame(joinGameRequest, null));
    }

    @Test
    void joinGameUnauthorized() throws BadRequestException, DataAccessException, UnauthorizedException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        var createGameRequest = new GameService.CreateGameRequest("game");
        var createGameResponse = gameService.createGame(createGameRequest, registerResponse.authToken());
        var joinGameRequest = new GameService.JoinGameRequest("BLACK", createGameResponse.gameID());
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame(joinGameRequest, "badAuth"));
    }

    @Test
    void joinGameInvalidID() throws BadRequestException, DataAccessException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        var joinGameRequest = new GameService.JoinGameRequest("WHITE", -9);
        assertThrows(GameService.InvalidGameException.class, () -> gameService.joinGame(joinGameRequest, registerResponse.authToken()));
    }

    @Test
    void joinGameBlackTaken() throws BadRequestException, DataAccessException, UnauthorizedException, GameService.InvalidGameException, GameService.TeamTakenException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        var registerRequest2 = new UserService.RegisterRequest("username2", "password", "email");
        var registerResponse2 = userService.register(registerRequest2);
        var createGameRequest = new GameService.CreateGameRequest("game");
        var createGameResponse = gameService.createGame(createGameRequest, registerResponse.authToken());
        var joinGameRequest = new GameService.JoinGameRequest("BLACK", createGameResponse.gameID());
        gameService.joinGame(joinGameRequest, registerResponse.authToken());
        assertThrows(GameService.TeamTakenException.class, () -> gameService.joinGame(joinGameRequest, registerResponse2.authToken()));
    }

    @Test
    void joinGameWhiteTaken() throws BadRequestException, DataAccessException, UnauthorizedException, GameService.InvalidGameException, GameService.TeamTakenException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        var registerRequest2 = new UserService.RegisterRequest("username2", "password", "email");
        var registerResponse2 = userService.register(registerRequest2);
        var createGameRequest = new GameService.CreateGameRequest("game");
        var createGameResponse = gameService.createGame(createGameRequest, registerResponse.authToken());
        var joinGameRequest = new GameService.JoinGameRequest("WHITE", createGameResponse.gameID());
        gameService.joinGame(joinGameRequest, registerResponse.authToken());
        assertThrows(GameService.TeamTakenException.class, () -> gameService.joinGame(joinGameRequest, registerResponse2.authToken()));
    }

    @Test
    void joinGameInvalidTeam() throws BadRequestException, DataAccessException, UnauthorizedException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        var createGameRequest = new GameService.CreateGameRequest("game");
        var createGameResponse = gameService.createGame(createGameRequest, registerResponse.authToken());
        var joinGameRequest = new GameService.JoinGameRequest("invalidTeam", createGameResponse.gameID());
        assertThrows(BadRequestException.class, () -> gameService.joinGame(joinGameRequest, registerResponse.authToken()));
    }


}