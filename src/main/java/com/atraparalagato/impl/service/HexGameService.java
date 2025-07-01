package com.atraparalagato.impl.service;

import com.atraparalagato.base.service.GameService;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.*;
import com.atraparalagato.base.repository.DataRepository;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Servicio avanzado para orquestar la lógica de Atrapar al Gato.
 */
public class HexGameService extends GameService<HexPosition, HexGameBoard, HexGameState> {

    private final DataRepository<HexGameState> repository;
    private final CatMovementStrategy<HexPosition, HexGameBoard> catStrategy;
    private HexGameState currentState;

    public HexGameService(
            DataRepository<HexGameState> repository,
            CatMovementStrategy<HexPosition, HexGameBoard> catStrategy,
            int boardSize,
            HexPosition catStart
    ) {
        this.repository = repository;
        this.catStrategy = catStrategy;
        HexGameBoard board = new HexGameBoard(boardSize);
        this.currentState = new HexGameState(board, catStart);
    }

    @Override
    public void initializeGame(int boardSize, HexPosition catStart) {
        HexGameBoard board = new HexGameBoard(boardSize);
        this.currentState = new HexGameState(board, catStart);
    }

    @Override
    public boolean isValidMove(HexPosition pos) {
        return currentState.canExecuteMove(pos);
    }

    @Override
    public void executeMove(HexPosition pos) {
        currentState.performMove(pos);
        catMove();
    }

    private void catMove() {
        if (currentState.isGameFinished()) return;
        List<HexPosition> moves = catStrategy.getPossibleMoves(currentState.getCatPosition(), currentState.getBoard());
        HexPosition best = catStrategy.selectBestMove(currentState.getCatPosition(), currentState.getBoard(), moves);
        if (best != null && !currentState.getBoard().isBlocked(best)) {
            currentState.setCatPosition(best);
            currentState.updateGameStatus();
        }
    }

    @Override
    public HexPosition getSuggestedMove() {
        List<HexPosition> moves = catStrategy.getPossibleMoves(currentState.getCatPosition(), currentState.getBoard());
        return catStrategy.selectBestMove(currentState.getCatPosition(), currentState.getBoard(), moves);
    }

    @Override
    public HexPosition getTargetPosition() {
        return currentState.getCatPosition();
    }

    @Override
    public Map<String, Object> getGameStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("moves", currentState.getMoveCount());
        stats.put("score", currentState.calculateScore());
        stats.put("finished", currentState.isGameFinished());
        stats.put("playerWon", currentState.hasPlayerWon());
        return stats;
    }

    // Métodos de integración con repositorio
    public void saveState() {
        repository.save(currentState);
    }

    public Optional<HexGameState> loadState(UUID id) {
        return repository.findById(id);
    }

    public List<HexGameState> findStates(Predicate<HexGameState> filter) {
        return repository.findWhere(filter);
    }

    public <R> List<R> transformStates(Predicate<HexGameState> filter, Function<HexGameState, R> transformer) {
        return repository.findAndTransform(filter, transformer);
    }
}
