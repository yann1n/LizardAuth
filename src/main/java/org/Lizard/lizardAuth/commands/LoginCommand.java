package org.Lizard.lizardAuth.commands;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.Lizard.lizardAuth.config.Configuration;
import org.Lizard.lizardAuth.managers.AuthenticationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LoginCommand implements CommandExecutor {

    private final AuthenticationManager authenticationManager;
    private final BukkitAudiences adventure;
    private final Configuration config;

    public LoginCommand(AuthenticationManager authenticationManager, BukkitAudiences adventure, Configuration config) {
        this.authenticationManager = authenticationManager;
        this.adventure = adventure;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            adventure.sender(sender).sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }

        if (args.length != 1) {
            adventure.player(player).sendMessage(Component.text("Usage: /login <password>", NamedTextColor.RED));
            return true;
        }

        if (authenticationManager.isLoggedIn(player.getUniqueId())) {
            adventure.player(player).sendMessage(Component.text("You are already logged in.", NamedTextColor.YELLOW));
            return true;
        }

        String password = args[0];
        if (authenticationManager.login(player, password)) {
            adventure.player(player).sendMessage(Component.text("Successfully logged in!", NamedTextColor.GREEN));
            authenticationManager.setLoggedIn(player.getUniqueId(), true);
            authenticationManager.removeUnauthenticatedActions(player.getUniqueId());
        } else {
            adventure.player(player).sendMessage(Component.text("Invalid password.", NamedTextColor.RED));
        }

        return true;
    }
}