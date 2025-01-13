package org.Lizard.lizardAuth.managers;

import com.zaxxer.hikari.HikariDataSource;
import org.Lizard.lizardAuth.LizardAuth;
import org.Lizard.lizardAuth.models.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DatabaseManager {

    private final HikariDataSource dataSource;
    private final LizardAuth plugin;
    private final Map<String, PlayerData> cachedPlayers = new HashMap<>();

    public DatabaseManager(HikariDataSource dataSource, LizardAuth plugin) {
        this.dataSource = dataSource;
        this.plugin = plugin;
    }

    public void initializeDatabase() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS players (" +
                             "username VARCHAR(255) PRIMARY KEY," +
                             "hashed_password VARCHAR(255) NOT NULL," +
                             "salt VARCHAR(255) NOT NULL," +
                             "is_licensed BOOLEAN NOT NULL)"
             )) {
            statement.execute();
        }
    }

    public PlayerData loadPlayer(String username) {
        if (cachedPlayers.containsKey(username)) {
            return cachedPlayers.get(username);
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM players WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                PlayerData playerData = new PlayerData(
                        resultSet.getString("username"),
                        resultSet.getString("hashed_password"),
                        resultSet.getString("salt"),
                        resultSet.getBoolean("is_licensed")
                );
                cachedPlayers.put(username, playerData);
                return playerData;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error loading player data for " + username, e);
        }
        return null;
    }

    public void loadAllPlayers() {
        cachedPlayers.clear();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM players")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                PlayerData playerData = new PlayerData(
                        resultSet.getString("username"),
                        resultSet.getString("hashed_password"),
                        resultSet.getString("salt"),
                        resultSet.getBoolean("is_licensed")
                );
                cachedPlayers.put(playerData.getUsername(), playerData);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error loading all player data", e);
        }
    }

    public boolean savePlayer(PlayerData playerData) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT OR REPLACE INTO players (username, hashed_password, salt, is_licensed) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, playerData.getUsername());
            statement.setString(2, playerData.getHashedPassword());
            statement.setString(3, playerData.getSalt());
            statement.setBoolean(4, playerData.isLicensed());
            cachedPlayers.put(playerData.getUsername(), playerData);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error saving player data for " + playerData.getUsername(), e);
            return false;
        }
    }

    public void shutdown() {
        dataSource.close();
    }
}