package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.strategy.AStarCatMovement;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementación esqueleto de GameState para tableros hexagonales.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar:
 * - Estado del juego más sofisticado que ExampleGameState
 * - Sistema de puntuación avanzado
 * - Lógica de victoria/derrota más compleja
 * - Serialización eficiente
 * - Manejo de eventos y callbacks
 */
public class HexGameState extends GameState<HexPosition> {
	
	private HexPosition catPosition;
	private HexGameBoard gameBoard;
	private int boardSize; //TODO: [REVISAR SI AÑADO final DE VUELTA O NO (DEPENDERÁ DEL USO DE restoreFromSerializable)]
	
	// TODO: [AGREGAR ESTOS EXTRAS E INICIALIZARLOS EN EL CONSTRUCTOR]

	// private int gameTime;
	private enum Difficulties {
		EASY,
		HARD
	};
	private Difficulties difficulty;
	
	// Constructor de HexGameState
	public HexGameState(String gameId, int boardSize) {
		super(gameId);
		this.boardSize = boardSize;
		this.catPosition = new HexPosition(0, 0);
		this.gameBoard = new HexGameBoard(boardSize);
	}
	
	// Verifica si una posición es válida para que el jugador la bloquee, considerando todo el estado del juego (es decir: que sea una posición válida desde la perspectiva del tablero, que el gato no esté en esa posición, y que el juego siga en progreso)
	@Override
	protected boolean canExecuteMove(HexPosition position) {
		return gameBoard.isValidMove(position) &&
			!catPosition.equals(position) &&
			getStatus() == GameStatus.IN_PROGRESS;
	}
	
	// Ejecuta el bloqueo de una posición en el tablero
	@Override
	protected boolean performMove(HexPosition position) {
		return gameBoard.makeMove(position);
	}
	
	// Verifica si el gato está en el borde del mapa (en el índice más alto en algún extremo, fuera del alcance del jugador)
	private boolean isCatAtBorder() {
		return catPosition.isAtBorder(boardSize);
	}
	
	// Verifica usando el algoritmo A* si el gato puede llegar a alguna casilla del borde del mapa, y en ese caso regresa false porque no está atrapado, en el caso contrario regresa true
	private boolean isCatTrapped() {
		AStarCatMovement AStarCat = new AStarCatMovement(gameBoard);

		return !AStarCat.hasPathToGoal(catPosition);
	}
	
	// Actualiza el estado del juego: si el gató llegó al borde, el jugador perdió; si el gato está atrapado, el jugador ganó; y en caso de que ninguna haya de las anteriores ocurra, el estado se mantiene (IN_PROGRESS)
	@Override
	protected void updateGameStatus() {
		if (isCatAtBorder()) {
			setStatus(GameStatus.PLAYER_LOST);
		} else if (isCatTrapped()) {
			setStatus(GameStatus.PLAYER_WON);
		}
	}
	
	// Obtiene la posición del gato
	@Override
	public HexPosition getCatPosition() {
		return catPosition;
	}
	
	// Cambia la posición del gato, y cambia el estado del juego si esta nueva posición debe terminar el juego
	@Override
	public void setCatPosition(HexPosition position) {
		this.catPosition = position;

		updateGameStatus();
	}
	
	// Verifica si el juego terminó, revisando el estado del juego
	@Override
	public boolean isGameFinished() {
		return getStatus() != GameStatus.IN_PROGRESS;
	}
	
	// Verifica si el jugador ganó, revisando el estado del juego
	@Override
	public boolean hasPlayerWon() {
		return getStatus() == GameStatus.PLAYER_WON;
	}
	
	@Override
	public int calculateScore() {
		// TODO: [IMPLEMENTAR PUNTAJE ACTUALIZADO EN CADA TURNO DE SER POSIBLE]
		// Considerar factores como:
		// 1. Número de movimientos (menos es mejor)
		// 2. Tiempo transcurrido
		// 3. Tamaño del tablero (más difícil = más puntos)
		// 4. Bonificaciones especiales
		// 5. Penalizaciones por movimientos inválidos
		throw new UnsupportedOperationException("Los estudiantes deben implementar calculateScore");
	}
	
	// Crea un objeto JSON serializado con la información del estado del juego
	// TODO: [INCLUIR DIFICULTAD Y TIEMPO DE JUEGO ACÁ CUANDO SE IMPLEMENTEN]
	@Override
	public Object getSerializableState() {
		JSONObject JSON_Object = new JSONObject();

		JSON_Object.put("gameId", getGameId());
		JSON_Object.put("status", getStatus());
		JSON_Object.put("catPosition", new JSONArray(List.of(catPosition.getQ(), catPosition.getR())));

		JSONArray blockedPositionsArray = new JSONArray(gameBoard.getBlockedPositions().stream()
			.map((_position) -> new JSONArray(List.of(_position.getQ(), _position.getR())))
			.collect(Collectors.toList()));
		JSON_Object.put("blockedPositions", blockedPositionsArray);
		
		JSON_Object.put("moveCount", getMoveCount());
		JSON_Object.put("boardSize", boardSize);
		// JSON_Object.put("score", [getScore()]);
		// JSON_Object.put("difficulty", [getDifficulty()]);
		// JSON_Object.put("elapsedTime", [getTimeElapsed()]);
		
		return JSON_Object;
	}
	
	// Recibe un objeto JSON serializado con la información del estado del juego y extrae esa información
	@Override
	public void restoreFromSerializable(Object serializedState) {
		if (serializedState instanceof JSONObject) {
			try {
				JSONObject JSONState = (JSONObject) serializedState;

				setStatus(GameStatus.valueOf(JSONState.getString("status")));

				JSONArray newCatPositionArray = JSONState.getJSONArray("catPosition");
				catPosition = new HexPosition(newCatPositionArray.getInt(0), newCatPositionArray.getInt(1));

				JSONArray newBlockedPositionsArray = JSONState.getJSONArray("blockedPositions");
				LinkedHashSet<HexPosition> newBlockedPositionsSet = new LinkedHashSet<>();

				for (int i = 0; i < newBlockedPositionsArray.length(); i++) {
					JSONArray positionArray = newBlockedPositionsArray.getJSONArray(i);
					HexPosition position = new HexPosition(positionArray.getInt(0), positionArray.getInt(1));

					newBlockedPositionsSet.add(position);
				}
				
				gameBoard.setBlockedPositions(newBlockedPositionsSet);

				setMoveCount(JSONState.getInt("moveCount"));
				setBoardSize(JSONState.getInt("boardSize"));
			} catch (JSONException error) {
				error.printStackTrace();
				throw new RuntimeException("Error al deserializar estado del juego desde JSON", error);
			}
		} else {
			throw new JSONException("No se recibió un JSONObject para deserializar");
		}
	}
	
	// Métodos auxiliares que los estudiantes pueden implementar
	
	/**
	 * TODO: Calcular estadísticas avanzadas del juego. [¿VAMOS A USAR ESTO?]
	 * Puede incluir métricas como eficiencia, estrategia, etc.
	 */
	public Map<String, Object> getAdvancedStatistics() {
		throw new UnsupportedOperationException("Método adicional para implementar");
	}
	
	// Getters adicionales que pueden ser útiles
	
	public HexGameBoard getGameBoard() {
		return gameBoard;
	}
	
	// Actualiza la cantidad de movimientos ya realizados
	public void setMoveCount(int newMoveCount) {
		moveCount = newMoveCount;
	}

	public int getBoardSize() {
		return boardSize;
	}

	// Actualiza el tamaño del tablero
	// TODO: [REVISAR SI ESTA FUNCIÓN SE QUEDA O SE VA (DEPENDE DE SI boardSize VOLVERÁ A SER final O NO)]
	public void setBoardSize(int newBoardSize) {
		boardSize = newBoardSize;
	}
	
	// TODO: Los estudiantes pueden agregar más métodos según necesiten [MAYBE AGREGAR PARA DIFFICULTY Y TIMEELAPSED]
	// Ejemplos: getDifficulty(), getTimeElapsed(), getPowerUps(), etc.
} 