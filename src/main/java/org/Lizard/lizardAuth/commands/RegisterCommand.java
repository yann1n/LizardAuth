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

public class RegisterCommand implements CommandExecutor {

    private final AuthenticationManager authenticationManager;
    private final BukkitAudiences adventure;
    private final Configuration config;

    public RegisterCommand(AuthenticationManager authenticationManager, BukkitAudiences adventure, Configuration config) {
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

        if (args.length != 2) {
            adventure.player(player).sendMessage(Component.text("Usage: /register <password> <confirm_password>", NamedTextColor.RED));
            return true;
        }

        if (authenticationManager.isRegistered(player.getName())) {
            adventure.player(player).sendMessage(Component.text("You are already registered. Use /login <password>.", NamedTextColor.YELLOW));
            return true;
        }

        String password = args[0];
        String confirmPassword = args[1];

        if (!password.equals(confirmPassword)) {
            adventure.player(player).sendMessage(Component.text("Passwords do not match.", NamedTextColor.RED));
            return true;
        }

        if (password.length() < config.getMinimumPasswordLength()) {
            adventure.player(player).sendMessage(Component.text("Password must be at least " + config.getMinimumPasswordLength() + " characters long.", NamedTextColor.RED));
            return true;
        }

        if (authenticationManager.register(player, password)) {
            adventure.player(player).sendMessage(Component.text("Successfully registered! Please use /login <password> to log in.", NamedTextColor.GREEN));
        } else {
            adventure.player(player).sendMessage(Component.text("Failed to register. Please try again.", NamedTextColor.RED));
        }

        return true;
    }
}