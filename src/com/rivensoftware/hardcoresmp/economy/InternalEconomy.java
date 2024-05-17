package com.rivensoftware.hardcoresmp.economy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import co.aikar.commands.PaperCommandManager;

public class InternalEconomy {
    private MySQLManager mySQLManager;
    private EconomyCommands economyCommands;

    public InternalEconomy(MySQLManager mySQLManager) {
        this.mySQLManager = mySQLManager;
        this.economyCommands = new EconomyCommands(mySQLManager);
    }

    public void registerCommands(PaperCommandManager commandManager) {
        commandManager.registerCommand(economyCommands);
    }

    // Additional methods to interact with the economy
    public double getBalance(UUID playerUUID) {
        return economyCommands.getBalance(playerUUID);
    }

    public void setBalance(UUID playerUUID, double amount) {
        economyCommands.setBalance(playerUUID, amount);
    }

    public void addBalance(UUID playerUUID, double amount) {
        economyCommands.addBalance(playerUUID, amount);
    }

    public void removeBalance(UUID playerUUID, double amount) {
        economyCommands.removeBalance(playerUUID, amount);
    }

    public double getOfflineBalance(UUID playerUUID) {
        try (Connection connection = mySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT balance FROM player_balances WHERE player_uuid = ?")) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            } else {
                return 0.0; // Return 0.0 if the player does not exist in the database
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
