package com.splitscale.reems.repositories;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.core.hazardStats.HazardStats;
import com.splitscale.reems.core.hazardStats.HazardStatsRequest;
import com.splitscale.reems.core.repositories.HazardStatsRepository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HazardStatsRepositoryInteractor implements HazardStatsRepository {
  private DatabaseDriver db;

  public HazardStatsRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  public String add(HazardStatsRequest hazardStatsRequest) throws IOException {
    String query = "INSERT INTO HazardStats (created, edited, total_hazards, change_this_month) VALUES (?, ?, ?, ?);";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setDate(1, hazardStatsRequest.getCreated());
      pstmt.setDate(2, hazardStatsRequest.getEdited());
      pstmt.setLong(3, hazardStatsRequest.getTotal_hazards());
      pstmt.setLong(4, hazardStatsRequest.getChange_this_month());

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException("Could not add hazard stats to the database due to a server error: " + e.getMessage());
    }
  }

  public void deleteAll() throws IOException {
    String query = "DELETE FROM HazardStats;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not delete hazard stats from the database due to a server error: " + e.getMessage());
    }
  }

  public void deleteById(String id) throws IOException {
    String query = "DELETE FROM HazardStats WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not delete hazard stats from the database due to a server error: " + e.getMessage());
    }
  }

  public List<HazardStats> getAll() throws IOException {
    List<HazardStats> hazardStatsList = new ArrayList<>();

    String query = "SELECT id, created, edited, total_hazards, change_this_month FROM HazardStats;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");
        long totalHazards = rs.getLong("total_hazards");
        long changeThisMonth = rs.getLong("change_this_month");

        HazardStats hazardStats = new HazardStats(id, created, edited, totalHazards, changeThisMonth);
        hazardStatsList.add(hazardStats);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve hazard stats from the database due to a server error: " + e.getMessage());
    }

    return hazardStatsList;
  }

  public HazardStats getById(String id) throws IOException {
    String query = "SELECT id, created, edited, total_hazards, change_this_month FROM HazardStats WHERE id = ?;";
    HazardStats hazardStats;

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");
        long totalHazards = rs.getLong("total_hazards");
        long changeThisMonth = rs.getLong("change_this_month");

        hazardStats = new HazardStats(id, created, edited, totalHazards, changeThisMonth);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve hazard stats from the database due to a server error: " + e.getMessage());
    }

    return hazardStats;
  }

  public void update(HazardStats hazardStats) throws IOException {
    String query = "UPDATE HazardStats SET created = ?, edited = ?, total_hazards = ?, change_this_month = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setDate(1, hazardStats.getCreated());
      pstmt.setDate(2, hazardStats.getEdited());
      pstmt.setLong(3, hazardStats.getTotal_hazards());
      pstmt.setLong(4, hazardStats.getChange_this_month());
      pstmt.setString(5, hazardStats.getId());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not update hazard stats in the database due to a server error: " + e.getMessage());
    }
  }
}
