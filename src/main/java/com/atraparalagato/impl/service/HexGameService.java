package com.atraparalagato.impl.service;

import com.atraparalagato.base.model.GameService;
import com.atraparalagato.impl.model.*;
import com.atraparalagato.impl.strategy.AStarCatMovement;

public class HexGameService extends GameService<HexPosition> {

    private HexGameState gameState;

    @Override
    public void initializeGame() {
        HexGameBoard board = new HexGameBoard();
        HexPosition initialCatPosition = new HexPosition(5, 5); // centro del tablero
        this.gameState = new HexGameState(board, initialCatPosition);

        this.catMovement = new AStarCatMovement(); // estrategia inicial
        this.catMovement.setBoard(board);
        this.catMovement.setGameState(gameState);
    }

    @Override
    public boolean isValidMove(HexPosition position) {
        return gameState.canExecuteMove(position);
    }

    @Override
    public HexPosition getTargetPosition() {
        return catMovement.selectBestMove(gameState.getCatPosition());
    }

    @Override
    public void moveCat(HexPosition position) {
        gameState.setCatPosition(position);
        gameState.updateGameStatus();
    }

    @Override
    public void executePlayerMove(HexPosition position) {
        gameState.performMove(position);
        gameState.updateGameStatus();
    }

    @Override
    public Object getGameState() {
        return gameState.getSerializableState();
    }

    @Override
    public boolean isGameFinished() {
        return gameState.isGameFinished();
    }

    @Override
    public boolean hasPlayerWon() {
        return gameState.hasPlayerWon();
    }

    @Override
    public int getScore() {
        return gameState.calculateScore();
    }
}
