package com.jeido.test.repository;

import com.jeido.test.Test;
import com.jeido.test.entity.Player;
import com.jeido.test.service.DataBaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PlayerRepository {

    private static PlayerRepository instance;

    private final DataBaseService db;
    private final Connection connection;

    public static PlayerRepository getInstance() {
        if (instance == null) {
            instance = new PlayerRepository();
        }
        return instance;
    }

    private PlayerRepository() {
        db = DataBaseService.getInstance();
        connection = db.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS player(id VARCHAR(36) PRIMARY KEY, name VARCHAR(16))")) {
            stmt.execute();
        } catch (SQLException e) {
            Test.LOGGER.error("Error while initializing player table", e);
            db.closeConnection();

        }
    }

    public void save(Player player) {
        if (player == null) {
            return;
        }
        if (player.getId() == null) {
            return;
        }
        try(PreparedStatement stmt = connection.prepareStatement("INSERT INTO player VALUES(?,?)")) {
            stmt.setString(1, player.getId().toString());
            stmt.setString(2, player.getName());
            stmt.execute();
        } catch (SQLException e) {
            Test.LOGGER.error("Error while saving player {}", player, e);
        }
    }

    public Player findById(UUID id) {
        if (id == null) {
            return null;
        }
        String sql = "SELECT * FROM player WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Player(UUID.fromString(rs.getString("id")), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            Test.LOGGER.error("Error while finding player by id {}", id, e);
        }
        return null;
    }

    public List<Player> findAll() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM player";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
                ) {
            while (rs.next()) {
                players.add(new Player(UUID.fromString(rs.getString("id")), rs.getString("name")));
            }
        } catch (SQLException e) {
            Test.LOGGER.error("Error while finding all players", e);
        }
        return players;
    }

    public Player update(UUID id, Player player) {
        if (id == null) {
            return null;
        }
        if (!exists(id)) {
            return null;
        }

        String sql = "UPDATE player SET name = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player.getName());
            stmt.setString(2, id.toString());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                return null;
            }
            return new Player(UUID.fromString(id.toString()), player.getName());
        } catch (SQLException e) {
            Test.LOGGER.error("Error while updating player with id {}", id, e);
        }
        return null;
    }

    public void delete(UUID id) {
        if (id == null) {
            return;
        }

        if (!exists(id)) {
            return;
        }

        String sql = "DELETE FROM player WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.execute();
        } catch (SQLException e) {
            Test.LOGGER.error("Error while deleting player with id {}", id, e);
        }

    }

    public boolean exists(UUID id) {
        if (id == null) {
            return false;
        }
        return findById(id) != null;
    }
}
