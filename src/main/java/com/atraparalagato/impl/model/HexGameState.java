package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;

import java.util.HashMap;
import java.util.Map;

/**
 * Estado avanzado del juego para el tablero hexagonal.
 * Debe ser más robusto y sofisticado que ExampleGameState.
 */
public class HexGameState extends GameState<HexPosition> {
    private HexGameBoard board;
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
        return !isGameFinished() && board.isValidMove(position);
    }

    @Override
    protected boolean performMove(HexPosition position) {
        if (canExecuteMove(position)) {
            board.executeMove(position);
            return true;
        }
        return false;
    }

    @Override
    protected void updateGameStatus() {
        if (board.isCatTrapped(catPosition)) {
            setStatus(GameStatus.PLAYER_WON);
        } else if (board.isAtEdge(catPosition)) {
            setStatus(GameStatus.PLAYER_LOST);
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
        // Implementar restauración desde el objeto serializado según formato guardado
        // Puedes dejarlo como TODO si aún no lo necesitas
    }
}
