package com.me.tft_02.mcmmoredeem.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

import com.me.tft_02.mcmmoredeem.locale.LocaleLoader;
import com.me.tft_02.mcmmoredeem.mcMMORedeem;

public final class CommandUtils {

    public static boolean hidden(CommandSender sender, Player target, boolean hasPermission) {
        return sender instanceof Player && !((Player) sender).canSee(target) && !hasPermission;
    }

    public static boolean noConsoleUsage(CommandSender sender) {
        if (sender instanceof Player) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.NoConsole"));
        return true;
    }

    public static boolean isOffline(CommandSender sender, Player player) {
        if (player != null) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
        return true;
    }

    public static boolean isInvalidInteger(CommandSender sender, String value) {
        if (StringUtils.isInt(value)) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.InvalidInteger"));
        return true;
    }

    public static boolean isNegativeInteger(CommandSender sender, int value) {
        if (value > 0) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.NegativeInteger"));
        return true;
    }

    public static boolean isInvalidSkill(CommandSender sender, String skillName) {
        if (SkillUtils.isSkill(skillName)) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.InvalidSkill"));
        return true;
    }

    public static String getCurrencyName() {
        String currencyNamePlural = mcMMORedeem.p.econ.currencyNamePlural();
        return (currencyNamePlural == null) ? "" : " " + currencyNamePlural;
    }

    public static List<String> getOnlinePlayerNames(CommandSender sender) {
        Player player = sender instanceof Player ? (Player) sender : null;
        List<String> onlinePlayerNames = new ArrayList<String>();

        for (Player onlinePlayer : mcMMORedeem.p.getServer().getOnlinePlayers()) {
            if (player != null && player.canSee(onlinePlayer)) {
                onlinePlayerNames.add(onlinePlayer.getName());
            }
        }

        return onlinePlayerNames;
    }

    /**
     * Get a matched player name if one was found in the database.
     *
     * @param partialName Name to match
     *
     * @return Matched name or {@code partialName} if no match was found
     */
    public static String getMatchedPlayerName(String partialName) {
        Player player = mcMMORedeem.p.getServer().getPlayer(partialName);

        if (player != null) {
            partialName = player.getName();
        }

        return partialName;
    }

    /**
     * Attempts to match any player names with the given name, and returns a list of all possibly matches.
     *
     * This list is not sorted in any particular order.
     * If an exact match is found, the returned list will only contain a single result.
     *
     * @param partialName Name to match
     * @return List of all possible names
     */
    private static List<String> matchPlayer(String partialName) {
        List<String> matchedPlayers = new ArrayList<String>();

        for (OfflinePlayer offlinePlayer : mcMMORedeem.p.getServer().getOfflinePlayers()) {
            String playerName = offlinePlayer.getName();

            if (partialName.equalsIgnoreCase(playerName)) {
                // Exact match
                matchedPlayers.clear();
                matchedPlayers.add(playerName);
                break;
            }

            if (playerName.toLowerCase().contains(partialName.toLowerCase())) {
                // Partial match
                matchedPlayers.add(playerName);
            }
        }

        return matchedPlayers;
    }
}
