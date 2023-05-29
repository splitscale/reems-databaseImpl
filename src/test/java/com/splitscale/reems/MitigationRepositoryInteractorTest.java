package com.splitscale.reems;

import com.splitscale.reems.core.mitigation.Mitigation;
import com.splitscale.reems.core.mitigation.MitigationRequest;
import com.splitscale.reems.repositories.MitigationRepositoryInteractor;
import com.splitscale.reems.driver.DatabaseDriver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MitigationRepositoryInteractorTest {
  @Mock
  private DatabaseDriver db;

  @Mock
  private Connection conn;

  @Mock
  private PreparedStatement pstmt;

  @Mock
  private ResultSet rs;

  private MitigationRepositoryInteractor mitigationRepository;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mitigationRepository = new MitigationRepositoryInteractor(db);
  }

  @Test
  public void testAdd() throws IOException, SQLException {
    String generatedId = "1";
    MitigationRequest mitigationRequest = new MitigationRequest(
        "expenseId", generatedId, Date.valueOf("2023-05-28"), Date.valueOf("2023-05-28"),
        "Title", "Description", "10.0", "Status");

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    when(pstmt.getGeneratedKeys()).thenReturn(rs);
    when(rs.next()).thenReturn(true);
    when(rs.getString(1)).thenReturn(generatedId);
    doNothing().when(conn).close();

    String result = mitigationRepository.add(mitigationRequest);

    verify(pstmt).setString(1, mitigationRequest.getExpenseId());
    verify(pstmt).setDate(2, mitigationRequest.getCreated());
    verify(pstmt).setDate(3, mitigationRequest.getEdited());
    verify(pstmt).setString(4, mitigationRequest.getTitle());
    verify(pstmt).setString(5, mitigationRequest.getDescription());
    verify(pstmt).setString(6, mitigationRequest.getCost());
    verify(pstmt).setString(7, mitigationRequest.getStatus());
    verify(conn).close();
    assertEquals(generatedId, result);
  }

  @Test
  public void testDelete() throws IOException, SQLException {
    String mitigationId = "1";

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    doNothing().when(conn).close();

    mitigationRepository.delete(mitigationId);

    verify(pstmt).setString(1, mitigationId);
    verify(pstmt).executeUpdate();
    verify(conn).close();
  }

  @Test
  public void testGetAll() throws IOException, SQLException {
    String mitigationId = "1";
    String expenseId = "expenseId";
    Date createdDate = Date.valueOf("2023-05-28");
    Date editedDate = Date.valueOf("2023-05-28");
    String title = "Title";
    String description = "Description";
    Double cost = 10.0;
    String status = "Status";

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, false);
    when(rs.getString("id")).thenReturn(mitigationId);
    when(rs.getString("expense_id")).thenReturn(expenseId);
    when(rs.getDate("created")).thenReturn(createdDate);
    when(rs.getDate("edited")).thenReturn(editedDate);
    when(rs.getString("title")).thenReturn(title);
    when(rs.getString("description")).thenReturn(description);
    when(rs.getDouble("cost")).thenReturn(cost);
    when(rs.getString("status")).thenReturn(status);
    doNothing().when(conn).close();

    List<Mitigation> result = mitigationRepository.getAll();

    verify(pstmt).executeQuery();
    verify(conn).close();
    assertEquals(1, result.size());
    Mitigation mitigation = result.get(0);
    assertEquals(mitigationId, mitigation.getId());
    assertEquals(expenseId, mitigation.getExpenseId());
    assertEquals(createdDate, mitigation.getCreated());
    assertEquals(editedDate, mitigation.getEdited());
    assertEquals(title, mitigation.getTitle());
    assertEquals(description, mitigation.getDescription());
    assertEquals(cost, mitigation.getCost());
    assertEquals(status, mitigation.getStatus());
  }

  @Test
  public void testGetById() throws IOException, SQLException {
    String mitigationId = "1";
    String expenseId = "expenseId";
    Date createdDate = Date.valueOf("2023-05-28");
    Date editedDate = Date.valueOf("2023-05-28");
    String title = "Title";
    String description = "Description";
    Double cost = 10.0;
    String status = "Status";

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, false);
    when(rs.getString("expense_id")).thenReturn(expenseId);
    when(rs.getDate("created")).thenReturn(createdDate);
    when(rs.getDate("edited")).thenReturn(editedDate);
    when(rs.getString("title")).thenReturn(title);
    when(rs.getString("description")).thenReturn(description);
    when(rs.getDouble("cost")).thenReturn(cost);
    when(rs.getString("status")).thenReturn(status);
    doNothing().when(conn).close();

    Mitigation result = mitigationRepository.getById(mitigationId);

    verify(pstmt).setString(1, mitigationId);
    verify(pstmt).executeQuery();
    verify(conn).close();
    assertEquals(mitigationId, result.getId());
    assertEquals(expenseId, result.getExpenseId());
    assertEquals(createdDate, result.getCreated());
    assertEquals(editedDate, result.getEdited());
    assertEquals(title, result.getTitle());
    assertEquals(description, result.getDescription());
    assertEquals(cost, result.getCost());
    assertEquals(status, result.getStatus());
  }

  @Test
  public void testUpdate() throws IOException, SQLException {
    String mitigationId = "1";
    String expenseId = "expenseId";
    Date createdDate = Date.valueOf("2023-05-28");
    Date editedDate = Date.valueOf("2023-05-28");
    String title = "Title";
    String description = "Description";
    Double cost = 10.0;
    String status = "Status";

    Mitigation mitigation = new Mitigation(
        mitigationId, expenseId, createdDate, editedDate, title, description, cost, status);

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    doNothing().when(conn).close();

    mitigationRepository.update(mitigation);

    verify(pstmt).setString(1, expenseId);
    verify(pstmt).setString(4, title);
    verify(pstmt).setString(5, description);
    verify(pstmt).setDouble(6, cost);
    verify(pstmt).setString(7, status);
    verify(pstmt).setString(8, mitigationId);
    verify(pstmt).executeUpdate();
    verify(conn).close();
  }
}
