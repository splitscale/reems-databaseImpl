package com.splitscale.reems.repositories;

import com.splitscale.reems.core.mitigation.Mitigation;
import com.splitscale.reems.core.mitigation.MitigationRequest;
import com.splitscale.reems.core.repositories.MitigationRepository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.splitscale.reems.driver.DatabaseDriver;

public class MitigationRepositoryInteractor implements MitigationRepository {
  private DatabaseDriver db;

  public MitigationRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  @Override
  public String add(MitigationRequest mitigationRequest) throws IOException {
    String query = "INSERT INTO Mitigation (expense_id, created, edited, title, description, cost, status) VALUES (?, ?, ?, ?, ?, ?, ?);";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, mitigationRequest.getExpenseId());
      pstmt.setDate(2, mitigationRequest.getCreated());
      pstmt.setDate(3, mitigationRequest.getEdited());
      pstmt.setString(4, mitigationRequest.getTitle());
      pstmt.setString(5, mitigationRequest.getDescription());
      pstmt.setString(6, mitigationRequest.getCost());
      pstmt.setString(7, mitigationRequest.getStatus());

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException("Could not add mitigation info to the database due to a server error: " + e.getMessage());
    }
  }

  @Override
  public void delete(String id) throws IOException {
    String query = "DELETE FROM Mitigation WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not delete mitigation info from the database due to a server error: " + e.getMessage());
    }
  }

  @Override
  public List<Mitigation> getAll() throws IOException {
    List<Mitigation> mitigationList = new ArrayList<>();

    String query = "SELECT id, expense_id, created, edited, title, description, cost, status FROM Mitigation;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        String expenseId = rs.getString("expense_id");
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");
        String title = rs.getString("title");
        String description = rs.getString("description");
        Double cost = rs.getDouble("cost");
        String status = rs.getString("status");

        Mitigation mitigation = new Mitigation(id, expenseId, created, edited, title, description, cost, status);
        mitigationList.add(mitigation);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve mitigation info from the database due to a server error: " + e.getMessage());
    }

    return mitigationList;
  }

  @Override
  public Mitigation getById(String id) throws IOException {
    String query = "SELECT id, expense_id, created, edited, title, description, cost, status FROM Mitigation WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        String expenseId = rs.getString("expense_id");
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");
        String title = rs.getString("title");
        String description = rs.getString("description");
        Double cost = rs.getDouble("cost");
        String status = rs.getString("status");

        Mitigation mitigation = new Mitigation(id, expenseId, created, edited, title, description, cost, status);
        conn.close();
        return mitigation;
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve mitigation info from the database due to a server error: " + e.getMessage());
    }
  }

  @Override
  public void update(Mitigation mitigation) throws IOException {
    String query = "UPDATE Mitigation SET expense_id = ?, created = ?, edited = ?, title = ?, description = ?, cost = ?, status = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, mitigation.getExpenseId());
      pstmt.setString(4, mitigation.getTitle());
      pstmt.setString(5, mitigation.getDescription());
      pstmt.setDouble(6, mitigation.getCost());
      pstmt.setString(7, mitigation.getStatus());
      pstmt.setString(8, mitigation.getId());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not update mitigation info in the database due to a server error: " + e.getMessage());
    }
  }
}
