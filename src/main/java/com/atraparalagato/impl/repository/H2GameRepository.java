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
 * ImplementaciÃ³n de DataRepository usando base de datos H2.
 */
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
            data JSON NOT NULL
            )
            """;
        try {
            Statement statement = connection.createStatement();
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

        String selectQuery = """
            SELECT COUNT(gameId) FROM Games
            WHERE gameId = '""" + hexState.getGameId() + "';";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);

            JSONObject serializedEntity = (JSONObject) hexState.getSerializableState();

            beforeSave(entity);

            if (resultSet.next() && resultSet.getInt(1) != 0) {
                String updateQuery = """
                    UPDATE Games
                    SET data = '""" + serializedEntity.toString() + "'" + """
                    WHERE gameId = '""" + hexState.getGameId() + "';";
                statement.executeUpdate(updateQuery);
            } else {
                String insertQuery = """
                    INSERT INTO Games (gameId, data)
                    VALUES
                    ('""" + hexState.getGameId() + "', '" + serializedEntity.toString() + "');";
                statement.executeUpdate(insertQuery);
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

        String query = """
            SELECT data FROM Games
            WHERE gameId = '""" + id + "'" + """
            LIMIT 1;
            """;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

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
            state.setBoardSize(json.getInt("boardSize"));

            return state;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al deserializar estado del juego", e);
        }
    }

    @Override
    public List<GameState<HexPosition>> findAll() {
        throw new UnsupportedOperationException("Los estudiantes deben implementar findAll");
    }

    @Override
    public List<GameState<HexPosition>> findWhere(Predicate<GameState<HexPosition>> condition) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar findWhere");
    }

    @Override
    public <R> List<R> findAndTransform(Predicate<GameState<HexPosition>> condition, Function<GameState<HexPosition>, R> transformer) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar findAndTransform");
    }

    @Override
    public long countWhere(Predicate<GameState<HexPosition>> condition) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar countWhere");
    }

    @Override
    public boolean deleteById(String id) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar deleteById");
    }

    @Override
    public long deleteWhere(Predicate<GameState<HexPosition>> condition) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar deleteWhere");
    }

    @Override
    public boolean existsById(String id) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar existsById");
    }

    @Override
    public <R> R executeInTransaction(Function<DataRepository<GameState<HexPosition>, String>, R> operation) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar executeInTransaction");
    }

    @Override
    public List<GameState<HexPosition>> findWithPagination(int page, int size) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar findWithPagination");
    }

    @Override
    public List<GameState<HexPosition>> findAllSorted(Function<GameState<HexPosition>, ? extends Comparable<?>> sortKeyExtractor, boolean ascending) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar findAllSorted");
    }

    @Override
    public <R> List<R> executeCustomQuery(String query, Function<Object, R> resultMapper) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar executeCustomQuery");
    }

    @Override
    protected void cleanup() {
        throw new UnsupportedOperationException("Los estudiantes deben implementar cleanup");
    }
}