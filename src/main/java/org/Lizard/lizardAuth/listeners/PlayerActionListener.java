package org.Lizard.lizardAuth.listeners;

import org.Lizard.lizardAuth.managers.AuthenticationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerActionListener implements Listener {

    private final AuthenticationManager authenticationManager;
    private final org.Lizard.lizardAuth.config.Configuration config;

    public PlayerActionListener(AuthenticationManager authenticationManager, org.Lizard.lizardAuth.config.Configuration config) {
        this.authenticationManager = authenticationManager;
        this.config = config;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!authenticationManager.isLoggedIn(player.getUniqueId()) && !authenticationManager.isPendingLicenseCheck(player.getUniqueId())) {
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!authenticationManager.isLoggedIn(player.getUniqueId()) && !authenticationManager.isPendingLicenseCheck(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§cPlease register or login to chat.");
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase();
        if (!authenticationManager.isLoggedIn(player.getUniqueId()) && !authenticationManager.isPendingLicenseCheck(player.getUniqueId())) {
            if (!message.startsWith("/register") && !message.startsWith("/login") && !message.startsWith("/checklic")) {
                event.setCancelled(true);
                player.sendMessage("§cPlease register or login to use this command.");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!authenticationManager.isLoggedIn(player.getUniqueId()) && !authenticationManager.isPendingLicenseCheck(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!authenticationManager.isLoggedIn(player.getUniqueId()) && !authenticationManager.isPendingLicenseCheck(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!authenticationManager.isLoggedIn(player.getUniqueId()) && !authenticationManager.isPendingLicenseCheck(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && !authenticationManager.isLoggedIn(damager.getUniqueId()) && !authenticationManager.isPendingLicenseCheck(damager.getUniqueId())) {
            event.setCancelled(true);
        }
        if (event.getEntity() instanceof Player target && !authenticationManager.isLoggedIn(target.getUniqueId()) && !authenticationManager.isPendingLicenseCheck(target.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}