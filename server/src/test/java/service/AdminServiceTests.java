package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTests {
    static DataAccess dataAccess;
    static AdminService adminService;

    @BeforeAll
    static void init() {
        dataAccess = new MemoryDataAccess();
        adminService = new AdminService(dataAccess);
    }

    @Test
    void clear() throws DataAccessException {
        adminService.clear();
    }
}