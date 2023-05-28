package com.splitscale.reems.repositories;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.energy.consumption.EnergyConsumption;
import com.splitscale.reems.energy.consumption.EnergyConsumptionRequest;

import net.bytebuddy.utility.dispatcher.JavaDispatcher.Container;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class EnergyConsumptionRepositoryInteractor
    implements EnergyConsumptionRepository {
  private DatabaseDriver db;

  public EnergyConsumptionRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  @Override
  public String add(EnergyConsumptionRequest request) throws IOException {

  private static final String INSERT_QUERY = "INSERT INTO EnergyConsumption (id, property_id, property_name, usage, consumed, amount, date_created, date_modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

  public EnergyConsumption add(String id, String propertyId, String propertyName, String usage, String consumed,
      String amount, Date created, Date modified) throws IOException {
    EnergyConsumption energyConsumption = new EnergyConsumption();
    energyConsumption.setId(id);
    energyConsumption.setPropertyId(propertyId);
    energyConsumption.setPropertyName(propertyName);
    energyConsumption.setUsage(usage);
    energyConsumption.setConsumed(consumed);
    energyConsumption.setAmount(amount);
    energyConsumption.setCreated(created);
    energyConsumption.setModified(modified);

    try (Connection conn = db.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
      pstmt.setString(1, id);
      pstmt.setString(2, propertyId);
      pstmt.setString(2, propertyName);
      pstmt.setString(4, usage);
      pstmt.setString(5, consumed);
      pstmt.setString(6, amount);
      pstmt.setDate(7, new java.sql.Date(created.getTime()));
      pstmt.setDate(8, new java.sql.Date(modified.getTime()));

      pstmt.executeUpdate();

      ResultSet generatedKeys = pstmt.executeQuery();

      if (generatedKeys.next()) {
        long ID = generatedKeys.getLong(1);

        energyConsumption.setContainerID(id);
      }

    } catch (SQLException e) {
      throw new IOException("Error occurred while adding EnergyConsumption: " + e.getMessage());
    }

    return energyConsumption;
  }

  @Override
  public void delete(String arg0) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'delete'");
  }

  @Override
  public List<EnergyConsumption> getAll() throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAll'");
  }

  @Override
  public EnergyConsumption getById(String arg0) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getById'");
  }

  @Override
  public void update(EnergyConsumption arg0) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }
}
