package com.splitscale.reems;

import static org.junit.Assert.assertTrue;

import com.surrealdb.connection.SurrealWebSocketConnection;
import com.surrealdb.driver.SyncSurrealDriver;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

  /**
   * Rigorous Test :-)
   */
  @Test
  public void shouldAnswerWithTrue() {
    SurrealWebSocketConnection conn = new SurrealWebSocketConnection(
      "localhost",
      8000,
      false
    );
    conn.connect(5);
    SyncSurrealDriver driver = new SyncSurrealDriver(conn);
    driver.signIn("root", "root");
    driver.use("namespace-name", "database-name");
    String tableName = "user";
    driver.delete(tableName);
    User tobie = driver.create(tableName, User.builder().name("Tobie").build());
    User jaime = driver.create(tableName, User.builder().name("Tobie").build());
    List<Map<String, String>> updates = driver.update(
      jaime.id,
      Map.of("name", "Jaime")
    );
    List<User> allUsers = driver.select(tableName, User.class);
    System.out.printf("All users = %s", allUsers);
    conn.disconnect();
  }
}
