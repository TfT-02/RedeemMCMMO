package com.me.tft_02.mcmmoredeem.datatypes;

import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerData {
    private UUID uuid;
    private String playerName;
    private Player player;
    private int credits;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.credits = 0;
    }

    public PlayerData(String uuid) {
        this.uuid = UUID.fromString(uuid);
        this.credits = 0;
    }

    public PlayerData(UUID uuid, String playerName, Player player, int credits) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.player = player;
        this.credits = credits;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int addCredits(int credits) {
        int newAmount = getCredits() + credits;
        setCredits(newAmount);
        return newAmount;
    }

    public int removeCredits(int credits) {
        int newAmount = getCredits() - credits;
        setCredits((newAmount < 0) ? 0 : newAmount);
        return newAmount;
    }
}
