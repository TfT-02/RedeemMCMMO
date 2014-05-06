package com.me.tft_02.mcmmoredeem.util;

import org.bukkit.permissions.Permissible;

public class Permissions {

    public static boolean addCredits(Permissible permissible) {
        return permissible.hasPermission("mcmmoredeem.commands.addcredits");
    }

    public static boolean buyCredits(Permissible permissible) {
        return permissible.hasPermission("mcmmoredeem.commands.buycredits");
    }

    public static boolean credits(Permissible permissible) {
        return permissible.hasPermission("mcmmoredeem.commands.credits");
    }

    public static boolean redeem(Permissible permissible) {
        return permissible.hasPermission("mcmmoredeem.commands.redeem");
    }

    public static boolean sendCredits(Permissible permissible) {
        return permissible.hasPermission("mcmmoredeem.commands.sendcredits");
    }

    public static boolean takeCredits(Permissible permissible) {
        return permissible.hasPermission("mcmmoredeem.commands.takecredits");
    }

}
