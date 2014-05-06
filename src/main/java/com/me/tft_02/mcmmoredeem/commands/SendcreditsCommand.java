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

public class SendcreditsCommand  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
            case 2:
                Player target = mcMMORedeem.p.getServer().getPlayer(args[0]);

                if (CommandUtils.isOffline(sender, target)) {
                    return true;
                }

                if (CommandUtils.isInvalidInteger(sender, args[1])) {
                    return true;
                }

                int amount = Integer.parseInt(args[1]);

                if (CommandUtils.isNegativeInteger(sender, amount)) {
                    return true;
                }

                Player player = (Player) sender;
                PlayerData playerData = CreditsManager.getPlayerData(player.getUniqueId());
                int creditsPlayer =  playerData.getCredits();

                if (creditsPlayer <= 0 || amount > creditsPlayer) {
                    player.sendMessage(LocaleLoader.getString("Commands.Sendcredits.NotEnoughCredits"));
                    return true;
                }

                PlayerData targetData = CreditsManager.getPlayerData(target.getUniqueId());

                int newAmountPlayer = playerData.removeCredits(amount);
                int newAmountTarget = targetData.addCredits(amount);

                player.sendMessage(LocaleLoader.getString("Commands.Sendcredits.Success", amount, target.getName()));
                player.sendMessage(LocaleLoader.getString("Commands.Generic.CreditBalance", newAmountPlayer));

                target.sendMessage(LocaleLoader.getString("Commands.Generic.CreditReceived.2", amount, player.getName()));
                target.sendMessage(LocaleLoader.getString("Commands.Generic.CreditBalance", newAmountTarget));
                return true;

            default:
                return false;
        }
    }
}
