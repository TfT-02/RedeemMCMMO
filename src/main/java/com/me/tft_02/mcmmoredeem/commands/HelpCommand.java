package com.me.tft_02.mcmmoredeem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.me.tft_02.mcmmoredeem.locale.LocaleLoader;
import com.me.tft_02.mcmmoredeem.util.Permissions;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                if (Integer.parseInt(args[1]) > 1) {
                    getHelpPage(Integer.parseInt(args[1]), sender);
                    return true;
                }
                else {
                    getHelpPage(1, sender);
                    return true;
                }

            default:
                getHelpPage(1, sender);
                return true;
        }
    }

    private void getHelpPage(int page, CommandSender sender) {
        int maxPages = 3;
        int nextPage = page + 1;

        if (page > maxPages) {
            sender.sendMessage(LocaleLoader.getString(LocaleLoader.getString("Help.Page_Does_Not_Exist"), maxPages));
            return;
        }

        sender.sendMessage(LocaleLoader.getString("Help.Page_Header", page, maxPages));
        switch (page) {
            case 1:
                sendHelpPage(sender, LocaleLoader.getString("Help.Page_0.Line_0"));
                sendHelpPage(sender, LocaleLoader.getString("Help.Page_0.Line_1"));

            case 2:
                sendHelpPage(sender, LocaleLoader.getString("Help.Page_1.Line_0"));

                if (Permissions.credits(sender)) {
                    sendHelpPage(sender, LocaleLoader.getString("Help.Page_1.Line_1"));
                }

                if (Permissions.redeem(sender)) {
                    sendHelpPage(sender, LocaleLoader.getString("Help.Page_1.Line_2"));
                }

                if (Permissions.buyCredits(sender)) {
                    sendHelpPage(sender, LocaleLoader.getString("Help.Page_1.Line_3"));
                }

                if (Permissions.sendCredits(sender)) {
                    sendHelpPage(sender, LocaleLoader.getString("Help.Page_1.Line_4"));
                }

            case 3:
                sendHelpPage(sender, LocaleLoader.getString("Help.Page_2.Line_0"));

                if (Permissions.addCredits(sender)) {
                    sendHelpPage(sender, LocaleLoader.getString("Help.Page_2.Line_1"));
                }

                if (Permissions.takeCredits(sender)) {
                    sendHelpPage(sender, LocaleLoader.getString("Help.Page_2.Line_2"));
                }

            default:
                if (nextPage <= maxPages) {
                    sender.sendMessage(LocaleLoader.getString("Help.Page_Ending", "/redeem help", nextPage));
                }
        }
    }

    /**
     * Send a string, but only if .length > 0
     */
    private void sendHelpPage(CommandSender sender, String string) {
        if (string.length() > 0) {
            sender.sendMessage(string);
        }
    }
}
