package com.atraparalagato.impl.service;

import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import java.util.*;

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
        List<HexPosition> moves = catStrategy.getPossibleMoves(currentState.getCatPosition(), currentState.getBoard());
        HexPosition best = catStrategy.selectBestMove(currentState.getCatPosition(), currentState.getBoard(), moves);
        if (best != null && !currentState.getBoard().isBlocked(best)) {
            currentState.setCatPosition(best);
            currentState.updateGameStatus();
        }
    }

    public HexPosition getSuggestedMove() {
        List<HexPosition> moves = catStrategy.getPossibleMoves(currentState.getCatPosition(), currentState.getBoard());
        return catStrategy.selectBestMove(currentState.getCatPosition(), currentState.getBoard(), moves);
    }

    public HexPosition getTargetPosition() {
        return currentState.getCatPosition();
    }
}
