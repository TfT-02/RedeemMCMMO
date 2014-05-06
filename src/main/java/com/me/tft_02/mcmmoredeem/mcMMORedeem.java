package com.me.tft_02.mcmmoredeem;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.me.tft_02.mcmmoredeem.commands.AddcreditsCommand;
import com.me.tft_02.mcmmoredeem.commands.BuycreditsCommand;
import com.me.tft_02.mcmmoredeem.commands.CreditsCommand;
import com.me.tft_02.mcmmoredeem.commands.RedeemCommand;
import com.me.tft_02.mcmmoredeem.commands.SendcreditsCommand;
import com.me.tft_02.mcmmoredeem.commands.TakecreditsCommand;
import com.me.tft_02.mcmmoredeem.config.Config;
import com.me.tft_02.mcmmoredeem.listeners.PlayerListener;
import com.me.tft_02.mcmmoredeem.util.CreditsManager;
import com.me.tft_02.mcmmoredeem.util.LogFilter;
import net.milkbowl.vault.economy.Economy;

public class mcMMORedeem extends JavaPlugin {
    /* File Paths */
    private static String mainDirectory;
    private static String flatFileDirectory;

    public static mcMMORedeem p;

    // Jar Stuff
    public static File redeemMcMMO;

    private boolean mcMMOEnabled = false;

    public Economy econ = null;

    public File creditsFile;
    public YamlConfiguration credits;

    @Override
    public void onEnable() {
        p = this;
        getLogger().setFilter(new LogFilter(this));

        setupFilePaths();

        setupMcMMO();

        if (!isMcMMOEnabled()) {
            this.getLogger().warning("mcMMO-Redeem requires mcMMO to run, please download mcMMO. http://dev.bukkit.org/server-mods/mcmmo/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (Config.getInstance().getUseVault()) {
            econ = getEconomy();
            if (econ == null) {
                getLogger().warning("Failed to setup economy! Economy features will be disabled.");
            }
        }

        registerEvents();

        CreditsManager.loadCredits();

        getCommand("addcredits").setExecutor(new AddcreditsCommand());
        getCommand("takecredits").setExecutor(new TakecreditsCommand());
        getCommand("sendcredits").setExecutor(new SendcreditsCommand());
        getCommand("credits").setExecutor(new CreditsCommand());
        getCommand("redeem").setExecutor(new RedeemCommand());

        if (econ != null) {
            getCommand("buycredits").setExecutor(new BuycreditsCommand());
        }
    }

    private void setupMcMMO() {
        if (getServer().getPluginManager().isPluginEnabled("mcMMO")) {
            mcMMOEnabled = true;
        }
    }

    public boolean isMcMMOEnabled() {
        return mcMMOEnabled;
    }

    @Override
    public void onDisable() {
        saveConfig();
        CreditsManager.saveCredits();
    }

    public static String getMainDirectory() {
        return mainDirectory;
    }

    public static String getFlatFileDirectory() {
        return flatFileDirectory;
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();

        // Register events
        pluginManager.registerEvents(new PlayerListener(), this);
    }

    public void debug(String message) {
        getLogger().info("[Debug] " + message);
    }

    /**
     * Setup the various storage file paths
     */
    private void setupFilePaths() {
        redeemMcMMO = getFile();
        mainDirectory = getDataFolder().getPath() + File.separator;
        flatFileDirectory = mainDirectory + "flatfile" + File.separator;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        credits = YamlConfiguration.loadConfiguration(creditsFile);
    }

    private Economy getEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return null;
        }

        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        return (registeredServiceProvider == null) ? null : registeredServiceProvider.getProvider();
    }
}
