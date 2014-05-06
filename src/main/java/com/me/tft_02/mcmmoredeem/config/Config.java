package com.me.tft_02.mcmmoredeem.config;

import java.util.ArrayList;
import java.util.List;

public class Config extends AutoUpdateConfigLoader {
    private static Config instance;

    private Config() {
        super("config.yml");
        validate();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
    }

    @Override
    protected boolean validateKeys() {
        // Validate all the settings!
        List<String> reason = new ArrayList<String>();

        /* General Settings */
        if (getSaveInterval() <= 0) {
            reason.add("General.Save_Interval should be greater than 0!");
        }

        return noErrorsInConfig(reason);
    }

    @Override
    protected void loadKeys() {}

    /* @formatter:off */

    /* GENERAL SETTINGS */
    public String getLocale() { return config.getString("General.Locale", "en_us"); }
    public int getSaveInterval() { return config.getInt("General.Save_Interval", 10); }
    public boolean getUpdateCheckEnabled() { return config.getBoolean("General.Update_Check", true); }
    public boolean getPreferBeta() { return config.getBoolean("General.Prefer_Beta", false); }
    public boolean getVerboseLoggingEnabled() { return config.getBoolean("General.Verbose_Logging", false); }
    public boolean getConfigOverwriteEnabled() { return config.getBoolean("General.Config_Update_Overwrite", true); }

    /* REDEEM SETTINGS */
    public boolean getJoinMessageEnabled() { return config.getBoolean("Redeem.Join_Message", true); }
    public int getStartupAmount() { return config.getInt("Redeem.Startup_Amount", 50); }
    public boolean getUseVault() { return config.getBoolean("Redeem.Use_Vault", true); }
    public int getCostPerCredit() { return config.getInt("Redeem.Cost_Per_Credit", 10); }

    /* @formatter:on */
}
