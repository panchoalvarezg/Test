package com.atraparalagato.impl.service;

import com.atraparalagato.base.service.GameService;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.strategy.AStarCatMovement;

import java.util.Optional;

/**
 * Servicio del juego específico para tablero hexagonal.
 */
public class HexGameService implements GameService<HexPosition> {

    private final AStarCatMovement catMovement;

    public HexGameService() {
        this.catMovement = new AStarCatMovement();
    }

    @Override
    public Optional<HexPosition> getSuggestedMove(String gameId) {
        HexGameState gameState = new HexGameState(gameId, 11);
        catMovement.setBoard(gameState.getGameBoard());
        catMovement.setGameState(gameState);

        return catMovement.getNextMove(gameState.getCatPosition());
    }

    @Override
    public HexGameState createNewGame(String gameId, int boardSize) {
        return new HexGameState(gameId, boardSize);
    }

    @Override
    public void blockPosition(HexGameState state, HexPosition position) {
        state.getGameBoard().blockPosition(position);
    }

    @Override
    public void moveCat(HexGameState state, HexPosition newPosition) {
        state.setCatPosition(newPosition);
        state.setMoveCount(state.getMoveCount() + 1);
    }

    @Override
    public boolean isGameOver(HexGameState state) {
        return catMovement.getGoalPredicate().test(state.getCatPosition()) ||
               catMovement.getNextMove(state.getCatPosition()).isEmpty();
    }
}
