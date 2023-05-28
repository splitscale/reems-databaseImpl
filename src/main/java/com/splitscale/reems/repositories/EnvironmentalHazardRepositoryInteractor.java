package com.splitscale.reems.repositories;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.hazard.environment.EnvironmentalHazard;
import com.splitscale.reems.hazard.environment.EnvironmentalHazardRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EnvironmentalHazardRepositoryInteractor implements EnvironmentalHazardRepository {
  private DatabaseDriver db;

  public EnvironmentalHazardRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  public String add(EnvironmentalHazardRequest environmentalHazardRequest) throws IOException {
    String query = "INSERT INTO EnvironmentalHazard (id, property_id, created, modified, category, title, description, riskLevel) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

      pstmt.setString(1, environmentalHazardRequest.getId());
      pstmt.setString(2, environmentalHazardRequest.getPropertyId());
      pstmt.setDate(3, environmentalHazardRequest.getCreated());
      pstmt.setDate(4, environmentalHazardRequest.getModified());
      pstmt.setString(5, environmentalHazardRequest.getCategory());
      pstmt.setString(6, environmentalHazardRequest.getTitle());
      pstmt.setString(7, environmentalHazardRequest.getDescription());
      pstmt.setString(8, environmentalHazardRequest.getRiskLevel());

      pstmt.executeUpdate();
      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException("Failed to add environmental hazard to the database. " + e.getMessage());
    }
  }

  @Override
  public void delete(String id) throws IOException {
    try (Connection connection = db.getConnection();
         PreparedStatement statement = connection.prepareStatement("DELETE FROM EnvironmentalHazard WHERE id = ?")) {

      statement.setString(1, id);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new IOException("Failed to delete environmental hazard from the database.", e);
    }
  }

  @Override
  public List<EnvironmentalHazard> getAll() throws IOException {
    List<EnvironmentalHazard> environmentalHazardList = new ArrayList<>();

    String query = "SELECT id, property_id, created, modified, category, title, description, riskLevel FROM EnvironmentalHazard;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        String propertyId = rs.getString("property_id");
        Date created = rs.getDate("created");
        Date modified = rs.getDate("modified");
        String category = rs.getString("category");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String riskLevel = rs.getString("riskLevel");

        EnvironmentalHazard environmentalHazard = new EnvironmentalHazard(id, propertyId, created, modified,
                category, title, description, riskLevel);
        environmentalHazardList.add(environmentalHazard);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException("Failed to retrieve all environmental hazards from the database." + e.getMessage());
    }

    return environmentalHazardList;
  }

  @Override
  public EnvironmentalHazard getById(String id) throws IOException {
    String query = "SELECT * FROM EnvironmentalHazard WHERE id = ?";
    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return createEnvironmentalHazardFromResultSet(rs);
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException("Failed to retrieve environmental hazard from the database." + e.getMessage());
    }
  }

  @Override
  public void update(EnvironmentalHazard environmentalHazard) throws IOException {
    String query = "UPDATE EnvironmentalHazard SET property_id = ?, category = ?, title = ?, description = ?, riskLevel = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);

      pstmt.setString(1, environmentalHazard.getPropertyId());
      pstmt.setString(4, environmentalHazard.getCategory());
      pstmt.setString(5, environmentalHazard.getTitle());
      pstmt.setString(6, environmentalHazard.getDescription());
      pstmt.setString(7, environmentalHazard.getRiskLevel());
      pstmt.setString(8, environmentalHazard.getId());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Failed to update environmental hazard in the database." + e.getMessage());
    }
  }

  private EnvironmentalHazard createEnvironmentalHazardFromResultSet(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("id");
    String propertyId = resultSet.getString("property_id");
    Date created = resultSet.getDate("created");
    Date modified = resultSet.getDate("modified");
    String category = resultSet.getString("category");
    String title = resultSet.getString("title");
    String description = resultSet.getString("description");
    String riskLevel = resultSet.getString("riskLevel");

    return new EnvironmentalHazard(id, propertyId, created, modified, category, title, description, riskLevel);
  }
}
