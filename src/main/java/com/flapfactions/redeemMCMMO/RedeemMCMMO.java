package com.flapfactions.redeemMCMMO;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;

public class RedeemMCMMO extends JavaPlugin {
    public mcMMO mcmmo;
    public playerListener pl;
    public Economy econ = null;

    @Override
    public void onEnable() {
        PluginManager plugMan = getServer().getPluginManager();
        Plugin mcm = plugMan.getPlugin("mcmmo");
        if (!(mcm instanceof mcMMO)) {
            throw new UnknownDependencyException("Can't find the right mcMMO, did it have an update?");
        }
        mcmmo = (mcMMO) mcm;
        if (!mcmmo.isEnabled()) {
            getLogger().severe("mcMMO is not enabled! Disabling RedeemMCMMO!");
            plugMan.disablePlugin(this);
            return;
        }
        econ = getEconomy();
        if (econ == null) {
            getLogger().warning("Failed to setup economy! Economy features will be disabled.");
        }
        pl = new playerListener(this);
        plugMan.registerEvents(this.pl, this);
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().warning("Failed to submit Plugin Metrics :(");
        }
        getLogger().info("RedeemMCMMO is now enabled - Originally by Candybuddy");
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        saveConfig();
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

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
        if (cmd.getName().equals("addcredits")) {
            if (args.length == 2) {
                Player target = (Bukkit.getServer().getPlayer(args[0]));
                if (target == null) {
                    sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is not online or doesn't exist!");
                    return true;
                } else {
                    String targetPlayer = target.getName();
                    String targetName = getConfig().getString(targetPlayer);
                    int amount = Integer.parseInt(args[1]);
                    if (amount <= 0) {
                        sender.sendMessage(ChatColor.RED + "The amount must be a positive number!");
                        return true;
                    }
                    if (targetName != null) {
                        int oldAmount = getConfig().getInt(targetPlayer + ".credits");
                        int newAmount = oldAmount + amount;
                        getConfig().set(targetPlayer + ".credits", newAmount);
                        saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "You have given " + ChatColor.GOLD + targetPlayer + ", " + amount + ChatColor.GREEN + " MCMMO credits.");
                        target.sendMessage(ChatColor.GREEN + "You have just received " + ChatColor.GOLD + amount + ChatColor.GREEN + " MCMMO credits.");
                        target.sendMessage(ChatColor.GREEN + "NEW CREDIT BALANCE: " + ChatColor.GOLD + newAmount);
                        target.sendMessage(ChatColor.GREEN + "Do " + ChatColor.AQUA + "/rmhelp" + ChatColor.GREEN + " for help with redeeming them!");
                        return true;
                    } else {
                        getConfig().set(targetPlayer + ".credits", amount);
                        saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "You have given " + ChatColor.GOLD + targetPlayer + ", " + amount + ChatColor.GREEN + " MCMMO credits.");
                        target.sendMessage(ChatColor.GREEN + "You have just received " + ChatColor.GOLD + amount + ChatColor.GREEN + " MCMMO credits.");
                        target.sendMessage(ChatColor.GREEN + "NEW CREDIT BALANCE: " + ChatColor.GOLD + amount);
                        target.sendMessage(ChatColor.GREEN + "Do " + ChatColor.AQUA + "/rmhelp" + ChatColor.GREEN + " for help with redeeming them!");
                        return true;
                    }
                }
            } else {
                return false;
            }
        } else if (cmd.getName().equals("takecredits")) {
            if (args.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Too little arguments!");
            } else if (args.length == 2) {
                Player target = (Bukkit.getServer().getPlayer(args[0]));
                if (target == null) {
                    sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is not online or doesn't exist!");
                    return true;
                } else {
                    String targetPlayer = target.getName();
                    String targetName = getConfig().getString(targetPlayer);
                    int amount = Integer.parseInt(args[1]);
                    if (amount <= 0) {
                        sender.sendMessage(ChatColor.RED + "The amount must be a positive number!");
                        return true;
                    }
                    if (targetName != null) {
                        int oldAmount = getConfig().getInt(targetPlayer + ".credits");
                        if (amount <= oldAmount) {
                            int newAmount = oldAmount - amount;
                            getConfig().set(targetPlayer + ".credits", newAmount);
                            saveConfig();
                            sender.sendMessage(ChatColor.GREEN + "You have taken " + ChatColor.GOLD + amount + ChatColor.GREEN + " credits off " + ChatColor.GOLD + args[0]);
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " already has 0 credits!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " already has 0 credits!");
                        return true;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Too many arguments!");
            }
        } else if (cmd.getName().equals("credits")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    String name = player.getName();
                    String playerName = getConfig().getString(name);
                    if (playerName != null) {
                        int credits = getConfig().getInt(name + ".credits");
                        player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.GOLD + credits + ChatColor.GREEN + " MCMMO credits remaining.");
                        return true;
                    } else {
                        player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.GOLD + "0" + ChatColor.GREEN + " MCMMO credits remaining.");
                        return true;
                    }
                } else if (args.length == 1) {
                    String targetName = getConfig().getString(args[0]);
                    if (targetName != null) {
                        int credits = getConfig().getInt(args[0] + ".credits");
                        player.sendMessage(ChatColor.GOLD + args[0] + ChatColor.GREEN + " has " + ChatColor.GOLD + credits + ChatColor.GREEN + " MCMMO credits remaining.");
                        return true;
                    } else {
                        player.sendMessage(ChatColor.GOLD + args[0] + ChatColor.GREEN + " has " + ChatColor.GOLD + "0 " + ChatColor.GREEN + "MCMMO credits remaining.");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Too many arguments!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player!");
                return true;
            }
        } else if (cmd.getName().equals("rmreload")) {
            sender.sendMessage(ChatColor.GREEN + "Reloaded configuration file for RedeemMCMMO!");
            reloadConfig();
            return true;
        } else if (cmd.getName().equals("redeem")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player!");
                return true;
            }
            Player player = (Player) sender;
            if (args.length <= 1) {
                player.sendMessage(ChatColor.RED + "Too little arguments!");
            } else if (args.length == 2) {
                String skillType = args[0];
                int cap = 0;

                if (skillType.equalsIgnoreCase("taming") || skillType.equalsIgnoreCase("swords") || skillType.equalsIgnoreCase("unarmed") || skillType.equalsIgnoreCase("archery") || skillType.equalsIgnoreCase("axes") || skillType.equalsIgnoreCase("acrobatics") || skillType.equalsIgnoreCase("fishing") || skillType.equalsIgnoreCase("excavation") || skillType.equalsIgnoreCase("mining") || skillType.equalsIgnoreCase("herbalism") || skillType.equalsIgnoreCase("repair") || skillType.equalsIgnoreCase("woodcutting")) {
                    cap = ExperienceAPI.getLevelCap(skillType);
                } else {
                    player.sendMessage(ChatColor.RED + skillType + " is not a valid skill!");
                    return true;
                }
                try {
                    Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "The amount of credits must be a number!");
                    return true;
                }
                int amount = Integer.parseInt(args[1]);
                int oldamount = getConfig().getInt(player.getName() + ".credits");
                if (oldamount < amount) {
                    player.sendMessage(ChatColor.RED + "You do not have enough credits!");
                    return true;
                }
                if (amount <= 0) {
                    player.sendMessage(ChatColor.RED + "The amount must be a positive number!");
                    return true;
                }
                if (ExperienceAPI.getLevel(player, skillType) + amount > cap) {
                    player.sendMessage(ChatColor.RED + "You have reached the maximum for " + skillType);
                    return true;
                }
                int newamount = oldamount - amount;
                getConfig().set(player.getName() + ".credits", newamount);
                saveConfig();

                ExperienceAPI.addLevel(player, skillType, amount);
                player.sendMessage(ChatColor.GREEN + "You have added " + ChatColor.GOLD + amount + ChatColor.GREEN + " credits to " + ChatColor.GOLD + skillType + ChatColor.GREEN + ".");
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Too many arguments!");
            }
        } else if (cmd.getName().equals("buycredits")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (getConfig().getBoolean("vault") == true) {
                    if (args.length == 0) {
                        player.sendMessage(ChatColor.RED + "Too little arguments!");
                    } else if (args.length == 1) {
                        int amount = Integer.parseInt(args[0]);
                        int cost = amount * getConfig().getInt("costPerCredit");
                        double balance = econ.getBalance(player.getName());
                        if (amount <= 0) {
                            player.sendMessage(ChatColor.RED + "The amount must be a positive number!");
                        } else {
                            if (balance >= cost) {
                                String playerName1 = player.getName();
                                String playerName = getConfig().getString(playerName1);
                                if (playerName != null) {
                                    EconomyResponse r = econ.withdrawPlayer(playerName1, cost);
                                    if (r.transactionSuccess()) {
                                        int oldAmount = getConfig().getInt(playerName1 + ".credits");
                                        int newAmount = oldAmount + amount;
                                        getConfig().set(playerName1 + ".credits", newAmount);
                                        saveConfig();
                                        player.sendMessage(ChatColor.GREEN + "You bought " + ChatColor.GOLD + amount + ChatColor.GREEN + " MCMMO credits.");
                                        return true;
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Your payment failed, try again or contact an admin!");
                                        return true;
                                    }
                                } else {
                                    EconomyResponse r = econ.withdrawPlayer(playerName1, cost);
                                    if (r.transactionSuccess()) {
                                        getConfig().set(playerName1 + ".credits", amount);
                                        saveConfig();
                                        player.sendMessage(ChatColor.GREEN + "You bought " + ChatColor.GOLD + amount + ChatColor.GREEN + " MCMMO credits.");
                                        return true;
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Your payment failed, try again or contact an admin!");
                                        return true;
                                    }
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You do not have enough money to buy that many credits!");
                                return true;
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Too many arguments!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "This feature is not enabled!");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player!");
                return true;
            }
        } else if (cmd.getName().equals("rmhelp")) {
            sender.sendMessage(ChatColor.YELLOW + "----- " + ChatColor.BLUE + "RedeemMCMMO Help ~ Player Commands" + ChatColor.YELLOW + " -----");
            sender.sendMessage(ChatColor.AQUA + "/redeem <skill> <amount>" + ChatColor.YELLOW + " - Reedeem your credits into any mcMMO skill.");
            sender.sendMessage(ChatColor.AQUA + "/buycredits <amount>" + ChatColor.YELLOW + " - Buy credits with ingame money if it is enabled.");
            sender.sendMessage(ChatColor.AQUA + "/credits [player]" + ChatColor.YELLOW + " - Veiw your own or another players credit balance.");
            sender.sendMessage(ChatColor.YELLOW + "----- " + ChatColor.BLUE + "RedeemMCMMO Help ~ Admin Commands" + ChatColor.YELLOW + " -----");
            sender.sendMessage(ChatColor.AQUA + "/addcredits <player> <amount>" + ChatColor.YELLOW + " - Give a player credits.");
            sender.sendMessage(ChatColor.AQUA + "/takecredits <player> <amount>" + ChatColor.YELLOW + " - Take credits away from a player.");
            sender.sendMessage(ChatColor.AQUA + "/rmreload" + ChatColor.YELLOW + " - Reload the configuration file.");
            return true;
        }
        return false;
    }

}
