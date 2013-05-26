package com.flapfactions.redeemMCMMO;

import java.io.File;
import java.io.IOException;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.util.commands.CommandUtils;

public class RedeemMCMMO extends JavaPlugin {
    public mcMMO mcmmo;
    public playerListener pl;
    public Economy econ = null;

    public File creditsFile;
    public YamlConfiguration credits;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginManager plugMan = getServer().getPluginManager();

        Plugin mcm = plugMan.getPlugin("mcmmo");
        if (!(mcm instanceof mcMMO)) {
            throw new UnknownDependencyException("Can't find the right mcMMO, did it have an update?");
        }
        if (!mcm.isEnabled()) {
            getLogger().severe(format("&emcMMO&c is not enabled! Disabling RedeemMCMMO!"));
            plugMan.disablePlugin(this);
            return;
        }

        if (getConfig().getBoolean("vault")) {
            econ = getEconomy();
            if (econ == null) {
                getLogger().warning(format("&cFailed to setup economy! &eEconomy features will be disabled."));
            }
        }

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().warning(format("&dFailed to submit Plugin Metrics :("));
        }

        pl = new playerListener(this);
        plugMan.registerEvents(this.pl, this);

        creditsFile = new File(this.getDataFolder(), "credits.yml");
        credits = YamlConfiguration.loadConfiguration(creditsFile);
        saveConfig();

        getLogger().info(format("&aRedeemMCMMO is now enabled - &dOriginally by Candybuddy"));
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        try {
            credits.save(creditsFile);
        } catch (IOException e) {
            getLogger().warning("Error saving credits file");
        }
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
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return null;
        }
        return rsp.getProvider();
    }

    public void send(CommandSender to, String formatMessage, Object... args) {
        to.sendMessage(format(formatMessage, args));
    }

    public String format(String formatMessage, Object... args) {
        if (args == null)
            return ChatColor.translateAlternateColorCodes('&', formatMessage);
        return String.format(ChatColor.translateAlternateColorCodes('&', formatMessage), args);
    }

    /*
     * Green: A
     * Aqua: B
     * Red: C
     * Gold: 6
     * Yellow: E
     *
     * Success messages should be in Green
     * Failure messages should be in Red
     * Players should be enclosed in Yellow
     * Credits should be enclosed in Gold
     * Commands should be in Aqua
     */

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
        if (cmd.getName().equals("addcredits")) {
            if (args.length != 2) {
                return false; // usage
            }
            Player target = (Bukkit.getServer().getPlayer(args[0]));
            if (target == null) {
                send(sender, "&e%s&c is not online or doesn't exist!", args[0]);
                return true; // TODO unindent
            }
            String targetPlayer = target.getName();
            String targetName = getConfig().getString(targetPlayer);
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                send(sender, "&cThe amount must be a positive integer!");
                return true;
            }
            if (amount <= 0) {
                send(sender, "&cThe amount must be a positive integer!");
                return true;
            }
            int newAmount;
            if (targetName != null) {
                int oldAmount = getConfig().getInt(targetPlayer + ".credits");
                newAmount = oldAmount + amount;
            } else {
                newAmount = amount;
            }
            getConfig().set(targetPlayer + ".credits", newAmount);
            saveConfig();
            send(sender, "&aYou have given &e%s&a &6%d&a McMMO credits.", targetPlayer, amount);
            send(target, "&aYou have just received &6%d&a McMMO credits.", amount);
            send(target, "&aNEW CREDIT BALANCE: &6%d", newAmount);
            send(target, "&aUse &b/rmhelp&a for help with redeeming them!");
            return true;
        } else if (cmd.getName().equals("takecredits")) {
            if (args.length != 2) {
                return false; // usage
            }
            Player target = (Bukkit.getServer().getPlayer(args[0]));
            if (target == null) {
                send(sender, "&e%s&c is not online or doesn't exist!", args[0]);
                return true;
            }
            String targetPlayer = target.getName();
            String targetName = getConfig().getString(targetPlayer);
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                send(sender, "&cThe amount must be a positive integer!");
                return true;
            }
            if (amount <= 0) {
                send(sender, "&cThe amount must be a positive integer!");
                return true;
            }
            if (targetName == null) {
                send(sender, "&e%s&c doesn't have any credits!", targetPlayer);
                return true;
            }
            int oldAmount = getConfig().getInt(targetPlayer + ".credits");
            int newAmount = oldAmount - amount;
            if (newAmount < 0) {
                // typo leniency - if you overshoot by less than 10, the command still goes through
                if (newAmount < -10) {
                    send(sender, "&e%s&c doesn't have that many credits!", targetPlayer);
                    return true;
                }
                send(sender, "&e%s&c doesn't have &6%d&c credits! Assuming you want to take &6%d&c credits.", targetPlayer, amount, oldAmount);
                amount = oldAmount;
                newAmount = 0;
            }
            getConfig().set(targetPlayer + ".credits", newAmount);
            saveConfig();
            send(sender, "&aYou have taken &6%d&a credits off &e%s", amount, targetPlayer);
            send(target, "&aYou have lost &6%d&a credits.", amount);
            send(target, "&aNEW CREDIT BALANCE: &6%d", newAmount);
            return true;
        } else if (cmd.getName().equals("sendcredits")) {
            if (CommandUtils.noConsoleUsage(sender)) {
                return true;
            }
            if (args.length != 2) {
                return false; // usage
            }
            Player target = getServer().getPlayer(args[0]);
            if (target == null) {
                send(sender, "&e%s&c is not online or doesn't exist!", args[0]);
                return true;
            }
            String targetPlayer = target.getName();
            String targetName = getConfig().getString(targetPlayer);
            Player source = (Player) sender;
            String sourcePlayer = source.getName();
            String sourceName = getConfig().getString(sourcePlayer);
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                send(sender, "&cThe amount must be a positive integer!");
                return true;
            }
            if (amount <= 0) {
                send(sender, "&cThe amount must be a positive integer!");
                return true;
            }
            if (sourceName == null) {
                send(sender, "&eYou&c don't have any credits to send!");
                return true;
            }
            int sourceOldAmount = getConfig().getInt(targetPlayer + ".credits");
            if (amount > sourceOldAmount) {
                send(sender, "&eYou&c don't have &6%d&c credits!", amount);
                return true;
            }
            int newSourceAmount = sourceOldAmount - amount;
            int oldTargetAmount = 0;
            if (targetName != null) {
                oldTargetAmount = getConfig().getInt(targetPlayer + ".credits");
            }
            int newTargetAmount = oldTargetAmount + amount;
            getConfig().set(targetPlayer + ".credits", newTargetAmount);
            getConfig().set(sourcePlayer + ".credits", newSourceAmount);
            saveConfig();
            send(sender, "&aYou have sent &6%d&a McMMO credits to &e%s&a.", amount, targetPlayer);
            send(target, "&aYou have just received &6%d&a McMMO credits from &e%s&a.", amount, sourcePlayer);
            send(sender, "&aNEW CREDIT BALANCE: &6%d", newSourceAmount);
            send(target, "&aNEW CREDIT BALANCE: &6%d", newTargetAmount);
            return true;
        } else if (cmd.getName().equals("credits")) {
            if (args.length == 0) {
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }
                Player player = (Player) sender;
                String name = player.getName();
                String playerName = getConfig().getString(name);
                int credits = 0;
                if (playerName != null) {
                    credits = getConfig().getInt(name + ".credits");
                }
                send(sender, "&aYou have &6%d&a McMMO credits remaining.", credits);
                return true;
            } else if (args.length == 1) {
                String targetName = getConfig().getString(args[0]);
                int credits = 0;
                if (targetName != null) {
                    credits = getConfig().getInt(args[0] + ".credits");
                }
                send(sender, "&e%s&a has &6%d&a McMMO credits remaining.", args[0], credits);
                return true;
            } else {
                return false; // usage
            }
        } else if (cmd.getName().equals("rmreload")) {
            reloadConfig();
            send(sender, "&aReloaded configuration file for RedeemMCMMO!");
            return true;
        } else if (cmd.getName().equals("redeem")) {
            if (CommandUtils.noConsoleUsage(sender)) {
                return true;
            }
            Player player = (Player) sender;
            if (args.length != 2) {
                return false; // usage
            }
            String skillType = args[0];
            try {
                int cap = ExperienceAPI.getLevelCap(skillType);
                int amount = Integer.parseInt(args[1]);

                int oldAmount = getConfig().getInt(player.getName() + ".credits");
                if (oldAmount < amount) {
                    send(player, "&cYou do not have enough credits!");
                    return true;
                }
                if (amount <= 0) {
                    send(player, "&cThe amount must be a positive integer!");
                    return true;
                }
                if (ExperienceAPI.getLevel(player, skillType) + amount > cap) {
                    send(player, "&cYou may not exceed the maximum level (&d%d&c) for &6%s&c!", cap, skillType);
                    return true;
                }
                int newAmount = oldAmount - amount;
                getConfig().set(player.getName() + ".credits", newAmount);
                ExperienceAPI.addLevel(player, skillType, amount);
                saveConfig();
                send(player, "&aYou have gained &d%d&a levels in &6%s&a!", amount, skillType);
                send(sender, "&aNEW CREDIT BALANCE: &6%d", newAmount);
                return true;
            } catch (InvalidSkillException e) {
                send(player, "&e%s&c is not a valid skill!", skillType);
                return true;
            } catch (NumberFormatException e) {
                send(sender, "&cThe amount must be a positive integer!");
                return true;
            }
        } else if (cmd.getName().equals("buycredits")) {
            if (CommandUtils.noConsoleUsage(sender)) {
                return true;
            }
            Player player = (Player) sender;
            if (econ == null) {
                send(player, "&cThis feature is not enabled!");
                return true;
            }
            if (args.length != 1) {
                return false; // usage
            }
            int amount = Integer.parseInt(args[0]);
            int cost = amount * getConfig().getInt("costPerCredit");
            double balance = econ.getBalance(player.getName());
            String currencyName = econ.currencyNamePlural();
            if (amount <= 0) {
                send(sender, "&cThe amount must be a positive integer!");
                return true;
            }
            if (balance < cost) {
                if (currencyName == null) {
                    send(sender, "&cYou do not have enough money to buy &6%d&c credits; you need at least &6%d&c.", amount, cost);
                    return true;
                } else {
                    send(sender, "&cYou do not have enough money to buy &6%d&c credits; you need at least &6%d %s&c.", amount, cost, currencyName);
                    return true;
                }
            }

            String playerName1 = player.getName();
            String playerName = getConfig().getString(playerName1);

            int oldAmount = 0;
            if (playerName != null) {
                oldAmount = getConfig().getInt(playerName1 + ".credits");
            }

            EconomyResponse r = econ.withdrawPlayer(playerName1, cost);
            if (!r.transactionSuccess()) {
                send(sender, "&cYour payment failed, try again or contact an admin. Reason: &e\"%s\"", r.errorMessage);
                return true;
            }

            int newAmount = oldAmount + amount;
            getConfig().set(playerName1 + ".credits", newAmount);
            saveConfig();

            if (currencyName == null) {
                send(sender, "&aYou bought &6%d&c McMMO credits for &6%d&a.", amount, cost);
                return true;
            } else {
                send(sender, "&aYou bought &6%d&c McMMO credits for &6%d %s&a.", amount, cost, currencyName);
                return true;
            }
        } else if (cmd.getName().equals("rmhelp")) {
            send(sender, "&e----- &9RedeemMCMMO Help ~ Player Commands&e -----");
            send(sender, ((econ != null && sender.hasPermission("redeemMCMMO.buycredits")) ? "&b" : "&c") + "/buycredits <amount>&e - Buy credits with ingame money if it is enabled.");
            send(sender, (sender.hasPermission("redeemMCMMO.showcredits") ? "&b" : "&c") + "/credits [player]&e - View your own or another players credit balance.");
            send(sender, (sender.hasPermission("redeemMCMMO.sendcredits") ? "&b" : "&c") + "/sendcredits <player> <amount>&e - Reedeem your credits into any mcMMO skill.");
            send(sender, (sender.hasPermission("redeemMCMMO.redeem") ? "&b" : "&c") + "/redeem <skill> <amount>&e - Reedeem your credits into any mcMMO skill.");
            send(sender, "&e----- &9RedeemMCMMO Help ~ Admin Commands&e -----");
            send(sender, (sender.hasPermission("redeemMCMMO.addcredits") ? "&b" : "&c") + "/addcredits <player> <amount>&e - Give a player credits.");
            send(sender, (sender.hasPermission("redeemMCMMO.takecredits") ? "&b" : "&c") + "/takecredits <player> <amount>&e - Take credits away from a player.");
            send(sender, (sender.hasPermission("redeemMCMMO.rmreload") ? "&b" : "&c") + "/rmreload&e - Reload the configuration file.");
            return true;
        }
        return false; // usage
    }
}
