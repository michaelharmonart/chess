package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {
    static DataAccess dataAccess;
    static UserService userService;
    static String authToken;

    @BeforeAll
    static void init() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    @BeforeEach
    void beforeEach() throws DataAccessException {
        dataAccess.clear();
    }

    @Test
    void register() throws BadRequestException, DataAccessException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        userService.register(registerRequest);
        assertEquals(new UserData("username", "password", "email"), dataAccess.getUser("username"));
    }

    @Test
    void registerExisting() throws BadRequestException, DataAccessException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        userService.register(registerRequest);
        var registerRequest2 = new UserService.RegisterRequest("username", "password2", "email2");
        assertThrows(UsernameTakenException.class, () -> userService.register(registerRequest2));
    }

    @Test
    void registerBadRequest() {
        var registerRequest = new UserService.RegisterRequest(null, null, null);
        assertThrows(BadRequestException.class, () -> userService.register(registerRequest));
    }


    @Test
    void login() throws UserService.InvalidLoginException, BadRequestException, DataAccessException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        userService.register(registerRequest);
        var loginRequest = new UserService.LoginRequest("username", "password");
        var loginResponse = userService.login(loginRequest);
        authToken = loginResponse.authToken();
        assertNotNull(authToken);
    }

    @Test
    void loginBadRequest() {
        var loginRequest = new UserService.LoginRequest(null, null);
        assertThrows(BadRequestException.class, () -> userService.login(loginRequest));
    }

    @Test
    void loginBadPassword() {
        var loginRequest = new UserService.LoginRequest("username", "password3");
        assertThrows(UserService.InvalidLoginException.class, () -> userService.login(loginRequest));
    }

    @Test
    void loginBadUsername() {
        var loginRequest = new UserService.LoginRequest("username2", "password");
        assertThrows(UserService.InvalidLoginException.class, () -> userService.login(loginRequest));
    }


    @Test
    void logout() throws UnauthorizedException, BadRequestException, DataAccessException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        userService.logout(registerResponse.authToken());
    }

    @Test
    void logoutBadRequest() throws UnauthorizedException, BadRequestException, DataAccessException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        userService.logout(registerResponse.authToken());
        assertThrows(BadRequestException.class, () -> userService.logout(null));
    }

    @Test
    void logoutAgain() throws UnauthorizedException, BadRequestException, DataAccessException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        var registerResponse = userService.register(registerRequest);
        userService.logout(registerResponse.authToken());
        assertThrows(UnauthorizedException.class, () -> userService.logout(registerResponse.authToken()));
    }


}