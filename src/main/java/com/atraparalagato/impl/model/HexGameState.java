
package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.GameState;

import java.util.*;

public class HexGameState extends GameState<HexPosition> {

    private String gameId;
    private int moveCount = 0;
    private boolean finished = false;
    private boolean playerWon = false;
    private HexPosition catPosition;
    private HexGameBoard board;

    public HexGameState(String gameId, int boardSize) {
        this.gameId = gameId;
        this.board = new HexGameBoard(boardSize);
        this.catPosition = new HexPosition(boardSize / 2, boardSize / 2);
    }

    @Override
    public String getGameId() {
        return gameId;
    }

    @Override
    public boolean canExecuteMove(HexPosition position) {
        return !finished && board.isValidMove(position);
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
            finished = true;
            playerWon = false;
        } else if (board.isBlocked(catPosition)) {
            finished = true;
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
        return finished;
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
        state.put("gameId", gameId);
        state.put("moveCount", moveCount);
        state.put("catPosition", List.of(catPosition.getQ(), catPosition.getR()));
        state.put("blockedPositions", board.getPositionsWhere(p -> !board.isValidMove(p)));
        state.put("boardSize", board.getBoardSize());
        return state;
    }

    @Override
    public void restoreFromSerializable(Object data) {
        throw new UnsupportedOperationException("restoreFromSerializable no implementado aún");
    }

    @Override
    public GameBoard<HexPosition> getGameBoard() {
        return board;
    }

    public void setMoveCount(int count) {
        this.moveCount = count;
    }

    public void setBoardSize(int size) {
        this.board = new HexGameBoard(size);
    }
}
