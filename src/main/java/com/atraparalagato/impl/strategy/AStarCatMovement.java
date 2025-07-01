// AStarCatMovement.java
package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;

import java.util.List;
import java.util.Random;

public class AStarCatMovement extends CatMovementStrategy<HexPosition> {

    private final Random random = new Random();

    @Override
    public HexPosition selectBestMove(HexPosition currentPosition) {
        List<HexPosition> neighbors = board.getAdjacentPositions(currentPosition);
        if (neighbors.isEmpty()) return currentPosition;
        return neighbors.get(random.nextInt(neighbors.size()));
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition from, HexPosition to) {
        return List.of(); // aún no implementado
    }

    @Override
    public boolean hasPathToGoal(HexPosition from) {
        return !board.getAdjacentPositions(from).isEmpty();
    }

    @Override
    public double getMoveCost(HexPosition from, HexPosition to) {
        return 1.0;
    }
}
