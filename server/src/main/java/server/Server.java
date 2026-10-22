package server;

import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", this::handleClear);
        javalin.post("/user", this::handleRegister);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public void handleClear(Context context) {
        context.json("{}");
    }

    public void handleRegister(Context context) {
        context.json("{}");
    }

}
