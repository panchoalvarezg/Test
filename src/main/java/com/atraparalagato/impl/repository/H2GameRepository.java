package com.atraparalagato.impl.repository;

import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;

import java.sql.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementación esqueleto de DataRepository usando base de datos H2.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar:
 * - Conexión a base de datos H2
 * - Operaciones CRUD con SQL
 * - Manejo de transacciones
 * - Mapeo objeto-relacional
 * - Consultas personalizadas
 * - Manejo de errores de BD
 */
public class H2GameRepository extends DataRepository<HexGameState, String> {
	
	// TODO: Los estudiantes deben definir la configuración de la base de datos
	// Ejemplos: DataSource, JdbcTemplate, EntityManager, etc.

	// Variable para la conexión con la base de datos H2
	private Connection connection; //TODO: RECORDAR AGREGAR connection.close(); DONDE SEA QUE SE CIERRE LA BASE DE DATOS
	
	// Constructor que se conecta e inicializa la base de datos H2
	public H2GameRepository() {
		try {
			this.connection = DriverManager.getConnection("jdbc:h2:file:./data/atrapar-al-gato-db", "sa", "password");

			initialize();
		} catch (SQLException error) {
			error.printStackTrace();
			throw new RuntimeException("Error al conectarse a la base de datos H2", error);
		}
		
		// TODO: Inicializar conexión a H2 y crear tablas si no existen
		// Pista: Usar spring.datasource.url configurado en application.properties
		throw new UnsupportedOperationException("Los estudiantes deben implementar el constructor");
	}

	// Inicializa la tabla de la base de datos H2 con el formato de gameId como llave primaria y un JSON con toda la data de la partida
	@Override
	protected void initialize() {// TODO: [REVISAR LARGO DE gameId, VERIFICAR QUE FUNCIONA JSON]
		String query = """
			CREATE TABLE IF NOT EXISTS Games (
			gameId VARCHAR(255) PRIMARY KEY NOT NULL,
			data JSON NOT NULL
			)
			""";

		try {
			Statement statement = connection.createStatement();
			statement.executeQuery(query);
		} catch (SQLException error) {
			error.printStackTrace();
			throw new RuntimeException("Error al inicializar la base de datos H2", error);
		}
	}
	
	// Guarda el estado del juego en la base de datos H2, insertando la nueva fila con los datos correspondientes si el juego con ese gameId no existe en la base de datos, y reemplazando los datos de esa fila si es que sí existe
	@Override
	public HexGameState save(HexGameState entity) {
		if (entity == null) throw new IllegalArgumentException("Estado del juego inexistente para guardar");
		if (!(entity instanceof HexGameState)) throw new IllegalArgumentException("Estado del juego a guardar no es del tipo correcto");

		// Query para verificar si juego con el gameId dado está o no en la base de datos
		String selectQuery = """
			SELECT COUNT(gameId) FROM Games
			WHERE gameId = '""" + entity.getGameId() + "';";

		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(selectQuery);

			JSONObject serializedEntity = (JSONObject) entity.getSerializableState();

			// Hook antes de guardar
			beforeSave(entity);

			// En caso de que sí esté el gameId en la base de datos, los nuevos datos actualizan la fila en la base de datos
			if (resultSet.getInt(1) != 0) {
				// TODO: [REVISAR QUE gameId Y data ESTÉN EN EL FORMATO CORRECTO]
				String updateQuery = """
					UPDATE Games
					SET data = '""" + serializedEntity.toString() + "'" + """
					WHERE gameId = '""" + entity.getGameId() + "';";
				
				statement.executeUpdate(updateQuery);
			} else { // En caso de que no esté el gameId en la base de datos, los nuevos datos se insertan en una nueva fila en la base de datos
				// TODO: [REVISAR QUE gameId Y data ESTÉN EN EL FORMATO CORRECTO]
				String insertQuery = """
					INSERT INTO Games (gameId, data)
					VALUES
					('""" + entity.getGameId() + "', '" + serializedEntity.toString() + "');";
				
				statement.executeUpdate(insertQuery);
			}

			// Hook después de guardar
			afterSave(entity);

			// Regresa la entidad
			return entity;
		} catch (SQLException error) {
			error.printStackTrace();
			throw new RuntimeException("Error al guardar estado del juego", error);
		}
	}
	
	// Obtiene el String del estado del juego serializado en JSON
	private String serializeGameState(HexGameState gameState) {
		return gameState.getSerializableState().toString();
	}
	
	// Deserializa el estado del juego desde un String serializado con formato JSON y crea el objeto de estado del juego con esta nueva información
	private HexGameState deserializeGameState(String serializedData, String gameId) {
		try {
			JSONObject JSONData = new JSONObject(serializedData);

			HexGameState newGameState = new HexGameState(gameId, JSONData.getInt("boardSize"));
			
			JSONArray newCatPositionArray = JSONData.getJSONArray("catPosition");
			newGameState.setCatPosition(new HexPosition(newCatPositionArray.getInt(0), newCatPositionArray.getInt(1)));

			JSONArray newBlockedPositionsArray = JSONData.getJSONArray("blockedPositions");
			LinkedHashSet<HexPosition> newBlockedPositionsSet = new LinkedHashSet<>();

			for (int i = 0; i < newBlockedPositionsArray.length(); i++) {
				JSONArray positionArray = newBlockedPositionsArray.getJSONArray(i);
				HexPosition position = new HexPosition(positionArray.getInt(0), positionArray.getInt(1));

				newBlockedPositionsSet.add(position);
			}
			
			newGameState.getGameBoard().setBlockedPositions(newBlockedPositionsSet);
			
			newGameState.setMoveCount(JSONData.getInt("moveCount"));
			newGameState.setBoardSize(JSONData.getInt("boardSize"));

			return newGameState;
		} catch (JSONException error) {
			error.printStackTrace();
			throw new RuntimeException("Error al deserializar estado del juego desde JSON proveniente de la base de datos", error);
		}
	}

	// 
	@Override
	public Optional<HexGameState> findById(String id) {
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
			}
			else {
				return Optional.empty();
			}
		} catch (SQLException error) {
			error.printStackTrace();
			throw new RuntimeException("Error al buscar juego por id", error);
		} catch (JSONException error) {
			error.printStackTrace();
			throw new RuntimeException("Error al convertir estado del juego en la base de datos a JSON", error);
		}
	}
	
	// 
	@Override
	public List<HexGameState> findAll() {
		// TODO: Obtener todos los juegos de la base de datos
		// Considerar paginación para grandes volúmenes de datos
		throw new UnsupportedOperationException("Los estudiantes deben implementar findAll");
	}
	
	@Override
	public List<HexGameState> findWhere(Predicate<HexGameState> condition) {
		// TODO: Implementar búsqueda con condiciones
		// Opciones:
		// 1. Cargar todos y filtrar en memoria (simple pero ineficiente)
		// 2. Convertir Predicate a SQL WHERE (avanzado)
		// 3. Usar consultas predefinidas para casos comunes
		throw new UnsupportedOperationException("Los estudiantes deben implementar findWhere");
	}
	
	@Override
	public <R> List<R> findAndTransform(Predicate<HexGameState> condition, 
									   Function<HexGameState, R> transformer) {
		// TODO: Buscar y transformar en una operación
		// Puede optimizarse para hacer la transformación en SQL
		throw new UnsupportedOperationException("Los estudiantes deben implementar findAndTransform");
	}
	
	@Override
	public long countWhere(Predicate<HexGameState> condition) {
		// TODO: Contar registros que cumplen condición
		// Preferiblemente usar COUNT(*) en SQL para eficiencia
		throw new UnsupportedOperationException("Los estudiantes deben implementar countWhere");
	}
	
	@Override
	public boolean deleteById(String id) {
		// TODO: Eliminar juego por ID
		// Retornar true si se eliminó, false si no existía
		throw new UnsupportedOperationException("Los estudiantes deben implementar deleteById");
	}
	
	@Override
	public long deleteWhere(Predicate<HexGameState> condition) {
		// TODO: Eliminar múltiples registros según condición
		// Retornar número de registros eliminados
		throw new UnsupportedOperationException("Los estudiantes deben implementar deleteWhere");
	}
	
	@Override
	public boolean existsById(String id) {
		// TODO: Verificar si existe un juego con el ID dado
		// Usar SELECT COUNT(*) para eficiencia
		throw new UnsupportedOperationException("Los estudiantes deben implementar existsById");
	}
	
	@Override
	public <R> R executeInTransaction(Function<DataRepository<HexGameState, String>, R> operation) {
		// TODO: Ejecutar operación en transacción
		// 1. Iniciar transacción
		// 2. Ejecutar operación
		// 3. Commit si exitoso, rollback si error
		// 4. Manejar excepciones apropiadamente
		throw new UnsupportedOperationException("Los estudiantes deben implementar executeInTransaction");
	}
	
	@Override
	public List<HexGameState> findWithPagination(int page, int size) {
		// TODO: Implementar paginación con LIMIT y OFFSET
		// Validar parámetros de entrada
		throw new UnsupportedOperationException("Los estudiantes deben implementar findWithPagination");
	}
	
	@Override
	public List<HexGameState> findAllSorted(Function<HexGameState, ? extends Comparable<?>> sortKeyExtractor, 
										   boolean ascending) {
		// TODO: Implementar ordenamiento
		// Convertir sortKeyExtractor a ORDER BY SQL
		throw new UnsupportedOperationException("Los estudiantes deben implementar findAllSorted");
	}
	
	@Override
	public <R> List<R> executeCustomQuery(String query, Function<Object, R> resultMapper) {
		// TODO: Ejecutar consulta SQL personalizada
		// 1. Validar consulta SQL
		// 2. Ejecutar consulta
		// 3. Mapear resultados usando resultMapper
		// 4. Manejar errores SQL
		throw new UnsupportedOperationException("Los estudiantes deben implementar executeCustomQuery");
	}
	
	@Override
	protected void cleanup() {
		// TODO: Limpiar recursos
		// 1. Cerrar conexiones
		// 2. Limpiar cache si existe
		// 3. Liberar recursos
		throw new UnsupportedOperationException("Los estudiantes deben implementar cleanup");
	}
	
	// Métodos auxiliares que los estudiantes pueden implementar
	
	/**
	 * TODO: Crear el esquema de la base de datos.
	 * Definir tablas, columnas, tipos de datos, restricciones.
	 */
	private void createSchema() {
		throw new UnsupportedOperationException("Método auxiliar para implementar");
	}
	
	/**
	 * TODO: Convertir Predicate a cláusula WHERE SQL.
	 * Implementación avanzada opcional.
	 */
	private String predicateToSql(Predicate<HexGameState> predicate) {
		throw new UnsupportedOperationException("Método auxiliar avanzado para implementar");
	}
} 