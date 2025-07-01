package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Estrategia de movimiento del gato usando BFS.
 */
public class BFSCatMovement implements CatMovementStrategy<HexPosition> {

    private HexGameBoard board;
    private HexGameState gameState;

    public void setBoard(HexGameBoard board) {
        this.board = board;
    }

    public void setGameState(HexGameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public Optional<HexPosition> getNextMove(HexPosition from) {
        Set<HexPosition> visited = new HashSet<>();
        Queue<List<HexPosition>> queue = new LinkedList<>();
        queue.add(List.of(from));
        visited.add(from);

        while (!queue.isEmpty()) {
            List<HexPosition> path = queue.poll();
            HexPosition current = path.get(path.size() - 1);

            if (getGoalPredicate().test(current) && path.size() > 1) {
                return Optional.of(path.get(1));
            }

            for (HexPosition neighbor : board.getNeighbors(current)) {
                if (!visited.contains(neighbor) && !board.getBlockedPositions().contains(neighbor)) {
                    visited.add(neighbor);
                    List<HexPosition> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Predicate<HexPosition> getGoalPredicate() {
        int size = board.getBoardSize();
        return pos -> pos.getQ() == 0 || pos.getQ() == size - 1 || pos.getR() == 0 || pos.getR() == size - 1;
    }

    @Override
    public Function<HexPosition, Integer> getHeuristicFunction() {
        return pos -> 0; // BFS no usa heurística.
    }

    @Override
    public int getMoveCost(HexPosition from, HexPosition to) {
        return 1;
    }
}
