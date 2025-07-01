package com.atraparalagato.impl.service;

import com.atraparalagato.base.model.Position;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import java.util.*;

public class HexGameService {

    private final DataRepository<HexGameState> repository;
    private final CatMovementStrategy<HexPosition> catStrategy;
    private HexGameState currentState;

    public HexGameService(DataRepository<HexGameState> repository,
                          CatMovementStrategy<HexPosition> catStrategy,
                          int boardSize,
                          HexPosition catStart,
                          String gameId) {
        this.repository = repository;
        this.catStrategy = catStrategy;
        HexGameBoard board = new HexGameBoard(boardSize);
        this.currentState = new HexGameState(gameId, board, catStart);
    }

    public boolean isValidMove(HexPosition pos) {
        return currentState.canExecuteMove(pos);
    }

    public boolean executeMove(HexPosition pos) {
        boolean result = currentState.executeMove(pos);
        if (result) {
            catMove();
        }
        return result;
    }

    public void catMove() {
        List<HexPosition> moves = catStrategy.getPossibleMoves(currentState.getCatPosition(), currentState);
        HexPosition best = catStrategy.selectBestMove(currentState.getCatPosition(), currentState, moves);
        if (best != null && !((HexGameBoard) currentState.board).isBlocked(best)) {
            currentState.setCatPosition(best);
            currentState.updateGameStatus();
        }
    }

    public HexPosition getSuggestedMove() {
        List<HexPosition> moves = catStrategy.getPossibleMoves(currentState.getCatPosition(), currentState);
        return catStrategy.selectBestMove(currentState.getCatPosition(), currentState, moves);
    }

    public HexPosition getTargetPosition() {
        return currentState.getCatPosition();
    }

    public Map<String, Object> getGameStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("moves", currentState.getMoveCount());
        stats.put("score", currentState.calculateScore());
        stats.put("finished", currentState.isGameFinished());
        stats.put("playerWon", currentState.hasPlayerWon());
        return stats;
    }

    public void saveState() {
        repository.save(currentState);
    }

    public Optional<HexGameState> loadState(String id) {
        return repository.findById(id);
    }
}
