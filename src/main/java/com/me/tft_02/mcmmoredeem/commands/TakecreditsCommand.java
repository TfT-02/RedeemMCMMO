package com.me.tft_02.mcmmoredeem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.mcmmoredeem.datatypes.PlayerData;
import com.me.tft_02.mcmmoredeem.locale.LocaleLoader;
import com.me.tft_02.mcmmoredeem.mcMMORedeem;
import com.me.tft_02.mcmmoredeem.util.CommandUtils;
import com.me.tft_02.mcmmoredeem.util.CreditsManager;

public class TakecreditsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                Player target = mcMMORedeem.p.getServer().getPlayer(args[0]);

                if (CommandUtils.isOffline(sender, target)) {
                    return true;
                }

                int amount = Integer.parseInt(args[1]);

                if (CommandUtils.isNegativeInteger(sender, amount)) {
                    return true;
                }

                PlayerData targetData = CreditsManager.getPlayerData(target.getUniqueId());
                int creditsTarget = targetData.getCredits();

                if (creditsTarget <= 0) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Takecredits.NoCredits", target.getName()));
                    return true;
                }

                if (amount > creditsTarget) {
                    amount = creditsTarget;
                }

                int newAmount = targetData.removeCredits(amount);

                sender.sendMessage(LocaleLoader.getString("Commands.Takecredits.Success", amount, target.getName()));

                target.sendMessage(LocaleLoader.getString("Commands.Takecredits.LostCredits", amount));
                target.sendMessage(LocaleLoader.getString("Commands.Generic.CreditBalance", newAmount));
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "takecredits", "<player>", "<amount>"));
                return true;
        }
    }
}