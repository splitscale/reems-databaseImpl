package com.splitscale.reems;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import com.splitscale.reems.driver.DatabaseDriver;

public class DatabaseDriverTest {

    private DatabaseDriver databaseDriver;

    @Before
    public void setup() {
        try {
            databaseDriver = new DatabaseDriver(Path.of("src", "test", "resources", "db.properties").toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to load database configuration: " + e.getMessage());
        }
    }

    @Test
    public void testGetConnection() {
        try {
            // Attempt to establish a database connection
            Connection conn = databaseDriver.getConnection();

            // Check if the connection is not null
            assertNotNull(conn);

            // Check if the connection is valid
            assertTrue(conn.isValid(5));

            // Close the connection
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to establish a database connection: " + e.getMessage());
        }
    }
}
