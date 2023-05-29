package com.splitscale.reems.repositories;

import java.io.IOException;
import java.util.List;

import com.splitscale.reems.expenseStats.ExpenseStats;
import com.splitscale.reems.expenseStats.ExpenseStatsRequest;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.splitscale.reems.driver.DatabaseDriver;

public class ExpenseStatsRepositoryInteractor implements ExpenseStatsRepository {

  private DatabaseDriver db;

  public ExpenseStatsRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  @Override
  public String add(ExpenseStatsRequest expenseStatsRequest) throws IOException {
    String query = "INSERT INTO ExpenseStats (created, edited, total_expenses, change_this_month, expense_this_month) VALUES (?, ?, ?, ?, ?);";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setDate(1, expenseStatsRequest.getCreated());
      pstmt.setDate(2, expenseStatsRequest.getEdited());
      pstmt.setString(3, expenseStatsRequest.getTotalExpenses());
      pstmt.setString(4, expenseStatsRequest.getChangeThisMonth());
      pstmt.setString(5, expenseStatsRequest.getExpenseThisMonth());

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException("Could not add expense stats to the database due to a server error: " + e.getMessage());
    }
  }

  @Override
  public void deleteAll() throws IOException {
    String query = "DELETE FROM ExpenseStats;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not delete all expense stats from the database due to a server error: " + e.getMessage());
    }
  }

  @Override
  public void deleteById(String id) throws IOException {
    String query = "DELETE FROM ExpenseStats WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not delete expense stats from the database due to a server error: " + e.getMessage());
    }
  }

  @Override
  public List<ExpenseStats> getAll() throws IOException {
    List<ExpenseStats> expenseStatsList = new ArrayList<>();

    String query = "SELECT id, created, edited, total_expenses, change_this_month, expense_this_month FROM ExpenseStats;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");
        String totalExpenses = rs.getString("total_expenses");
        String changeThisMonth = rs.getString("change_this_month");
        String expenseThisMonth = rs.getString("expense_this_month");

        ExpenseStats expenseStats = new ExpenseStats(id, created, edited, totalExpenses, changeThisMonth,
            expenseThisMonth);
        expenseStatsList.add(expenseStats);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve expense stats from the database due to a server error: " + e.getMessage());
    }

    return expenseStatsList;
  }

  @Override
  public ExpenseStats getById(String id) throws IOException {
    String query = "SELECT id, created, edited, total_expenses, change_this_month, expense_this_month FROM ExpenseStats WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        Date created = rs.getDate("created");
        Date edited = rs.getDate("edited");
        String totalExpenses = rs.getString("total_expenses");
        String changeThisMonth = rs.getString("change_this_month");
        String expenseThisMonth = rs.getString("expense_this_month");

        ExpenseStats expenseStats = new ExpenseStats(id, created, edited, totalExpenses, changeThisMonth,
            expenseThisMonth);
        conn.close();
        return expenseStats;
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException(
          "Could not retrieve expense stats from the database due to a server error: " + e.getMessage());
    }
  }

  @Override
  public void update(ExpenseStats expenseStats) throws IOException {
    String query = "UPDATE ExpenseStats SET created = ?, edited = ?, total_expenses = ?, change_this_month = ?, expense_this_month = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setDate(1, expenseStats.getCreated());
      pstmt.setDate(2, expenseStats.getEdited());
      pstmt.setString(3, expenseStats.getTotalExpenses());
      pstmt.setString(4, expenseStats.getChangeThisMonth());
      pstmt.setString(5, expenseStats.getExpenseThisMonth());
      pstmt.setString(6, expenseStats.getId());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not update expense stats in the database due to a server error: " + e.getMessage());
    }
  }
}
