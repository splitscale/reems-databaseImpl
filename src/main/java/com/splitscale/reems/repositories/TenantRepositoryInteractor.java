package com.splitscale.reems.repositories;

import com.splitscale.reems.tenantinfo.TenantInfo;
import com.splitscale.reems.tenantinfo.TenantInfoRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.splitscale.reems.driver.DatabaseDriver;

public class TenantRepositoryInteractor implements TenantRepository {
  private DatabaseDriver db;

  public TenantRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  public String add(TenantInfoRequest tenantInfoRequest) throws IOException {
    String query = "INSERT INTO tenant_info (tenant_name, created, edited) VALUES (?, ?, ?);";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, tenantInfoRequest.getTenantName());
      pstmt.setDate(2, tenantInfoRequest.getCreated());
      pstmt.setDate(3, tenantInfoRequest.getEdited());

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException("Could not add tenant info to the database due to a server error: " + e.getMessage());
    }
  }

  public void delete(String id) throws IOException {
    String query = "DELETE FROM tenant_info WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not delete tenant info from the database due to a server error: " + e.getMessage());
    }
  }

  public List<TenantInfo> getAll() throws IOException {
    List<TenantInfo> tenantInfoList = new ArrayList<>();

    String query = "SELECT id, tenant_name, created, edited FROM tenant_info;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        String tenantName = rs.getString("tenant_name");
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");

        TenantInfo tenantInfo = new TenantInfo(id, tenantName, created, edited);
        tenantInfoList.add(tenantInfo);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve tenant info from the database due to a server error: " + e.getMessage());
    }

    return tenantInfoList;
  }

  public TenantInfo getById(String id) throws IOException {
    String query = "SELECT id, tenant_name, created, edited FROM tenant_info WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        String tenantName = rs.getString("tenant_name");
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");

        TenantInfo tenantInfo = new TenantInfo(id, tenantName, created, edited);
        conn.close();
        return tenantInfo;
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve tenant info from the database due to a server error: " + e.getMessage());
    }
  }

  public void update(TenantInfo tenantInfo) throws IOException {
    String query = "UPDATE tenant_info SET tenant_name = ?, created = ?, edited = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, tenantInfo.getTenantName());
      pstmt.setDate(2, tenantInfo.getCreated());
      pstmt.setDate(3, tenantInfo.getEdited());
      pstmt.setString(4, tenantInfo.getId());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not update tenant info in the database due to a server error: " + e.getMessage());
    }
  }
}
