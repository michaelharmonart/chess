package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UsernameTakenException;
import io.javalin.*;
import io.javalin.http.Context;
import service.AdminService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    private final UserService userService;
    private final AdminService adminService;

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        adminService = new AdminService(dataAccess);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", this::handleClear);
        javalin.post("/user", this::handleRegister);
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

    private void handleRegister(Context context) throws DataAccessException {
        var serializer = new Gson();
        var registerRequest = serializer.fromJson(context.body(), UserService.RegisterRequest.class);
        try {
            var registerResponse = userService.register(registerRequest);
            context.result(new Gson().toJson(registerResponse));
        } catch (UsernameTakenException e) {
            var body = new Gson().toJson(Map.of("message", "Error: already taken"));
            context.status(403);
            context.result(body);
        } catch (UserService.BadRequestException e) {
            var body = new Gson().toJson(Map.of("message", "Error: bad request"));
            context.status(400);
            context.result(body);
        }
    }

    private void handleException(Exception e, Context context) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        context.status(500);
        context.result(body);
    }
}
