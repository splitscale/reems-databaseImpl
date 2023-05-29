package com.splitscale.reems.repositories;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.expenses.Expense;
import com.splitscale.reems.expenses.ExpenseRequest;

public class ExpenseRepositoryInteractor implements ExpenseRepository {
  private DatabaseDriver db;

  public ExpenseRepositoryInteractor(DatabaseDriver db) {
    this.db = db;
  }

  public String add(ExpenseRequest expenseRequest) throws IOException {
    String query = "INSERT INTO Expense (created, modified, value, unit) VALUES (?, ?, ?, ?);";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setDate(1, expenseRequest.getCreated());
      pstmt.setDate(2, expenseRequest.getModified());
      pstmt.setString(3, expenseRequest.getValue());
      pstmt.setString(4, expenseRequest.getUnit());

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      String generatedId = null;
      if (rs.next()) {
        generatedId = rs.getString(1);
      }

      conn.close();
      return generatedId;
    } catch (SQLException e) {
      throw new IOException("Could not add expense to the database due to a server error: " + e.getMessage());
    }
  }

  public void deleteAll() throws IOException {
    String query = "DELETE FROM Expense;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not delete all expenses from the database due to a server error: " + e.getMessage());
    }
  }

  public void deleteById(String id) throws IOException {
    String query = "DELETE FROM Expense WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not delete expense from the database due to a server error: " + e.getMessage());
    }
  }

  public List<Expense> getAll() throws IOException {
    List<Expense> expenseList = new ArrayList<>();

    String query = "SELECT id, created, modified, value, unit FROM Expense;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String id = rs.getString("id");
        Date created = rs.getDate("created");
        Date modified = rs.getDate("modified");
        String value = rs.getString("value");
        String unit = rs.getString("unit");

        Expense expense = new Expense(id, created, modified, value, unit);
        expenseList.add(expense);
      }

      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not retrieve expenses from the database due to a server error: " + e.getMessage());
    }

    return expenseList;
  }

  public Expense getById(String id) throws IOException {
    String query = "SELECT id, created, modified, value, unit FROM Expense WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        Date created = rs.getDate("created");
        Date modified = rs.getDate("modified");
        String value = rs.getString("value");
        String unit = rs.getString("unit");

        Expense expense = new Expense(id, created, modified, value, unit);
        conn.close();
        return expense;
      } else {
        conn.close();
        return null;
      }
    } catch (SQLException e) {
      throw new IOException("Could not retrieve expense from the database due to a server error: " + e.getMessage());
    }
  }

  public void update(Expense expense) throws IOException {
    String query = "UPDATE Expense SET created = ?, modified = ?, value = ?, unit = ? WHERE id = ?;";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(3, expense.getValue());
      pstmt.setString(4, expense.getUnit());
      pstmt.setString(5, expense.getId());

      pstmt.executeUpdate();
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Could not update expense in the database due to a server error: " + e.getMessage());
    }
  }
}
