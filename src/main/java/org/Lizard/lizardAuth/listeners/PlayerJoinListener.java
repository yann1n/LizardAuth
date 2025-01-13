package org.Lizard.lizardAuth.listeners;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.Lizard.lizardAuth.LizardAuth;
import org.Lizard.lizardAuth.config.Configuration;
import org.Lizard.lizardAuth.managers.AuthenticationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class PlayerJoinListener implements Listener {

    private final BukkitAudiences adventure;
    private final AuthenticationManager authenticationManager;
    private final Configuration config;
    private final LizardAuth plugin;

    public PlayerJoinListener(LizardAuth plugin, BukkitAudiences adventure, AuthenticationManager authenticationManager, Configuration config) {
        this.plugin = plugin;
        this.adventure = adventure;
        this.authenticationManager = authenticationManager;
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (authenticationManager.isPendingLicenseCheck(player.getUniqueId())) {
            // Let the player through, as they are trying to verify their license
            authenticationManager.removeMarkedForLicenseCheck(player.getUniqueId());
            adventure.player(player).sendMessage(Component.text("License verification successful!", NamedTextColor.GREEN));
            authenticationManager.setLicenseStatus(player.getName(), true);
            authenticationManager.setLoggedIn(player.getUniqueId(), true);
            return;
        }

        if (config.isLicenseCheckEnabled() && authenticationManager.isLicensed(player.getName())) {
            authenticationManager.setLoggedIn(player.getUniqueId(), true);
            return; // Licensed players skip auth
        }

        authenticationManager.markForAuthentication(player.getUniqueId());
        adventure.player(player).sendMessage(Component.text("This server requires authentication. Please register with /register <password> <confirm_password> or login with /login <password>.", NamedTextColor.YELLOW));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (authenticationManager.isMarkedForAuthentication(player.getUniqueId())) {
                    player.kick(Component.text("Authentication timed out.", NamedTextColor.RED));
                }
            }
        }.runTaskLaterAsynchronously(plugin, TimeUnit.SECONDS.toSeconds(config.getCommandTimeout()) * 20);
    }
}