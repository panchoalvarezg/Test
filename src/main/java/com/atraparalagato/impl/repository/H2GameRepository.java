package com.atraparalagato.impl.repository;

import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class H2GameRepository extends DataRepository<GameState<HexPosition>, String> {

    private Connection connection;

    public H2GameRepository() {
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:file:./data/atrapar-al-gato-db", "sa", "password");
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al conectar a la base de datos H2", e);
        }
    }

    @Override
    protected void initialize() {
        String query = """
            CREATE TABLE IF NOT EXISTS Games (
            gameId VARCHAR(255) PRIMARY KEY NOT NULL,
            data CLOB NOT NULL
            )
            """;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al inicializar la base de datos H2", e);
        }
    }

    @Override
    public GameState<HexPosition> save(GameState<HexPosition> entity) {
        if (!(entity instanceof HexGameState hexState)) {
            throw new IllegalArgumentException("Estado no es HexGameState");
        }

        String selectQuery = "SELECT COUNT(gameId) FROM Games WHERE gameId = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, hexState.getGameId());
            ResultSet resultSet = selectStmt.executeQuery();

            JSONObject serializedEntity = (JSONObject) hexState.getSerializableState();

            beforeSave(entity);

            if (resultSet.next() && resultSet.getInt(1) != 0) {
                String updateQuery = "UPDATE Games SET data = ? WHERE gameId = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, serializedEntity.toString());
                    updateStmt.setString(2, hexState.getGameId());
                    updateStmt.executeUpdate();
                }
            } else {
                String insertQuery = "INSERT INTO Games (gameId, data) VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, hexState.getGameId());
                    insertStmt.setString(2, serializedEntity.toString());
                    insertStmt.executeUpdate();
                }
            }

            afterSave(entity);
            return hexState;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al guardar estado del juego", e);
        }
    }

    @Override
    public Optional<GameState<HexPosition>> findById(String id) {
        if (id == null) return Optional.empty();

        String query = "SELECT data FROM Games WHERE gameId = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String dataJSONString = resultSet.getString(1);
                return Optional.of(deserializeGameState(dataJSONString, id));
            } else {
                return Optional.empty();
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar juego por id", e);
        }
    }

    private HexGameState deserializeGameState(String serializedData, String gameId) {
        JSONObject json = new JSONObject(serializedData);
        int boardSize = json.getInt("boardSize");
        HexGameState state = new HexGameState(gameId, boardSize);

        JSONArray catPos = json.getJSONArray("catPosition");
        state.setCatPosition(new HexPosition(catPos.getInt(0), catPos.getInt(1)));

        JSONArray blockedArray = json.getJSONArray("blockedPositions");
        Set<HexPosition> blocked = new LinkedHashSet<>();
        for (int i = 0; i < blockedArray.length(); i++) {
            JSONArray pos = blockedArray.getJSONArray(i);
            blocked.add(new HexPosition(pos.getInt(0), pos.getInt(1)));
        }
        state.getGameBoard().setBlockedPositions(blocked);

        for (int i = 0; i < json.getInt("moveCount"); i++) {
            state.incrementMoveCount();
        }

        return state;
    }

    // Otros métodos no implementados (como se indicó en el README)
    @Override public List<GameState<HexPosition>> findAll() { throw new UnsupportedOperationException(); }
    @Override public List<GameState<HexPosition>> findWhere(Predicate<GameState<HexPosition>> condition) { throw new UnsupportedOperationException(); }
    @Override public <R> List<R> findAndTransform(Predicate<GameState<HexPosition>> condition, Function<GameState<HexPosition>, R> transformer) { throw new UnsupportedOperationException(); }
    @Override public long countWhere(Predicate<GameState<HexPosition>> condition) { throw new UnsupportedOperationException(); }
    @Override public boolean deleteById(String id) { throw new UnsupportedOperationException(); }
    @Override public long deleteWhere(Predicate<GameState<HexPosition>> condition) { throw new UnsupportedOperationException(); }
    @Override public boolean existsById(String id) { throw new UnsupportedOperationException(); }
    @Override public <R> R executeInTransaction(Function<DataRepository<GameState<HexPosition>, String>, R> operation) { throw new UnsupportedOperationException(); }
    @Override public List<GameState<HexPosition>> findWithPagination(int page, int size) { throw new UnsupportedOperationException(); }
    @Override public List<GameState<HexPosition>> findAllSorted(Function<GameState<HexPosition>, ? extends Comparable<?>> sortKeyExtractor, boolean ascending) { throw new UnsupportedOperationException(); }
    @Override public <R> List<R> executeCustomQuery(String query, Function<Object, R> resultMapper) { throw new UnsupportedOperationException(); }
    @Override protected void cleanup() { throw new UnsupportedOperationException(); }
} 
