package com.splitscale.reems;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.core.properties.Property;
import com.splitscale.reems.core.properties.PropertyRequest;
import com.splitscale.reems.repositories.PropertyRepositoryInteractor;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PropertyRepositoryInteractorTest {

  @Mock
  private DatabaseDriver db;

  @Mock
  private Connection conn;

  @Mock
  private PreparedStatement pstmt;

  @Mock
  private ResultSet rs;

  private PropertyRepositoryInteractor propertyRepository;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    propertyRepository = new PropertyRepositoryInteractor(db);
  }

  @Test
  public void testAdd() throws IOException, SQLException {
    String generatedId = "1";
    PropertyRequest propertyRequest = new PropertyRequest("Test Property", null, null, "Test Location", "Test Status",
        generatedId);

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    when(pstmt.getGeneratedKeys()).thenReturn(rs);
    when(rs.next()).thenReturn(true);
    when(rs.getString(1)).thenReturn(generatedId);
    doNothing().when(conn).close();

    String result = propertyRepository.add(propertyRequest);

    verify(pstmt).setString(1, propertyRequest.getName());
    verify(pstmt).setString(2, propertyRequest.getLocation());
    verify(pstmt).setString(3, propertyRequest.getStatus());
    verify(conn).close();
    assertEquals(generatedId, result);
  }

  @Test
  public void testDelete() throws IOException, SQLException {
    String propertyId = "1";

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    doNothing().when(conn).close();

    propertyRepository.delete(propertyId);

    verify(pstmt).setString(1, propertyId);
    verify(pstmt).executeUpdate();
    verify(conn).close();
  }

  @Test
  public void testGetAll() throws IOException, SQLException {
    String propertyId = "1";
    String propertyName = "Test Property";
    String propertyLocation = "Test Location";
    String propertyStatus = "Test Status";

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, false);
    when(rs.getString("id")).thenReturn(propertyId);
    when(rs.getString("name")).thenReturn(propertyName);
    when(rs.getString("location")).thenReturn(propertyLocation);
    when(rs.getString("status")).thenReturn(propertyStatus);
    doNothing().when(conn).close();

    List<Property> result = propertyRepository.getAll();

    verify(pstmt).executeQuery();
    verify(conn).close();
    assertEquals(1, result.size());
    Property property = result.get(0);
    assertEquals(propertyId, property.getId());
    assertEquals(propertyName, property.getName());
    assertEquals(propertyLocation, property.getLocation());
    assertEquals(propertyStatus, property.getStatus());
  }

  @Test
  public void testGetById() throws IOException, SQLException {
    String propertyId = "1";
    String propertyName = "Test Property";
    String propertyLocation = "Test Location";
    String propertyStatus = "Test Status";

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, false);
    when(rs.getString("name")).thenReturn(propertyName);
    when(rs.getString("location")).thenReturn(propertyLocation);
    when(rs.getString("status")).thenReturn(propertyStatus);
    doNothing().when(conn).close();

    Property result = propertyRepository.getById(propertyId);

    verify(pstmt).setString(1, propertyId);
    verify(pstmt).executeQuery();
    verify(conn).close();
    assertEquals(propertyId, result.getId());
    assertEquals(propertyName, result.getName());
    assertEquals(propertyLocation, result.getLocation());
    assertEquals(propertyStatus, result.getStatus());
  }

  @Test
  public void testUpdate() throws IOException, SQLException {
    String propertyId = "1";
    String propertyName = "Test Property";
    String propertyLocation = "Test Location";
    String propertyStatus = "Test Status";

    Property property = new Property(propertyId, null, null, propertyName, propertyLocation, propertyStatus);

    when(db.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);
    doNothing().when(conn).close();

    propertyRepository.update(property);

    verify(pstmt).setString(1, propertyName);
    verify(pstmt).setString(2, propertyLocation);
    verify(pstmt).setString(3, propertyStatus);
    verify(pstmt).setString(4, propertyId);
    verify(pstmt).executeUpdate();
    verify(conn).close();
  }
}
