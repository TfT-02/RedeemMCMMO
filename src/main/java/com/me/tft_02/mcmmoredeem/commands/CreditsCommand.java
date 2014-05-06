package com.me.tft_02.mcmmoredeem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.mcmmoredeem.locale.LocaleLoader;
import com.me.tft_02.mcmmoredeem.util.CommandUtils;
import com.me.tft_02.mcmmoredeem.util.CreditsManager;

public class CreditsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int credits;

        switch (args.length) {
            case 0:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                Player player = (Player) sender;
                credits = CreditsManager.getCredits(player.getUniqueId());

                sender.sendMessage(LocaleLoader.getString("Commands.Credits.Self", credits));
                return true;

            case 1:
                Player target = Bukkit.getServer().getPlayer(args[0]);

                if (CommandUtils.isOffline(sender, target)) {
                    return true;
                }

                credits = CreditsManager.getCredits(target.getUniqueId());

                sender.sendMessage(LocaleLoader.getString("Commands.Credits.Other", target.getName(), credits));
                return true;

            default:
                return false;
        }
    }
}
