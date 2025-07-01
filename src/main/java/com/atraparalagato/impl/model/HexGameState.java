// HexGameState.java
package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.GameState;

import java.util.HashMap;
import java.util.Map;

public class HexGameState extends GameState<HexPosition> {

    private HexPosition catPosition;
    private HexGameBoard board;
    private boolean isFinished = false;
    private boolean playerWon = false;
    private int moveCount = 0;
    private int boardSize = 11;

    public HexGameState(HexGameBoard board, HexPosition start) {
        super("hex-game");
        this.board = board;
        this.catPosition = start;
    }

    @Override
    public boolean canExecuteMove(HexPosition position) {
        return board.isValidMove(position);
    }

    @Override
    public boolean performMove(HexPosition position) {
        if (!canExecuteMove(position)) return false;
        board.executeMove(position);
        moveCount++;
        updateGameStatus();
        return true;
    }

    @Override
    public void updateGameStatus() {
        if (!board.isPositionInBounds(catPosition)) {
            isFinished = true;
            playerWon = false;
        } else if (board.isBlocked(catPosition)) {
            isFinished = true;
            playerWon = true;
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
        return isFinished;
    }

    @Override
    public boolean hasPlayerWon() {
        return playerWon;
    }

    @Override
    public int calculateScore() {
        return moveCount;
    }

    @Override
    public Map<String, Object> getSerializableState() {
        Map<String, Object> state = new HashMap<>();
        state.put("cat", Map.of("q", catPosition.getQ(), "r", catPosition.getR()));
        state.put("blocked", board.getPositionsWhere(pos -> !board.isValidMove(pos)));
        state.put("status", isFinished ? (playerWon ? "WON" : "LOST") : "PLAYING");
        return state;
    }

    @Override
    public void restoreFromSerializable(Object state) {
        throw new UnsupportedOperationException("restoreFromSerializable no implementado todavía");
    }

    @Override
    public GameBoard<HexPosition> getBoard() {
        return board;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public void setBoardSize(int size) {
        this.boardSize = size;
    }
}
