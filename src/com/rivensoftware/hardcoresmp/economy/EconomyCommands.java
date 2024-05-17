package com.rivensoftware.hardcoresmp.economy;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@CommandAlias("eco")
public class EconomyCommands extends BaseCommand {

    private MySQLManager mySQLManager;

    public EconomyCommands(MySQLManager mySQLManager) {
        this.mySQLManager = mySQLManager;
    }

    @Subcommand("add")
    @CommandPermission("economy.add")
    public void onEcoAdd(CommandSender sender, String targetName, double amount) {
        UUID playerUUID = getUUIDFromName(targetName);
        if (playerUUID != null) {
            addBalance(playerUUID, amount);
            sender.sendMessage("Added " + amount + " to " + targetName + "'s balance.");
        } else {
            sender.sendMessage("Player not found.");
        }
    }

    @Subcommand("give")
    @CommandPermission("economy.give")
    public void onEcoGive(CommandSender sender, String targetName, double amount) {
        UUID playerUUID = getUUIDFromName(targetName);
        if (playerUUID != null) {
            setBalance(playerUUID, amount);
            sender.sendMessage("Set " + targetName + "'s balance to " + amount + ".");
        } else {
            sender.sendMessage("Player not found.");
        }
    }

    @Subcommand("set")
    @CommandPermission("economy.set")
    public void onEcoSet(CommandSender sender, String targetName, double amount) {
        UUID playerUUID = getUUIDFromName(targetName);
        if (playerUUID != null) {
            setBalance(playerUUID, amount);
            sender.sendMessage("Set " + targetName + "'s balance to " + amount + ".");
        } else {
            sender.sendMessage("Player not found.");
        }
    }

    @Subcommand("balance")
    @CommandPermission("economy.balance")
    public void onBalance(CommandSender sender, String targetName) {
        UUID playerUUID = getUUIDFromName(targetName);
        if (playerUUID != null) {
            double balance = getBalance(playerUUID);
            sender.sendMessage(targetName + "'s balance is " + balance + ".");
        } else {
            sender.sendMessage("Player not found.");
        }
    }

    public double getBalance(UUID playerUUID) {
        try (Connection connection = mySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT balance FROM player_balances WHERE player_uuid = ?")) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            } else {
                createPlayerBalance(playerUUID, 0.0);
                return 0.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public void setBalance(UUID playerUUID, double amount) {
        try (Connection connection = mySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE player_balances SET balance = ? WHERE player_uuid = ?")) {
            statement.setDouble(1, amount);
            statement.setString(2, playerUUID.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBalance(UUID playerUUID, double amount) {
        double currentBalance = getBalance(playerUUID);
        setBalance(playerUUID, currentBalance + amount);
    }

    public void removeBalance(UUID playerUUID, double amount) {
        double currentBalance = getBalance(playerUUID);
        setBalance(playerUUID, currentBalance - amount);
    }

    private void createPlayerBalance(UUID playerUUID, double initialBalance) {
        try (Connection connection = mySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO player_balances (player_uuid, balance) VALUES (?, ?)")) {
            statement.setString(1, playerUUID.toString());
            statement.setDouble(2, initialBalance);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private UUID getUUIDFromName(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return player.getUniqueId();
        } else {
            // Fetch UUID from database or use a service like Mojang API
            // Placeholder implementation: return null if player not online
            return null;
        }
    }
}
