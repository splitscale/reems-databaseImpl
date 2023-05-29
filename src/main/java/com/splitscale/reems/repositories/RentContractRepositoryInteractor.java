package com.splitscale.reems.repositories;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.rentContract.RentContract;
import com.splitscale.reems.rentContract.RentContractRequest;

public class RentContractRepositoryInteractor implements RentContractRepository {
  private DatabaseDriver db;

  public RentContractRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  public String add(RentContractRequest rentContractRequest) throws IOException {
    String query = "INSERT INTO RentContract (tenant_info_id, property_id) VALUES (?, ?);";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, rentContractRequest.getTenantInfoId());
      pstmt.setString(2, rentContractRequest.getPropertyId());

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException(
          "Could not add rent contract info to the database due to a server error: " + e.getMessage());
    }
  }

  public void delete(String id) throws IOException {
    String query = "DELETE FROM RentContract WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not delete rent contract info from the database due to a server error: " + e.getMessage());
    }
  }

  public List<RentContract> getAll() throws IOException {
    List<RentContract> rentContractList = new ArrayList<>();

    String query = "SELECT id, tenant_info_id, property_id FROM RentContract;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        String tenantInfoId = rs.getString("tenant_info_id");
        String propertyId = rs.getString("property_id");

        RentContract rentContract = new RentContract(id, tenantInfoId, propertyId);
        rentContractList.add(rentContract);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve rent contract info from the database due to a server error: " + e.getMessage());
    }

    return rentContractList;
  }

  public RentContract getById(String id) throws IOException {
    String query = "SELECT id, tenant_info_id, property_id FROM RentContract WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        String tenantInfoId = rs.getString("tenant_info_id");
        String propertyId = rs.getString("property_id");

        RentContract rentContract = new RentContract(id, tenantInfoId, propertyId);
        conn.close();
        return rentContract;
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve rent contract info from the database due to a server error: " + e.getMessage());
    }
  }

  public void update(RentContractRequest rentContractRequest) throws IOException {
    String query = "UPDATE RentContract SET tenant_info_id = ?, property_id = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, rentContractRequest.getTenantInfoId());
      pstmt.setString(2, rentContractRequest.getPropertyId());
      pstmt.setString(3, rentContractRequest.getId());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not update rent contract info in the database due to a server error: " + e.getMessage());
    }
  }
}
