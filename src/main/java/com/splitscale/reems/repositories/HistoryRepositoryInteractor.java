package com.splitscale.reems.repositories;

import com.splitscale.reems.core.history.History;
import com.splitscale.reems.core.history.HistoryRequest;
import com.splitscale.reems.core.repositories.HistoryRepository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.splitscale.reems.driver.DatabaseDriver;

public class HistoryRepositoryInteractor implements HistoryRepository {
  private DatabaseDriver db;

  public HistoryRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  @Override
  public String create(HistoryRequest historyRequest) throws IOException {
    String query = "INSERT INTO History (header, body) VALUES (?, ?, ?, ?);";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setString(2, historyRequest.getHeader());
      pstmt.setString(3, historyRequest.getBody());

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException("Could not create history entry in the database due to a server error: " + e.getMessage());
    }
  }

  public void deleteAll() throws IOException {
    String query = "DELETE FROM History;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not delete all history records from the database due to a server error: " + e.getMessage());
    }
  }

  public void deleteById(String id) throws IOException {
    String query = "DELETE FROM History WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not delete history record from the database due to a server error: " + e.getMessage());
    }
  }

  public List<History> getAll() throws IOException {
    List<History> historyList = new ArrayList<>();

    String query = "SELECT id, header, body, created FROM History;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        String header = rs.getString("header");
        String body = rs.getString("body");
        Date created = rs.getDate("created");

        History history = new History(id, header, body, created);
        historyList.add(history);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve history records from the database due to a server error: " + e.getMessage());
    }

    return historyList;
  }

  public History getById(String id) throws IOException {
    String query = "SELECT id, header, body, created FROM History WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        String header = rs.getString("header");
        String body = rs.getString("body");
        Date created = rs.getDate("created");

        History history = new History(id, header, body, created);
        conn.close();
        return history;
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve history record from the database due to a server error: " + e.getMessage());
    }
  }

  public void update(History history) throws IOException {
    String query = "UPDATE History SET header = ?, body = ?, created = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, history.getHeader());
      pstmt.setString(2, history.getBody());
      pstmt.setString(4, history.getId());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not update history record in the database due to a server error: " + e.getMessage());
    }
  }
}
