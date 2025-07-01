package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;

import java.util.List;
import java.util.Random;

public class AStarCatMovement extends CatMovementStrategy<HexPosition> {

    private final Random random = new Random();

    @Override
    public HexPosition selectBestMove(HexPosition currentPosition) {
        List<HexPosition> neighbors = board.getAdjacentPositions(currentPosition);
        if (neighbors.isEmpty()) {
            return currentPosition; // no hay movimientos posibles
        }

        // Selección aleatoria por ahora — luego reemplazar con A*
        return neighbors.get(random.nextInt(neighbors.size()));
    }

    // Métodos que pueden implementarse más adelante

    @Override
    protected double getHeuristic(HexPosition from, HexPosition to) {
        return from.distanceTo(to); // distancia Manhattan como heurística
    }

    @Override
    protected boolean isGoal(HexPosition position) {
        // Meta: escapar del borde
        int size = 11;
        return position.getQ() == 0 || position.getR() == 0
            || position.getQ() == size - 1 || position.getR() == size - 1;
    }

    @Override
    public boolean hasPathToGoal(HexPosition from) {
        return !board.getAdjacentPositions(from).isEmpty(); // dummy
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition from) {
        return List.of(); // aún no implementado
    }

    @Override
    public List<HexPosition> getPossibleMoves(HexPosition from) {
        return board.getAdjacentPositions(from);
    }
}
