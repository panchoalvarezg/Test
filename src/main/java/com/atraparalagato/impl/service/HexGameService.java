package com.atraparalagato.impl.service;

import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.base.strategy.CatMovementStrategy;

import java.util.Optional;

public class HexGameService {

    private final DataRepository<HexGameState, String> repository;
    private final CatMovementStrategy<HexPosition> catStrategy;
    private HexGameState currentState;

    public HexGameService(DataRepository<HexGameState, String> repository,
                          CatMovementStrategy<HexPosition> catStrategy,
                          int boardSize,
                          HexPosition catStart,
                          String gameId) {
        this.repository = repository;
        this.catStrategy = catStrategy;
        HexGameBoard board = new HexGameBoard(boardSize);
        this.currentState = new HexGameState(gameId, board, catStart);
    }

    public boolean executeMove(HexPosition pos) {
        boolean result = currentState.executeMove(pos);
        if (result) catMove();
        return result;
    }

    public void catMove() {
        // Lógica de movimiento del gato sin acceso a getGoalPredicate()
        Optional<HexPosition> best = catStrategy.findBestMove(
                currentState.getCatPosition(),
                null
        );
        best.ifPresent(newPos -> {
            currentState.setCatPosition(newPos);
            // Si necesitas actualizar el estado del juego, hazlo aquí.
        });
    }

    public Optional<HexPosition> getSuggestedMove() {
        return catStrategy.findBestMove(currentState.getCatPosition(), null);
    }

    public HexPosition getTargetPosition() {
        return currentState.getCatPosition();
    }
}
