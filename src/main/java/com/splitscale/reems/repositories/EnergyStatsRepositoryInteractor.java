package com.splitscale.reems.repositories;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.energyStats.EnergyStats;
import com.splitscale.reems.energyStats.EnergyStatsRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnergyStatsRepositoryInteractor implements EnergyStatsRepository {
  private DatabaseDriver db;

  public EnergyStatsRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  public String add(EnergyStatsRequest energyStatsRequest) throws IOException {
    String query = "INSERT INTO EnergyStats (created, edited, total_consumption, change_this_month, consumed_this_month) VALUES (?, ?, ?, ?, ?);";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setDate(1, energyStatsRequest.getCreated());
      pstmt.setDate(2, energyStatsRequest.getEdited());
      pstmt.setString(3, energyStatsRequest.getTotalConsumption());
      pstmt.setString(4, energyStatsRequest.getChangeThisMonth());
      pstmt.setString(5, energyStatsRequest.getConsumedThisMonth());

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException("Could not add energy stats to the database due to a server error: " + e.getMessage());
    }
  }

  public void deleteAll() throws IOException {
    String query = "DELETE FROM EnergyStats;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not delete energy stats from the database due to a server error: " + e.getMessage());
    }
  }

  public void deleteById(String id) throws IOException {
    String query = "DELETE FROM EnergyStats WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not delete energy stats from the database due to a server error: " + e.getMessage());
    }
  }

  public List<EnergyStats> getAll() throws IOException {
    List<EnergyStats> energyStatsList = new ArrayList<>();

    String query = "SELECT id, created, edited, total_consumption, change_this_month, consumed_this_month FROM EnergyStats;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");
        String totalConsumption = rs.getString("total_consumption");
        String changeThisMonth = rs.getString("change_this_month");
        String consumedThisMonth = rs.getString("consumed_this_month");

        EnergyStats energyStats = new EnergyStats(id, created, edited, totalConsumption, changeThisMonth,
            consumedThisMonth);
        energyStatsList.add(energyStats);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve energy stats from the database due to a server error: " + e.getMessage());
    }

    return energyStatsList;
  }

  public EnergyStats getById(String id) throws IOException {
    String query = "SELECT id, created, edited, total_consumption, change_this_month, consumed_this_month FROM EnergyStats WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");
        String totalConsumption = rs.getString("total_consumption");
        String changeThisMonth = rs.getString("change_this_month");
        String consumedThisMonth = rs.getString("consumed_this_month");

        EnergyStats energyStats = new EnergyStats(id, created, edited, totalConsumption, changeThisMonth,
            consumedThisMonth);
        conn.close();
        return energyStats;
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve energy stats from the database due to a server error: " + e.getMessage());
    }
  }

  public void update(EnergyStats energyStats) throws IOException {
    String query = "UPDATE EnergyStats SET created = ?, edited = ?, total_consumption = ?, change_this_month = ?, consumed_this_month = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setDate(1, energyStats.getCreated());
      pstmt.setDate(2, energyStats.getEdited());
      pstmt.setString(3, energyStats.getTotalConsumption());
      pstmt.setString(4, energyStats.getChangeThisMonth());
      pstmt.setString(5, energyStats.getConsumedThisMonth());
      pstmt.setString(6, energyStats.getId());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not update energy stats in the database due to a server error: " + e.getMessage());
    }
  }
}
