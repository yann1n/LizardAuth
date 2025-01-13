package org.Lizard.lizardAuth;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.Lizard.lizardAuth.commands.CheckLicCommand;
import org.Lizard.lizardAuth.commands.LoginCommand;
import org.Lizard.lizardAuth.commands.RegisterCommand;
import org.Lizard.lizardAuth.config.Configuration;
import org.Lizard.lizardAuth.listeners.PlayerActionListener;
import org.Lizard.lizardAuth.listeners.PlayerJoinListener;
import org.Lizard.lizardAuth.managers.AuthenticationManager;
import org.Lizard.lizardAuth.managers.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;

public class LizardAuth extends JavaPlugin {

    private BukkitAudiences adventure;
    private Configuration config;
    private DatabaseManager databaseManager;
    private AuthenticationManager authenticationManager;

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);
        this.config = new Configuration(this);
        this.config.load();

        // Initialize Database
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + getDataFolder() + File.separator + "lizardauth.db");
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setMaxLifetime(60000); // 60 seconds
        hikariConfig.setIdleTimeout(45000); // 45 seconds
        hikariConfig.setMaximumPoolSize(10);

        try {
            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            this.databaseManager = new DatabaseManager(dataSource, this);
            this.databaseManager.initializeDatabase();
        } catch (SQLException e) {
            getLogger().severe("Could not connect to the database! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.authenticationManager = new AuthenticationManager(this, databaseManager);

        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, adventure, authenticationManager, config), this);
        getServer().getPluginManager().registerEvents(new PlayerActionListener(authenticationManager, config), this);

        // Register Commands
        Objects.requireNonNull(getCommand("register")).setExecutor(new RegisterCommand(authenticationManager, adventure, config));
        Objects.requireNonNull(getCommand("login")).setExecutor(new LoginCommand(authenticationManager, adventure, config));
        Objects.requireNonNull(getCommand("checklic")).setExecutor(new CheckLicCommand(adventure, authenticationManager));

        getLogger().info("LizardAuth has been enabled!");
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
        getLogger().info("LizardAuth has been disabled!");
    }

    public BukkitAudiences adventure() {
        return adventure;
    }

    public Configuration getConfiguration() {
        return config;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }
}