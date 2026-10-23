package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {
    static DataAccess dataAccess;
    static UserService userService;

    @BeforeAll
    static void init() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    @Test
    void registerPostive() throws BadRequestException, DataAccessException {
        var registerRequest = new UserService.RegisterRequest("username", "password", "email");
        userService.register(registerRequest);
        assertEquals(new UserData("username", "password", "email"), dataAccess.getUser("username"));
    }

    @Test
    void registerExisting() throws BadRequestException, DataAccessException {
        var registerRequest = new UserService.RegisterRequest("username", "password2", "email2");
        assertThrows(UsernameTakenException.class, () -> userService.register(registerRequest));
    }

    @Test
    void registerBadRequest() throws BadRequestException, DataAccessException {
        var registerRequest = new UserService.RegisterRequest("username", null, "email2");
        assertThrows(BadRequestException.class, () -> userService.register(registerRequest));
    }


    @Test
    void login() {
    }

    @Test
    void logout() {
    }
}