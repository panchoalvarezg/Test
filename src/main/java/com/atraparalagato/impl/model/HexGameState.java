package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.Position;

import java.util.*;

public class HexGameState extends GameState<HexPosition> {

    private HexPosition catPosition;
    private GameBoard<HexPosition> board;
    private boolean isFinished;
    private boolean playerWon;
    private int score;

    public HexGameState(GameBoard<HexPosition> board, HexPosition initialCatPosition) {
        this.board = board;
        this.catPosition = initialCatPosition;
        this.isFinished = false;
        this.playerWon = false;
        this.score = 0;
    }

    @Override
    public boolean canExecuteMove(HexPosition position) {
        return !isFinished && board.isValidMove(position);
    }

    @Override
    public void performMove(HexPosition position) {
        if (!canExecuteMove(position)) {
            throw new IllegalStateException("Movimiento no válido");
        }

        board.executeMove(position);

        if (board.isBlocked(catPosition)) {
            isFinished = true;
            playerWon = true;
        }
    }

    @Override
    public void updateGameStatus() {
        if (board.isBlocked(catPosition)) {
            isFinished = true;
            playerWon = true;
        }

        if (!board.isPositionInBounds(catPosition)) {
            isFinished = true;
            playerWon = false;
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
        return score;
    }

    @Override
    public Map<String, Object> getSerializableState() {
        Map<String, Object> state = new HashMap<>();
        state.put("cat", Map.of("q", catPosition.getQ(), "r", catPosition.getR()));
        state.put("blocked", board.getBlockedPositions());
        state.put("status", isFinished ? (playerWon ? "WON" : "LOST") : "PLAYING");
        return state;
    }

    @Override
    public void restoreFromSerializable(Map<String, Object> state) {
        throw new UnsupportedOperationException("restoreFromSerializable no implementado todavía");
    }

    @Override
    public GameBoard<HexPosition> getBoard() {
        return board;
    }
}
