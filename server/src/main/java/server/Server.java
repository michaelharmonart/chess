package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UsernameTakenException;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    private final UserService userService;
    private final GameService gameService;
    private final AdminService adminService;

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        adminService = new AdminService(dataAccess);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", this::handleClear);
        javalin.post("/user", this::handleRegister);
        javalin.post("/session", this::handleLogin);
        javalin.delete("/session", this::handleLogout);
        javalin.get("/game", this::handleListGames);
        javalin.post("/game", this::handleCreateGame);
        javalin.put("game", this::handleJoinGame);
        javalin.exception(Exception.class, this::handleException);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void handleClear(Context context) throws DataAccessException {
        adminService.clear();
        context.result("{}");
    }

    private void handleRegister(Context context) throws DataAccessException, BadRequestException {
        var serializer = new Gson();
        var registerRequest = serializer.fromJson(context.body(), UserService.RegisterRequest.class);
        try {
            var registerResponse = userService.register(registerRequest);
            context.result(serializer.toJson(registerResponse));
        } catch (UsernameTakenException e) {
            var body = serializer.toJson(Map.of("message", "Error: already taken"));
            context.status(403);
            context.result(body);
        }
    }

    private void handleLogin(Context context) throws DataAccessException, UnauthorizedException, BadRequestException {
        var serializer = new Gson();
        var loginRequest = serializer.fromJson(context.body(), UserService.LoginRequest.class);
        var loginResponse = userService.login(loginRequest);
        context.result(serializer.toJson(loginResponse));
    }

    private void handleLogout(Context context) throws DataAccessException, UnauthorizedException, BadRequestException {
        var authToken = context.header("authorization");
        userService.logout(authToken);
        context.result("{}");
    }

    private void handleListGames(Context context) throws DataAccessException, UnauthorizedException, BadRequestException {
        var authToken = context.header("authorization");
        var serializer = new Gson();
        var listGamesResponse = gameService.listGames(authToken);
        context.result(serializer.toJson(listGamesResponse));
    }

    private void handleCreateGame(Context context) throws DataAccessException, UnauthorizedException, BadRequestException {
        var authToken = context.header("authorization");
        var serializer = new Gson();
        var createGameRequest = serializer.fromJson(context.body(), GameService.CreateGameRequest.class);
        var createGameResponse = gameService.createGame(createGameRequest, authToken);
        context.result(serializer.toJson(createGameResponse));
    }

    private void handleJoinGame(Context context) throws DataAccessException, UnauthorizedException, BadRequestException {
        var authToken = context.header("authorization");
        var serializer = new Gson();
        var joinGameRequest = serializer.fromJson(context.body(), GameService.JoinGameRequest.class);
        try {
            gameService.joinGame(joinGameRequest, authToken);
        } catch (GameService.InvalidGameException e) {
            throw new BadRequestException(e.getMessage());
        } catch (GameService.TeamTakenException e) {
            var body = serializer.toJson(Map.of("message", "Error: already taken"));
            context.status(403);
            context.result(body);
            return;
        }
        context.result("{}");
    }

    private void handleException(Exception e, Context context) {
        var serializer = new Gson();
        if (e instanceof BadRequestException) {
            var body = serializer.toJson(Map.of("message", "Error: bad request"));
            context.status(400);
            context.result(body);
            return;
        } else if (e instanceof UnauthorizedException) {
            var body = serializer.toJson(Map.of("message", "Error: unauthorized"));
            context.status(401);
            context.result(body);
            return;
        }
        var body = serializer.toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        context.status(500);
        context.result(body);

    }
}
