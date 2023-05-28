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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentalHazardRepositoryInteractor implements EnvironmentalHazardRepository {
  private DatabaseDriver db;

  public EnvironmentalHazardRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  public String add(EnvironmentalHazardRequest environmentalHazardRequest) throws IOException {
    String query = "INSERT INTO EnvironmentalHazard (id, expense_id, property_id, created, edited, consumed_value, energy_unit, usage_value, cost_per_unit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
      throw new IOException("Failed to delete energy consumption from the database.", e);
    }
  }

  @Override
  public List<EnvironmentalHazard> getAll() throws IOException {
    List<EnvironmentalHazard> environmentalHazard = new ArrayList<>();

    String query = "SELECT id, property_id, created, modified, category, title, description, risk_level FROM EnergyConsumption;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        String propertyId = rs.getString("propertyId");
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");
        String category = rs.getString("category");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String riskLevel = rs.getString("riskLevel");

        EnvironmentalHazard environmentalHazard = new EnvironmentalHazard(id, propertyId, created, edited,
            category, title, description, riskLevel);
        environmentalHazard.add(environmentalHazard);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException("Failed to retrieve all energy consumption entries from the database." + e.getMessage());
    }

    return environmentalHazards;
  }

  @Override
  public EnvironmentalHazards getById(String id) throws IOException {
    String query = "SELECT * FROM EnergyConsumption WHERE id = ?";
    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return createEnvironmentalHazardsFromResultSet(rs);
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException("Failed to retrieve energy consumption entry from the database." + e.getMessage());
    }
  }

  @Override
  public void update(EnvironmentalHazards environmentalHazards) throws IOException {
    String query = "UPDATE EnergyConsumption SET expense_id = ?, property_id = ?, created = ?, edited = ?, consumed_value = ?, energy_unit = ?, usage_value = ?, cost_per_unit = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);

      pstmt.setString(1, environmentalHazards.getId());
      pstmt.setString(2, environmentalHazards.getExpenseId());
      pstmt.setString(3, environmentalHazards.getPropertyId());
      pstmt.setString(4, environmentalHazards.getConsumedValue());
      pstmt.setString(5, environmentalHazards.getEnergyUnit());
      pstmt.setString(6, environmentalHazards.getUsageValue());
      pstmt.setDouble(7, environmentalHazards.getCostPerUnit());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Failed to update energy consumption in the database." + e.getMessage());
    }
  }

  private EnvironmentalHazards createEnergyConsumptionFromResultSet(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("id");
    String expenseId = resultSet.getString("expense_id");
    String propertyId = resultSet.getString("property_id");
    String consumedValue = resultSet.getString("consumed_value");
    String energyUnit = resultSet.getString("energy_unit");
    String usageValue = resultSet.getString("usage_value");
    double costPerUnit = resultSet.getDouble("cost_per_unit");

    return new EnvironmentalHazard();
  }

}
