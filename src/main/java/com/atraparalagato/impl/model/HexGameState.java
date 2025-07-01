package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;
import java.util.*;
import java.util.function.Predicate;

/**
 * Estado avanzado para el juego Atrapar al Gato.
 */
public class HexGameState extends GameState<HexPosition, HexGameBoard> {

    private HexPosition catPosition;
    private boolean finished;
    private boolean playerWon;
    private int moveCount;
    private int score;

    public HexGameState(HexGameBoard board, HexPosition catStart) {
        super(board);
        this.catPosition = catStart;
        this.finished = false;
        this.playerWon = false;
        this.moveCount = 0;
        this.score = 0;
    }

    @Override
    public boolean canExecuteMove(HexPosition pos) {
        return !board.isBlocked(pos) && board.isPositionInBounds(pos) && !pos.equals(catPosition);
    }

    @Override
    public void performMove(HexPosition pos) {
        if (!canExecuteMove(pos)) throw new IllegalArgumentException("Movimiento inválido");
        board.blockPosition(pos);
        moveCount++;
        updateGameStatus();
    }

    @Override
    public void updateGameStatus() {
        if (catPosition == null) return;
        boolean escape = isAtEdge(catPosition);
        boolean trapped = getAvailableCatMoves().isEmpty();

        if (escape) {
            finished = true;
            playerWon = false;
            score = Math.max(0, 100 - moveCount * 5);
        } else if (trapped) {
            finished = true;
            playerWon = true;
            score = Math.max(0, 200 - moveCount * 8);
        }
    }

    private boolean isAtEdge(HexPosition pos) {
        int q = pos.getQ(), r = pos.getR();
        int n = board.getBoardSize();
        return q == 0 || r == 0 || q == n - 1 || r == n - 1;
    }

    private List<HexPosition> getAvailableCatMoves() {
        return board.getAdjacentPositions(catPosition).stream()
                .filter(p -> !board.isBlocked(p))
                .toList();
    }

    @Override
    public HexPosition getCatPosition() {
        return catPosition;
    }

    @Override
    public void setCatPosition(HexPosition pos) {
        catPosition = pos;
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
        return score;
    }

    @Override
    public Object getSerializableState() {
        Map<String, Object> state = new HashMap<>();
        state.put("cat", catPosition);
        state.put("blocked", new ArrayList<>(board.getBlockedPositions()));
        state.put("finished", finished);
        state.put("playerWon", playerWon);
        state.put("moveCount", moveCount);
        state.put("score", score);
        return state;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void restoreFromSerializable(Object data) {
        if (!(data instanceof Map map)) throw new IllegalArgumentException("Estado serializable inválido");
        this.catPosition = (HexPosition) map.get("cat");
        this.finished = Boolean.TRUE.equals(map.get("finished"));
        this.playerWon = Boolean.TRUE.equals(map.get("playerWon"));
        this.moveCount = (Integer) map.getOrDefault("moveCount", 0);
        this.score = (Integer) map.getOrDefault("score", 0);
        board.setBlockedPositions(new HashSet<>((List<HexPosition>) map.get("blocked")));
    }

    public int getMoveCount() { return moveCount; }
    public void setMoveCount(int count) { moveCount = count; }
}
