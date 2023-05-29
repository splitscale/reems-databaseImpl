package com.splitscale.reems;

import com.splitscale.reems.core.tenantinfo.TenantInfo;
import com.splitscale.reems.core.tenantinfo.TenantInfoRequest;
import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.repositories.TenantRepositoryInteractor;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantRepositoryInteractorTest {
  @Mock
  private DatabaseDriver db;

  @Mock
  private Connection conn;

  @Mock
  private PreparedStatement pstmt;

  @Mock
  private ResultSet rs;

  private TenantRepositoryInteractor tenantRepository;

  @Before
  public void setup() {
    MockitoAnnotations.openMocks(this);
    tenantRepository = new TenantRepositoryInteractor(db);
  }

  @Test
  public void testAdd() throws IOException, SQLException {
    String generatedId = "1";
    TenantInfoRequest tenantInfoRequest = new TenantInfoRequest("Test Tenant", generatedId, Date.valueOf("2023-05-28"),
        Date.valueOf("2023-05-28"));

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    when(pstmt.getGeneratedKeys()).thenReturn(rs);
    when(rs.next()).thenReturn(true);
    when(rs.getString(1)).thenReturn(generatedId);
    doNothing().when(conn).close();

    String result = tenantRepository.add(tenantInfoRequest);

    verify(pstmt).setString(1, tenantInfoRequest.getTenantName());
    verify(pstmt).setDate(2, tenantInfoRequest.getCreated());
    verify(pstmt).setDate(3, tenantInfoRequest.getEdited());
    verify(conn).close();
    assertEquals(generatedId, result);
  }

  @Test
  public void testDelete() throws IOException, SQLException {
    String tenantId = "1";

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    doNothing().when(conn).close();

    tenantRepository.delete(tenantId);

    verify(pstmt).setString(1, tenantId);
    verify(pstmt).executeUpdate();
    verify(conn).close();
  }

  @Test
  public void testGetAll() throws IOException, SQLException {
    String tenantId = "1";
    String tenantName = "Test Tenant";
    Date createdDate = Date.valueOf("2023-05-28");
    Date editedDate = Date.valueOf("2023-05-28");

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, false);
    when(rs.getString("id")).thenReturn(tenantId);
    when(rs.getString("tenant_name")).thenReturn(tenantName);
    when(rs.getDate("created")).thenReturn(createdDate);
    when(rs.getDate("edited")).thenReturn(editedDate);
    doNothing().when(conn).close();

    List<TenantInfo> result = tenantRepository.getAll();

    verify(pstmt).executeQuery();
    verify(conn).close();
    assertEquals(1, result.size());
    TenantInfo tenantInfo = result.get(0);
    assertEquals(tenantId, tenantInfo.getId());
    assertEquals(tenantName, tenantInfo.getTenantName());
    assertEquals(createdDate, tenantInfo.getCreated());
    assertEquals(editedDate, tenantInfo.getEdited());
  }

  @Test
  public void testGetById() throws IOException, SQLException {
    String tenantId = "1";
    String tenantName = "Test Tenant";
    Date createdDate = Date.valueOf("2023-05-28");
    Date editedDate = Date.valueOf("2023-05-28");

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, false);
    when(rs.getString("tenant_name")).thenReturn(tenantName);
    when(rs.getDate("created")).thenReturn(createdDate);
    when(rs.getDate("edited")).thenReturn(editedDate);
    doNothing().when(conn).close();

    TenantInfo result = tenantRepository.getById(tenantId);

    verify(pstmt).setString(1, tenantId);
    verify(pstmt).executeQuery();
    verify(conn).close();
    assertEquals(tenantId, result.getId());
    assertEquals(tenantName, result.getTenantName());
    assertEquals(createdDate, result.getCreated());
    assertEquals(editedDate, result.getEdited());
  }

  @Test
  public void testUpdate() throws IOException, SQLException {
    String tenantId = "1";
    String tenantName = "Test Tenant";
    Date createdDate = Date.valueOf("2023-05-28");
    Date editedDate = Date.valueOf("2023-05-28");

    TenantInfo tenantInfo = new TenantInfo(tenantId, tenantName, createdDate, editedDate);

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    doNothing().when(conn).close();

    tenantRepository.update(tenantInfo);

    verify(pstmt).setString(1, tenantName);
    verify(pstmt).setDate(2, createdDate);
    verify(pstmt).setDate(3, editedDate);
    verify(pstmt).setString(4, tenantId);
    verify(pstmt).executeUpdate();
    verify(conn).close();
  }
}
