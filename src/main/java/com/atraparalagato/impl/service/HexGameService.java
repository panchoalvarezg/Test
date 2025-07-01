// HexGameService.java
package com.atraparalagato.impl.service;

import com.atraparalagato.base.service.GameService;
import com.atraparalagato.impl.model.*;
import com.atraparalagato.impl.strategy.AStarCatMovement;

public class HexGameService extends GameService<HexPosition> {

    private HexGameState gameState;
    private AStarCatMovement catMovement;

    @Override
    public void initializeGame() {
        HexGameBoard board = new HexGameBoard();
        HexPosition catPos = new HexPosition(5, 5);
        this.gameState = new HexGameState(board, catPos);
        this.catMovement = new AStarCatMovement();
        catMovement.setBoard(board);
        catMovement.setGameState(gameState);
    }

    @Override
    public boolean isValidMove(HexPosition position) {
        return gameState.canExecuteMove(position);
    }

    @Override
    public HexPosition getSuggestedMove(String playerId) {
        return catMovement.selectBestMove(gameState.getCatPosition());
    }

    @Override
    public void executePlayerMove(HexPosition position) {
        gameState.performMove(position);
    }

    @Override
    public void moveCat(HexPosition newPosition) {
        gameState.setCatPosition(newPosition);
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
