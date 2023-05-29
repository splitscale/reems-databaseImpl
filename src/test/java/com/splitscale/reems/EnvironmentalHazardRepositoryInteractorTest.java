package com.splitscale.reems;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.core.hazard.environment.EnvironmentalHazard;
import com.splitscale.reems.core.hazard.environment.EnvironmentalHazardRequest;
import com.splitscale.reems.repositories.EnvironmentalHazardRepositoryInteractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EnvironmentalHazardRepositoryInteractorTest {
    private EnvironmentalHazardRepositoryInteractor repository;
    private DatabaseDriver db;

    @BeforeEach
    void setUp() {
        db = mock(DatabaseDriver.class);
        repository = new EnvironmentalHazardRepositoryInteractor(db);
    }

    @Test
    void add_shouldInsertEnvironmentalHazardIntoDatabase() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        EnvironmentalHazardRequest request = new EnvironmentalHazardRequest(null, null, null, null,
                null, null, null, null);

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(statement);
        when(statement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn("generatedId");

        // Act
        String generatedId = repository.add(request);

        // Assert
        assertEquals("generatedId", generatedId);
        verify(connection).close();
    }

    @Test
    void delete_shouldDeleteEnvironmentalHazardFromDatabase() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        // Act
        assertDoesNotThrow(() -> repository.delete("id"));

        // Assert
        verify(connection).close();
    }

    @Test
    void getAll_shouldRetrieveAllEnvironmentalHazardsFromDatabase() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("id")).thenReturn("id");

        // Act
        List<EnvironmentalHazard> environmentalHazards = repository.getAll();

        // Assert
        assertEquals(1, environmentalHazards.size());
        assertEquals("id", environmentalHazards.get(0).getId());
        verify(connection).close();
    }

    @Test
    void getById_shouldRetrieveEnvironmentalHazardFromDatabase() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("id")).thenReturn("id");

        // Act
        EnvironmentalHazard environmentalHazard = repository.getById("id");

        // Assert
        assertNotNull(environmentalHazard);
        assertEquals("id", environmentalHazard.getId());
    }

    @Test
    void getById_shouldReturnNullIfEnvironmentalHazardNotFound() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        EnvironmentalHazard environmentalHazard = repository.getById("id");

        // Assert
        assertNull(environmentalHazard);
        verify(connection).close();
    }

    @Test
    void update_shouldUpdateEnvironmentalHazardInDatabase() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EnvironmentalHazard environmentalHazard = new EnvironmentalHazard("01", null, null, null,
                null, null, null, null);

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        // Act
        assertDoesNotThrow(() -> repository.update(environmentalHazard));

        // Assert
        verify(connection).close();
    }
}
