package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public static class BadRequestException extends Exception {
        public BadRequestException(String message) {
            super(message);
        }
    }

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public record RegisterRequest(String username, String password, String email) {
    }

    public record RegisterResponse(String username, String authToken) {
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws DataAccessException, BadRequestException {
        if (registerRequest.username == null | registerRequest.password == null | registerRequest.password == null) {
            throw new BadRequestException("All feilds must be non null");
        }

        dataAccess.createUser(new UserData(registerRequest.username, registerRequest.password, registerRequest.email));
        return new RegisterResponse(registerRequest.username, generateToken());
    }
}
