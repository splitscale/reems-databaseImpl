package com.splitscale.reems.repositories;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.energy.consumption.EnergyConsumption;
import com.splitscale.reems.energy.consumption.EnergyConsumptionRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnergyConsumptionRepositoryInteractor implements EnergyConsumptionRepository {
  private DatabaseDriver db;

  public EnergyConsumptionRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  public String add(EnergyConsumptionRequest energyConsumptionRequest) throws IOException {
    String query = "INSERT INTO EnergyConsumption (id, expense_id, property_id, created, edited, consumed_value, energy_unit, usage_value, cost_per_unit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

      pstmt.setString(1, energyConsumptionRequest.getId());
      pstmt.setString(2, energyConsumptionRequest.getExpenseId());
      pstmt.setString(3, energyConsumptionRequest.getPropertyId());
      pstmt.setDate(4, energyConsumptionRequest.getCreated());
      pstmt.setDate(5, energyConsumptionRequest.getEdited());
      pstmt.setString(6, energyConsumptionRequest.getConsumedValue());
      pstmt.setString(7, energyConsumptionRequest.getEnergyUnit());
      pstmt.setString(8, energyConsumptionRequest.getUsageValue());
      pstmt.setDouble(9, energyConsumptionRequest.getCostPerUnit());

      pstmt.executeUpdate();
      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException("Failed to add energy consumption to the database." + e.getMessage());
    }
  }

  @Override
  public void delete(String id) throws IOException {
    try (Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM EnergyConsumption WHERE id = ?")) {

      statement.setString(1, id);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new IOException("Failed to delete energy consumption from the database.", e);
    }
  }

  @Override
  public List<EnergyConsumption> getAll() throws IOException {
    List<EnergyConsumption> energyConsumptions = new ArrayList<>();

    String query = "SELECT id, expense_id, property_id, created, edited, consumed_value, energy_unit, usage_value, cost_per_unit FROM EnergyConsumption;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        String expenseId = rs.getString("expenseId");
        String propertyId = rs.getString("propertyId");
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");
        String consumedValue = rs.getString("consumedValue");
        String energyUnit = rs.getString("energyUnit");
        Double costPerUnit = rs.getDouble("costPerUnit");

        EnergyConsumption energyConsumption = new EnergyConsumption(id, expenseId, propertyId, created, edited,
            consumedValue, energyUnit, energyUnit, costPerUnit);
        energyConsumptions.add(energyConsumption);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException("Failed to retrieve all energy consumption entries from the database." + e.getMessage());
    }

    return energyConsumptions;
  }

  @Override
  public EnergyConsumption getById(String id) throws IOException {
    String query = "SELECT * FROM EnergyConsumption WHERE id = ?";
    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return createEnergyConsumptionFromResultSet(rs);
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException("Failed to retrieve energy consumption entry from the database." + e.getMessage());
    }
  }

  @Override
  public void update(EnergyConsumption energyConsumption) throws IOException {
    String query = "UPDATE EnergyConsumption SET expense_id = ?, property_id = ?, created = ?, edited = ?, consumed_value = ?, energy_unit = ?, usage_value = ?, cost_per_unit = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);

      pstmt.setString(1, energyConsumption.getId());
      pstmt.setString(2, energyConsumption.getExpenseId());
      pstmt.setString(3, energyConsumption.getPropertyId());
      pstmt.setString(4, energyConsumption.getConsumedValue());
      pstmt.setString(5, energyConsumption.getEnergyUnit());
      pstmt.setString(6, energyConsumption.getUsageValue());
      pstmt.setDouble(7, energyConsumption.getCostPerUnit());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Failed to update energy consumption in the database." + e.getMessage());
    }
  }

  private EnergyConsumption createEnergyConsumptionFromResultSet(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("id");
    String expenseId = resultSet.getString("expense_id");
    String propertyId = resultSet.getString("property_id");
    String consumedValue = resultSet.getString("consumed_value");
    String energyUnit = resultSet.getString("energy_unit");
    String usageValue = resultSet.getString("usage_value");
    double costPerUnit = resultSet.getDouble("cost_per_unit");

    return new EnergyConsumption(id, expenseId, propertyId, null, null, consumedValue, energyUnit, usageValue,
        costPerUnit);
  }
}
