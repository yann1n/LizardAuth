package org.Lizard.lizardAuth.managers;

import org.Lizard.lizardAuth.LizardAuth;
import org.Lizard.lizardAuth.models.PlayerData;
import org.bukkit.entity.Player;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationManager {

    private final LizardAuth plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, Boolean> loginStatus = new ConcurrentHashMap<>();
    private final Set<UUID> pendingLicenseChecks = ConcurrentHashMap.newKeySet();
    private final Set<UUID> awaitingAuthentication = ConcurrentHashMap.newKeySet();
    private final Argon2PasswordEncoder passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public AuthenticationManager(LizardAuth plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        loadAllPlayerData();
    }

    private void loadAllPlayerData() {
        databaseManager.loadAllPlayers();
    }

    public boolean register(Player player, String password) {
        if (databaseManager.loadPlayer(player.getName()) != null) {
            return false; // Already registered
        }
        String salt = UUID.randomUUID().toString();
        String hashedPassword = passwordEncoder.encode(password + salt);
        PlayerData playerData = new PlayerData(player.getName(), hashedPassword, salt, false);
        return databaseManager.savePlayer(playerData);
    }

    public boolean login(Player player, String password) {
        PlayerData playerData = databaseManager.loadPlayer(player.getName());
        if (playerData == null) {
            return false; // Not registered
        }
        return passwordEncoder.matches(password + playerData.getSalt(), playerData.getHashedPassword());
    }

    public boolean isLoggedIn(UUID playerId) {
        return loginStatus.getOrDefault(playerId, false);
    }

    public void setLoggedIn(UUID playerId, boolean loggedIn) {
        loginStatus.put(playerId, loggedIn);
    }

    public boolean isRegistered(String playerName) {
        return databaseManager.loadPlayer(playerName) != null;
    }

    public void markForLicenseCheck(UUID playerId) {
        pendingLicenseChecks.add(playerId);
    }

    public boolean isPendingLicenseCheck(UUID playerId) {
        return pendingLicenseChecks.contains(playerId);
    }

    public void removeMarkedForLicenseCheck(UUID playerId) {
        pendingLicenseChecks.remove(playerId);
    }

    public void setLicenseStatus(String playerName, boolean isLicensed) {
        PlayerData playerData = databaseManager.loadPlayer(playerName);
        if (playerData != null) {
            playerData.setLicensed(isLicensed);
            databaseManager.savePlayer(playerData);
        }
    }

    public boolean isLicensed(String playerName) {
        PlayerData playerData = databaseManager.loadPlayer(playerName);
        return playerData != null && playerData.isLicensed();
    }

    public void markForAuthentication(UUID playerId) {
        awaitingAuthentication.add(playerId);
    }

    public boolean isMarkedForAuthentication(UUID playerId) {
        return awaitingAuthentication.contains(playerId);
    }

    public void removeUnauthenticatedActions(UUID playerId) {
        awaitingAuthentication.remove(playerId);
    }
}