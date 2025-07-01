
package com.atraparalagato.impl.service;

import com.atraparalagato.base.service.GameService;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.repository.H2GameRepository;
import com.atraparalagato.impl.strategy.AStarCatMovement;

import java.util.Optional;
import java.util.UUID;

public class HexGameService implements GameService<HexPosition> {

    private final H2GameRepository repository = new H2GameRepository();
    private final AStarCatMovement catMovement = new AStarCatMovement();

    @Override
    public HexGameState createNewGame(int boardSize) {
        String gameId = UUID.randomUUID().toString();
        HexGameState state = new HexGameState(gameId, boardSize);
        repository.save(state);
        return state;
    }

    @Override
    public Optional<HexPosition> getSuggestedMove(String gameId) {
        Optional<HexGameState> optional = repository.findById(gameId)
                .map(state -> (HexGameState) state);
        if (optional.isEmpty()) return Optional.empty();

        HexGameState state = optional.get();
        catMovement.setBoard((HexGameBoard) state.getGameBoard());
        catMovement.setGameState(state);

        return Optional.ofNullable(catMovement.getSuggestedMove(state.getCatPosition()));
    }

    @Override
    public Optional<HexGameState> getGameState(String gameId) {
        return repository.findById(gameId).map(state -> (HexGameState) state);
    }

    @Override
    public boolean executeMove(String gameId, HexPosition position) {
        Optional<HexGameState> optional = repository.findById(gameId).map(state -> (HexGameState) state);
        if (optional.isEmpty()) return false;

        HexGameState state = optional.get();
        if (state.performMove(position)) {
            repository.save(state);
            return true;
        }
        return false;
    }

    @Override
    public void moveCat(String gameId, HexPosition nextPosition) {
        Optional<HexGameState> optional = repository.findById(gameId).map(state -> (HexGameState) state);
        optional.ifPresent(state -> {
            state.setCatPosition(nextPosition);
            state.updateGameStatus();
            repository.save(state);
        });
    }
}
