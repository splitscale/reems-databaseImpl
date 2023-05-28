package com.splitscale.reems.driver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseDriver {
  Properties properties;
  InputStream fileStream;

  public DatabaseDriver(String path) throws FileNotFoundException, IOException {
    this.properties = new Properties();
    properties.load(new FileInputStream(path));
  }

  public DatabaseDriver() {
}

private String getUrl() {
    return properties.getProperty("mysql.url");
  }

  private String getUsername() {
    return properties.getProperty("mysql.username");
  }

  private String getPassword() {
    return properties.getProperty("mysql.password");
  }

  public Connection getConnection() throws SQLException {

    return DriverManager.getConnection(getUrl(), getUsername(), getPassword());
  }
}