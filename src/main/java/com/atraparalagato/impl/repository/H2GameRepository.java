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

/**
 * Implementación de DataRepository usando base de datos H2.
 */
public class H2GameRepository extends DataRepository<GameState<HexPosition>, String> {

    private Connection connection;

    public H2GameRepository() {
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:file:./data/atrapar-al-gato-db", "sa", "password");
            initialize();
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos H2", e);
        }
    }

    @Override
    protected void initialize() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Games (
                    gameId VARCHAR(255) PRIMARY KEY NOT NULL,
                    data JSON NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos H2", e);
        }
    }

    @Override
    public GameState<HexPosition> save(GameState<HexPosition> entity) {
        if (!(entity instanceof HexGameState hexState)) {
            throw new IllegalArgumentException("Estado no es HexGameState");
        }

        try (Statement statement = connection.createStatement()) {
            JSONObject serializedEntity = (JSONObject) hexState.getSerializableState();

            beforeSave(entity);

            ResultSet resultSet = statement.executeQuery("""
                SELECT COUNT(gameId) FROM Games
                WHERE gameId = '%s';
            """.formatted(hexState.getGameId()));

            if (resultSet.next() && resultSet.getInt(1) != 0) {
                statement.executeUpdate("""
                    UPDATE Games
                    SET data = '%s'
                    WHERE gameId = '%s';
                """.formatted(serializedEntity.toString(), hexState.getGameId()));
            } else {
                statement.executeUpdate("""
                    INSERT INTO Games (gameId, data)
                    VALUES ('%s', '%s');
                """.formatted(hexState.getGameId(), serializedEntity.toString()));
            }

            afterSave(entity);
            return hexState;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar estado del juego", e);
        }
    }

    @Override
    public Optional<GameState<HexPosition>> findById(String id) {
        if (id == null) return Optional.empty();

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                SELECT data FROM Games
                WHERE gameId = '%s'
                LIMIT 1;
            """.formatted(id));

            if (resultSet.next()) {
                String dataJSONString = resultSet.getString(1);
                return Optional.of(deserializeGameState(dataJSONString, id));
            }
        } catch (SQLException | JSONException e) {
            throw new RuntimeException("Error al buscar juego por id", e);
        }

        return Optional.empty();
    }

    private HexGameState deserializeGameState(String serializedData, String gameId) {
        try {
            JSONObject json = new JSONObject(serializedData);
            HexGameState state = new HexGameState(gameId, json.getInt("boardSize"));

            JSONArray catPos = json.getJSONArray("catPosition");
            state.setCatPosition(new HexPosition(catPos.getInt(0), catPos.getInt(1)));

            JSONArray blockedArray = json.getJSONArray("blockedPositions");
            LinkedHashSet<HexPosition> blocked = new LinkedHashSet<>();
            for (int i = 0; i < blockedArray.length(); i++) {
                JSONArray pos = blockedArray.getJSONArray(i);
                blocked.add(new HexPosition(pos.getInt(0), pos.getInt(1)));
            }
            state.getGameBoard().setBlockedPositions(blocked);

            state.setMoveCount(json.getInt("moveCount"));
            return state;
        } catch (JSONException e) {
            throw new RuntimeException("Error al deserializar estado del juego", e);
        }
    }

    @Override public void cleanup() {
        // Se deja vacío para permitir compilación
    }

    @Override public List<GameState<HexPosition>> findAll() { throw new UnsupportedOperationException(); }
    @Override public List<GameState<HexPosition>> findWhere(Predicate<GameState<HexPosition>> c) { throw new UnsupportedOperationException(); }
    @Override public <R> List<R> findAndTransform(Predicate<GameState<HexPosition>> c, Function<GameState<HexPosition>, R> t) { throw new UnsupportedOperationException(); }
    @Override public long countWhere(Predicate<GameState<HexPosition>> c) { throw new UnsupportedOperationException(); }
    @Override public boolean deleteById(String id) { throw new UnsupportedOperationException(); }
    @Override public long deleteWhere(Predicate<GameState<HexPosition>> c) { throw new UnsupportedOperationException(); }
    @Override public boolean existsById(String id) { throw new UnsupportedOperationException(); }
    @Override public <R> R executeInTransaction(Function<DataRepository<GameState<HexPosition>, String>, R> op) { throw new UnsupportedOperationException(); }
    @Override public List<GameState<HexPosition>> findWithPagination(int page, int size) { throw new UnsupportedOperationException(); }
    @Override public List<GameState<HexPosition>> findAllSorted(Function<GameState<HexPosition>, ? extends Comparable<?>> k, boolean asc) { throw new UnsupportedOperationException(); }
    @Override public <R> List<R> executeCustomQuery(String query, Function<Object, R> mapper) { throw new UnsupportedOperationException(); }
}
