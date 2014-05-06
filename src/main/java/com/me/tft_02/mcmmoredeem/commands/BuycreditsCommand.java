package com.me.tft_02.mcmmoredeem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.mcmmoredeem.config.Config;
import com.me.tft_02.mcmmoredeem.datatypes.PlayerData;
import com.me.tft_02.mcmmoredeem.locale.LocaleLoader;
import com.me.tft_02.mcmmoredeem.mcMMORedeem;
import com.me.tft_02.mcmmoredeem.util.CommandUtils;
import com.me.tft_02.mcmmoredeem.util.CreditsManager;
import net.milkbowl.vault.economy.EconomyResponse;

public class BuycreditsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
            case 1:
                Player player = (Player) sender;

                if (CommandUtils.isInvalidInteger(sender, args[0])) {
                    return true;
                }

                int amount = Integer.parseInt(args[0]);

                if (CommandUtils.isNegativeInteger(sender, amount)) {
                    return true;
                }

                int cost = amount * Config.getInstance().getCostPerCredit();
                double balance = mcMMORedeem.p.econ.getBalance(player.getName());
                String currencyName = CommandUtils.getCurrencyName();

                if (balance < cost) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Buycredits.NotEnoughMoney", amount, cost, currencyName));
                    return true;
                }

                EconomyResponse response = mcMMORedeem.p.econ.withdrawPlayer(player.getName(), cost);
                if (!response.transactionSuccess()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Buycredits.Failed", response.errorMessage));
                    return true;
                }

                PlayerData playerData = CreditsManager.getPlayerData(player.getUniqueId());
                playerData.addCredits(amount);

                sender.sendMessage(LocaleLoader.getString("Commands.Buycredits.Success", amount, cost, currencyName));
                return true;

            default:
                return false;
        }
    }
}
