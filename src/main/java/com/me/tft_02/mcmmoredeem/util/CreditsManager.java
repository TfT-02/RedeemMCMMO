package com.me.tft_02.mcmmoredeem.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import com.me.tft_02.mcmmoredeem.datatypes.PlayerData;
import com.me.tft_02.mcmmoredeem.mcMMORedeem;

public class CreditsManager {
    private static String creditsFilePath = mcMMORedeem.getFlatFileDirectory() + "credits.yml";
    private static List<PlayerData> players = new ArrayList<PlayerData>();
    private static File creditsFile = new File(creditsFilePath);

    private CreditsManager() {}

    /**
     * Retrieve a playerdata object by its name
     *
     * @param uuid The UUID of the player
     * @return the existing player data, null otherwise
     */
    public static PlayerData getPlayerData(UUID uuid) {
        for (PlayerData playerData : players) {
            if (playerData.getUuid().equals(uuid)) {
                return playerData;
            }
        }

        return null;
    }

    public static int getCredits(UUID uuid) {
        PlayerData playerData = getPlayerData(uuid);
        if (playerData == null) {
            return 0;
        }
        else {
            return playerData.getCredits();
        }
    }

    /**
     * Load credits file.
     */
    public static void loadCredits() {
        if (!creditsFile.exists()) {
            return;
        }

        YamlConfiguration creditsFile = YamlConfiguration.loadConfiguration(CreditsManager.creditsFile);

        for (String uuid : creditsFile.getConfigurationSection("").getKeys(false)) {
            PlayerData playerData = new PlayerData(uuid);

            playerData.setPlayerName(creditsFile.getString(uuid + ".Name"));

            players.add(playerData);
        }
    }

    /**
     * Save credits file.
     */
    public static void saveCredits() {
        if (creditsFile.exists()) {
            if (!creditsFile.delete()) {
                mcMMORedeem.p.getLogger().warning("Could not delete credits file. Credit saving failed!");
                return;
            }
        }

        YamlConfiguration creditsFile = new YamlConfiguration();

        mcMMORedeem.p.debug("Saving Players... (" + players.size() + ")");
        for (PlayerData playerData : players) {
            String uuid = playerData.getUuid().toString();

            creditsFile.set(uuid + ".Name", playerData.getPlayerName());
            creditsFile.set(uuid + ".Credits", playerData.getCredits());
        }

        try {
            creditsFile.save(CreditsManager.creditsFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
