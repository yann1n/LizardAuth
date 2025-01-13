package org.Lizard.lizardAuth.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    private int commandTimeout;
    private int minimumPasswordLength;
    private boolean licenseCheckEnabled;

    public Configuration(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public void load() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        this.commandTimeout = config.getInt("command-timeout", 30);
        this.minimumPasswordLength = config.getInt("minimum-password-length", 6);
        this.licenseCheckEnabled = config.getBoolean("license-check-enabled", true);
    }

    public int getCommandTimeout() {
        return commandTimeout;
    }

    public int getMinimumPasswordLength() {
        return minimumPasswordLength;
    }

    public boolean isLicenseCheckEnabled() {
        return licenseCheckEnabled;
    }
}