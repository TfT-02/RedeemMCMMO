package com.me.tft_02.mcmmoredeem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.mcmmoredeem.datatypes.PlayerData;
import com.me.tft_02.mcmmoredeem.locale.LocaleLoader;
import com.me.tft_02.mcmmoredeem.util.CommandUtils;
import com.me.tft_02.mcmmoredeem.util.CreditsManager;

public class AddcreditsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                Player target = Bukkit.getServer().getPlayer(args[0]);

                if (CommandUtils.isOffline(sender, target)) {
                    return true;
                }

                String targetName = target.getName();

                if (CommandUtils.isInvalidInteger(sender, args[1])) {
                    return true;
                }

                int amount = Integer.parseInt(args[1]);

                if (CommandUtils.isNegativeInteger(sender, amount)) {
                    return true;
                }

                PlayerData playerData = CreditsManager.getPlayerData(target.getUniqueId());
                int newAmount = playerData.addCredits(amount);

                sender.sendMessage(LocaleLoader.getString("Commands.Addcredits.Sender", targetName, amount));

                target.sendMessage(LocaleLoader.getString("Commands.Generic.CreditReceived.1", amount));
                target.sendMessage(LocaleLoader.getString("Commands.Generic.CreditBalance", newAmount));
                target.sendMessage(LocaleLoader.getString("Commands.Generic.RedeemHelp"));
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "addcredits", "<player>", "<amount>"));
                return true;
        }
    }
}
