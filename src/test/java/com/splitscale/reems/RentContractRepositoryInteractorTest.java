package com.splitscale.reems;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.rentContract.RentContract;
import com.splitscale.reems.rentContract.RentContractRequest;
import com.splitscale.reems.repositories.RentContractRepositoryInteractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RentContractRepositoryInteractorTest {

  @Mock
  private DatabaseDriver db;

  @Mock
  private Connection conn;

  @Mock
  private PreparedStatement pstmt;

  @Mock
  private ResultSet rs;

  private RentContractRepositoryInteractor rentContractRepository;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    rentContractRepository = new RentContractRepositoryInteractor(db);
  }

  @Test
  public void testAdd() throws IOException, SQLException {
    String generatedId = "1";
    RentContractRequest rentContractRequest = new RentContractRequest("tenant_info_id", "property_id", generatedId);

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    when(pstmt.getGeneratedKeys()).thenReturn(rs);
    when(rs.next()).thenReturn(true);
    when(rs.getString(1)).thenReturn(generatedId);
    doNothing().when(conn).close();

    String result = rentContractRepository.add(rentContractRequest);

    verify(pstmt).setString(1, rentContractRequest.getTenantInfoId());
    verify(pstmt).setString(2, rentContractRequest.getPropertyId());
    verify(conn).close();
    assertEquals(generatedId, result);
  }

  @Test
  public void testDelete() throws IOException, SQLException {
    String rentContractId = "1";

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    doNothing().when(conn).close();

    rentContractRepository.delete(rentContractId);

    verify(pstmt).setString(1, rentContractId);
    verify(pstmt).executeUpdate();
    verify(conn).close();
  }

  @Test
  public void testGetAll() throws IOException, SQLException {
    String rentContractId = "1";
    String tenantInfoId = "tenant_info_id";
    String propertyId = "property_id";

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, false);
    when(rs.getString("id")).thenReturn(rentContractId);
    when(rs.getString("tenant_info_id")).thenReturn(tenantInfoId);
    when(rs.getString("property_id")).thenReturn(propertyId);
    doNothing().when(conn).close();

    List<RentContract> result = rentContractRepository.getAll();

    verify(pstmt).executeQuery();
    verify(conn).close();
    assertEquals(1, result.size());
    RentContract rentContract = result.get(0);
    assertEquals(rentContractId, rentContract.getId());
    assertEquals(tenantInfoId, rentContract.getTenantInfoId());
    assertEquals(propertyId, rentContract.getPropertyId());
  }

  @Test
  public void testGetById() throws IOException, SQLException {
    String rentContractId = "1";
    String tenantInfoId = "tenant_info_id";
    String propertyId = "property_id";

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, false);
    when(rs.getString("tenant_info_id")).thenReturn(tenantInfoId);
    when(rs.getString("property_id")).thenReturn(propertyId);
    doNothing().when(conn).close();

    RentContract result = rentContractRepository.getById(rentContractId);

    verify(pstmt).setString(1, rentContractId);
    verify(pstmt).executeQuery();
    verify(conn).close();
    assertEquals(rentContractId, result.getId());
    assertEquals(tenantInfoId, result.getTenantInfoId());
    assertEquals(propertyId, result.getPropertyId());
  }

  @Test
  public void testUpdate() throws IOException, SQLException {
    RentContractRequest rentContractRequest = new RentContractRequest("tenant_info_id", "property_id", null);
    rentContractRequest.setId("1");

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    doNothing().when(conn).close();

    rentContractRepository.update(rentContractRequest);

    verify(pstmt).setString(1, rentContractRequest.getTenantInfoId());
    verify(pstmt).setString(2, rentContractRequest.getPropertyId());
    verify(pstmt).setString(3, rentContractRequest.getId());
    verify(pstmt).executeUpdate();
    verify(conn).close();
  }
}
