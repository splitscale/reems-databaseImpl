package com.splitscale.reems;

import com.splitscale.reems.driver.DatabaseDriver;
import com.splitscale.reems.energy.consumption.EnergyConsumption;
import com.splitscale.reems.energy.consumption.EnergyConsumptionRequest;
import com.splitscale.reems.repositories.EnergyConsumptionRepositoryInteractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EnergyConsumptionRepositoryInteractorTest {
    private EnergyConsumptionRepositoryInteractor repository;
    private DatabaseDriver db;

    @BeforeEach
    void setUp() {
        db = mock(DatabaseDriver.class);
        repository = new EnergyConsumptionRepositoryInteractor(db);
    }

    @Test
    void add_shouldInsertEnergyConsumptionIntoDatabase() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        EnergyConsumptionRequest request = new EnergyConsumptionRequest(null, null, null, null, null, null, null, null,
                0);

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
    void delete_shouldDeleteEnergyConsumptionFromDatabase() throws SQLException, IOException {
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
    void getAll_shouldRetrieveAllEnergyConsumptionsFromDatabase() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("id")).thenReturn("id");
        // Set up the mocked ResultSet with necessary values

        // Act
        List<EnergyConsumption> energyConsumptions = repository.getAll();

        // Assert
        assertEquals(1, energyConsumptions.size());
        assertEquals("id", energyConsumptions.get(0).getId());
        verify(connection).close();
    }

    @Test
    void getById_shouldRetrieveEnergyConsumptionFromDatabase() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("id")).thenReturn("id");
        // Set up the mocked ResultSet with necessary values

        // Act
        EnergyConsumption energyConsumption = repository.getById("id");

        // Assert
        assertNotNull(energyConsumption);
        assertEquals("id", energyConsumption.getId());
    }

    @Test
    void getById_shouldReturnNullIfEnergyConsumptionNotFound() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        EnergyConsumption energyConsumption = repository.getById("id");

        // Assert
        assertNull(energyConsumption);
        verify(connection).close();
    }

    @Test
    void update_shouldUpdateEnergyConsumptionInDatabase() throws SQLException, IOException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EnergyConsumption energyConsumption = new EnergyConsumption(null, null, null, null, null, null, null, null, 0);

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        // Act
        assertDoesNotThrow(() -> repository.update(energyConsumption));

        // Assert
        verify(connection).close();
    }
}
