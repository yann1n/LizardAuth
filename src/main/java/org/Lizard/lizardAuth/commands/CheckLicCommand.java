package org.Lizard.lizardAuth.commands;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.Lizard.lizardAuth.managers.AuthenticationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CheckLicCommand implements CommandExecutor {

    private final BukkitAudiences adventure;
    private final AuthenticationManager authenticationManager;

    public CheckLicCommand(BukkitAudiences adventure, AuthenticationManager authenticationManager) {
        this.adventure = adventure;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            adventure.sender(sender).sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }

        if (authenticationManager.isPendingLicenseCheck(player.getUniqueId())) {
            adventure.player(player).sendMessage(Component.text("License check is already pending. Please re-login.", NamedTextColor.YELLOW));
            return true;
        }

        authenticationManager.markForLicenseCheck(player.getUniqueId());
        adventure.player(player).sendMessage(Component.text("You have been marked for license verification. Please disconnect and reconnect to the server.", NamedTextColor.GREEN));
        return true;
    }
}