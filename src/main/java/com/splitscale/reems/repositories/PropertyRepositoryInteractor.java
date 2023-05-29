package com.splitscale.reems.repositories;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.core.properties.Property;
import com.splitscale.reems.core.properties.PropertyRequest;
import com.splitscale.reems.core.repositories.PropertyRepository;

public class PropertyRepositoryInteractor implements PropertyRepository {
  private DatabaseDriver db;

  public PropertyRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  public String add(PropertyRequest propertyRequest) throws IOException {
    String query = "INSERT INTO Property (name, location, status) VALUES (?, ?, ?);";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, propertyRequest.getName());
      pstmt.setString(2, propertyRequest.getLocation());
      pstmt.setString(3, propertyRequest.getStatus());

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException("Could not add property info to the database due to a server error: " + e.getMessage());
    }
  }

  public void delete(String id) throws IOException {
    String query = "DELETE FROM Property WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not delete property info from the database due to a server error: " + e.getMessage());
    }
  }

  public List<Property> getAll() throws IOException {
    List<Property> propertyList = new ArrayList<>();

    String query = "SELECT id, name, location, status FROM Property;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String location = rs.getString("location");
        String status = rs.getString("status");

        Property property = new Property(id, null, null, name, location, status);
        propertyList.add(property);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve property info from the database due to a server error: " + e.getMessage());
    }

    return propertyList;
  }

  public Property getById(String id) throws IOException {
    String query = "SELECT id, name, location, status FROM Property WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        String name = rs.getString("name");
        String location = rs.getString("location");
        String status = rs.getString("status");

        Property property = new Property(id, null, null, name, location, status);
        conn.close();
        return property;
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve property info from the database due to a server error: " + e.getMessage());
    }
  }

  public void update(Property property) throws IOException {
    String query = "UPDATE Property SET name = ?, location = ?, status = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, property.getName());
      pstmt.setString(2, property.getLocation());
      pstmt.setString(3, property.getStatus());
      pstmt.setString(4, property.getId());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not update property info in the database due to a server error: " + e.getMessage());
    }
  }
}
