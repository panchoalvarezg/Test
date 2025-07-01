package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;
import java.util.*;

public class HexGameState extends GameState<HexPosition> {

    private HexPosition catPosition;
    private final HexGameBoard board;
    private boolean playerWon;

    public HexGameState(String gameId, HexGameBoard board, HexPosition catStart) {
        super(gameId);
        this.board = board;
        this.catPosition = catStart;
        this.playerWon = false;
    }

    public HexGameBoard getBoard() {
        return board;
    }

    @Override
    protected boolean canExecuteMove(HexPosition pos) {
        return board.isValidMove(pos) && !pos.equals(catPosition);
    }

    @Override
    protected boolean performMove(HexPosition pos) {
        if (!canExecuteMove(pos)) return false;
        board.executeMove(pos);
        return true;
    }

    @Override
    protected void updateGameStatus() {
        if (isCatAtEdge()) {
            setStatus(GameStatus.PLAYER_LOST);
        } else if (board.getAdjacentPositions(catPosition).stream().allMatch(board::isBlocked)) {
            setStatus(GameStatus.PLAYER_WON);
            playerWon = true;
        }
    }

    private boolean isCatAtEdge() {
        int n = board.getSize();
        int q = catPosition.getQ(), r = catPosition.getR();
        return q == 0 || r == 0 || q == n-1 || r == n-1;
    }

    @Override
    public HexPosition getCatPosition() {
        return catPosition;
    }

    @Override
    public void setCatPosition(HexPosition pos) {
        this.catPosition = pos;
    }

    @Override
    public boolean isGameFinished() {
        return getStatus() == GameStatus.PLAYER_LOST || getStatus() == GameStatus.PLAYER_WON;
    }

    @Override
    public boolean hasPlayerWon() {
        return playerWon;
    }

    @Override
    public int calculateScore() {
        return playerWon ? 200 - getMoveCount() * 8 : 100 - getMoveCount() * 5;
    }

    @Override
    public Object getSerializableState() {
        Map<String, Object> state = new HashMap<>();
        state.put("cat", catPosition);
        state.put("blocked", new ArrayList<>(board.getBlockedPositions()));
        state.put("status", getStatus());
        state.put("moveCount", getMoveCount());
        return state;
    }

    @Override
    public void restoreFromSerializable(Object serializedState) {
        if (!(serializedState instanceof Map map)) return;
        this.catPosition = (HexPosition) map.get("cat");
        this.playerWon = GameStatus.PLAYER_WON.equals(map.get("status"));
        this.moveCount = (Integer) map.getOrDefault("moveCount", 0);
        board.getBlockedPositions().clear();
        board.getBlockedPositions().addAll((Collection<HexPosition>) map.get("blocked"));
    }
}
