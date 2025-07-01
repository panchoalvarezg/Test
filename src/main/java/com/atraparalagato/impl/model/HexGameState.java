package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.GameBoard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Estado avanzado del juego para el tablero hexagonal.
 * Debe ser más robusto y sofisticado que ExampleGameState.
 */
public class HexGameState extends GameState {
    private final String gameId;
    private GameStatus status;
    private HexPosition catPosition;
    private final HexGameBoard gameBoard;
    private int moveCount;
    private int boardSize;
    private int score;

    public enum GameStatus {
        IN_PROGRESS,
        PLAYER_WON,
        CAT_ESCAPED
    }

    public HexGameState(int boardSize) {
        this.gameId = UUID.randomUUID().toString();
        this.status = GameStatus.IN_PROGRESS;
        this.boardSize = boardSize;
        this.gameBoard = new HexGameBoard(boardSize);
        this.catPosition = new HexPosition(boardSize / 2, boardSize / 2);
        this.moveCount = 0;
        this.score = 0;
    }

    public HexGameState(String gameId, GameStatus status, int boardSize, HexGameBoard board, HexPosition catPosition, int moveCount, int score) {
        this.gameId = gameId;
        this.status = status;
        this.boardSize = boardSize;
        this.gameBoard = board;
        this.catPosition = catPosition;
        this.moveCount = moveCount;
        this.score = score;
    }

    @Override
    public String getGameId() {
        return gameId;
    }

    @Override
    public HexGameBoard getGameBoard() {
        return gameBoard;
    }

    @Override
    public HexPosition getCatPosition() {
        return catPosition;
    }

    public void setCatPosition(HexPosition catPosition) {
        this.catPosition = catPosition;
    }

    @Override
    public int getMoveCount() {
        return moveCount;
    }

    public void incrementMoveCount() {
        moveCount++;
    }

    @Override
    public boolean isGameFinished() {
        return status == GameStatus.PLAYER_WON || status == GameStatus.CAT_ESCAPED;
    }

    @Override
    public boolean hasPlayerWon() {
        return status == GameStatus.PLAYER_WON;
    }

    @Override
    public void updateGameStatus() {
        if (gameBoard.isCatTrapped(catPosition)) {
            status = GameStatus.PLAYER_WON;
            calculateScore();
        } else if (gameBoard.isAtEdge(catPosition)) {
            status = GameStatus.CAT_ESCAPED;
        } else {
            status = GameStatus.IN_PROGRESS;
        }
    }

    @Override
    public void performMove(Object move) {
        if (!(move instanceof HexPosition)) {
            throw new IllegalArgumentException("Move must be a HexPosition");
        }
        HexPosition pos = (HexPosition) move;
        if (canExecuteMove(pos)) {
            gameBoard.blockPosition(pos);
            incrementMoveCount();
            updateGameStatus();
        } else {
            throw new IllegalArgumentException("Invalid move");
        }
    }

    @Override
    public boolean canExecuteMove(Object move) {
        if (!(move instanceof HexPosition)) return false;
        HexPosition pos = (HexPosition) move;
        return gameBoard.isPositionInBounds(pos) && !gameBoard.isBlocked(pos.getQ(), pos.getR());
    }

    @Override
    public int calculateScore() {
        // Ejemplo: mayor score si el gato es atrapado en menos movimientos
        if (status == GameStatus.PLAYER_WON) {
            this.score = Math.max(100 - moveCount * 5, 10);
        } else {
            this.score = 0;
        }
        return this.score;
    }

    @Override
    public Map<String, Object> getSerializableState() {
        Map<String, Object> map = new HashMap<>();
        map.put("gameId", gameId);
        map.put("status", status.name());
        if (catPosition != null) {
            map.put("catPosition", Map.of("q", catPosition.getQ(), "r", catPosition.getR()));
        } else {
            map.put("catPosition", null);
        }
        map.put("blockedCells", gameBoard.getBlockedPositions());
        map.put("movesCount", moveCount);
        map.put("boardSize", boardSize);
        map.put("score", score);
        map.put("implementation", "impl");
        return map;
    }

    // Puedes agregar métodos auxiliares según tus necesidades
}
