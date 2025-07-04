package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Estado del juego: el gato solo escapa si su posición está fuera del tablero;
 * no hay borde artificial, sino el "hexágono completo".
 */
public class HexGameState extends GameState<HexPosition> {
    private final HexGameBoard board;
    private HexPosition catPosition;
    private int score;

    public HexGameState(String gameId, HexGameBoard board, HexPosition catPosition) {
        super(gameId);
        this.board = board;
        this.catPosition = catPosition;
        this.score = 0;
    }

    @Override
    protected boolean canExecuteMove(HexPosition position) {
        // Solo permite si la casilla está dentro de los límites, no está bloqueada y el juego no terminó
        return !isGameFinished() && board.isValidMove(position);
    }

    @Override
    protected boolean performMove(HexPosition position) {
        // Bloquear la casilla elegida por el jugador
        board.executeMove(position);

        // Mueve el gato automáticamente después del bloqueo
        HexPosition nextCatPos = chooseCatMove();
        if (nextCatPos != null) {
            setCatPosition(nextCatPos);
        }
        // Retorna true para indicar que la jugada fue válida
        return true;
    }

    /**
     * Movimiento simple: mueve a la primera adyacente libre, si existe.
     * Si no hay ninguna adyacente dentro del tablero, el gato escapará (posición fuera de bounds tras el movimiento).
     */
    private HexPosition chooseCatMove() {
        List<HexPosition> adj = board.getAdjacentPositions(catPosition);
        for (HexPosition neighbor : adj) {
            if (!board.isBlocked(neighbor)) {
                return neighbor;
            }
        }
        // Si no hay adyacentes libres, el gato está atrapado (no se mueve)
        return null;
    }

    @Override
    protected void updateGameStatus() {
        // El gato escapa solo si sale completamente del tablero (posición fuera de bounds)
        if (!board.isPositionInBounds(catPosition)) {
            setStatus(GameStatus.PLAYER_LOST);
        } else if (board.isCatTrapped(catPosition)) {
            setStatus(GameStatus.PLAYER_WON);
        } else {
            setStatus(GameStatus.IN_PROGRESS);
        }
    }

    @Override
    public HexPosition getCatPosition() {
        return catPosition;
    }

    @Override
    public void setCatPosition(HexPosition position) {
        this.catPosition = position;
    }

    @Override
    public boolean isGameFinished() {
        return status == GameStatus.PLAYER_WON || status == GameStatus.PLAYER_LOST;
    }

    @Override
    public boolean hasPlayerWon() {
        return status == GameStatus.PLAYER_WON;
    }

    @Override
    public int calculateScore() {
        // Ejemplo simple: más puntos si atrapas al gato en menos movimientos.
        if (status == GameStatus.PLAYER_WON) {
            this.score = Math.max(100 - getMoveCount() * 5, 10);
        } else {
            this.score = 0;
        }
        return this.score;
    }

    @Override
    public Object getSerializableState() {
        Map<String, Object> map = new HashMap<>();
        map.put("gameId", getGameId());
        map.put("status", getStatus().name());
        if (catPosition != null) {
            map.put("catPosition", Map.of("q", catPosition.getQ(), "r", catPosition.getR()));
        } else {
            map.put("catPosition", null);
        }
        map.put("blockedCells", board.getBlockedPositions());
        map.put("movesCount", getMoveCount());
        map.put("boardSize", board.getSize());
        map.put("score", score);
        map.put("implementation", "impl");
        return map;
    }

    @Override
    public void restoreFromSerializable(Object serializedState) {
        // Implementar restauración desde el objeto serializado según formato guardado si es necesario
    }
}
