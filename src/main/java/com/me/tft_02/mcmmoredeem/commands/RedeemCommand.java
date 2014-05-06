package com.me.tft_02.mcmmoredeem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.api.ExperienceAPI;

import com.me.tft_02.mcmmoredeem.datatypes.PlayerData;
import com.me.tft_02.mcmmoredeem.locale.LocaleLoader;
import com.me.tft_02.mcmmoredeem.mcMMORedeem;
import com.me.tft_02.mcmmoredeem.util.CommandUtils;
import com.me.tft_02.mcmmoredeem.util.CreditsManager;

public class RedeemCommand  implements CommandExecutor {
    private CommandExecutor helpCommand = new HelpCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        switch (args.length) {
            case 1:
                if (!args[1].equalsIgnoreCase("help") || !args[1].equalsIgnoreCase("?")) {
                    printUsage(sender);
                    return true;
                }

                return helpCommand.onCommand(sender, command, label, args);

            case 2:
                String skillType = args[0];

                if (CommandUtils.isInvalidSkill(sender, skillType)) {
                    return true;
                }

                int amount = Integer.parseInt(args[1]);

                if (CommandUtils.isNegativeInteger(sender, amount)) {
                    return true;
                }

                PlayerData playerData = CreditsManager.getPlayerData(player.getUniqueId());
                int creditsPlayer = playerData.getCredits();

                if (creditsPlayer <= 0 || amount > creditsPlayer) {
                    player.sendMessage(LocaleLoader.getString("Commands.Redeem.NotEnoughCredits"));
                    return true;
                }

                int cap = ExperienceAPI.getLevelCap(skillType);

                if (ExperienceAPI.getLevel(player, skillType) + amount > cap) {
                    player.sendMessage(LocaleLoader.getString("Commands.Redeem.LevelCap", cap, skillType));
                    return true;
                }

                int newAmount = playerData.removeCredits(amount);

                ExperienceAPI.addLevel(player, skillType, amount);

                player.sendMessage(LocaleLoader.getString("Commands.Redeem.Success", amount, skillType));
                player.sendMessage(LocaleLoader.getString("Commands.Generic.CreditBalance", newAmount));
                return true;

            default:
                printUsage(sender);
                return true;
        }
    }

    private void printUsage(CommandSender sender) {
        sender.sendMessage(LocaleLoader.getString("General.Plugin.Header", mcMMORedeem.p.getDescription().getName()));
        sender.sendMessage(LocaleLoader.getString("General.Plugin.Authors", mcMMORedeem.p.getDescription().getAuthors()));
        sender.sendMessage(LocaleLoader.getString("General.Running_Version", mcMMORedeem.p.getDescription().getVersion()));
        sender.sendMessage(LocaleLoader.getString("General.Use_Help"));
    }
}
