package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public static class InvalidLoginException extends UnauthorizedException {
        public InvalidLoginException(String message) {
            super(message);
        }
    }


    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public record RegisterRequest(String username, String password, String email) {
    }

    public record RegisterResponse(String username, String authToken) {
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws DataAccessException, BadRequestException {
        if (registerRequest.username == null | registerRequest.password == null | registerRequest.password == null) {
            throw new BadRequestException("All fields must be non null");
        }

        dataAccess.createUser(new UserData(registerRequest.username, registerRequest.password, registerRequest.email));
        return new RegisterResponse(registerRequest.username, generateAuthToken());
    }

    public record LoginRequest(String username, String password) {
    }

    public record LoginResponse(String username, String authToken) {
    }

    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException, InvalidLoginException, BadRequestException {
        if (loginRequest.username == null | loginRequest.password == null) {
            throw new BadRequestException("All fields must be non null");
        }
        var user = dataAccess.getUser(loginRequest.username);
        if (user == null) {
            throw new InvalidLoginException("No user with that username");
        }
        if (!Objects.equals(user.password(), loginRequest.password())) {
            throw new InvalidLoginException("Incorrect password");
        }
        var authToken = generateAuthToken();
        dataAccess.createAuthToken(authToken);
        return new LoginResponse(user.username(), authToken);
    }
}
