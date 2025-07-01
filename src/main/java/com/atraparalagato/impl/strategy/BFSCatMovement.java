package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Predicate;

/**
 * Estrategia de movimiento del gato usando BFS (camino más corto).
 */
public class BFSCatMovement implements CatMovementStrategy<HexPosition, HexGameBoard> {

    @Override
    public List<HexPosition> getPossibleMoves(HexPosition from, HexGameBoard board) {
        return board.getAdjacentPositions(from).stream()
                .filter(p -> !board.isBlocked(p))
                .toList();
    }

    @Override
    public HexPosition selectBestMove(HexPosition from, HexGameBoard board, List<HexPosition> possibleMoves) {
        List<HexPosition> path = getFullPath(from, board);
        return path.size() > 1 ? path.get(1) : null;
    }

    @Override
    public boolean hasPathToGoal(HexPosition from, HexGameBoard board) {
        return !getFullPath(from, board).isEmpty();
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition from, HexGameBoard board) {
        Predicate<HexPosition> isGoal = pos -> {
            int n = board.getBoardSize();
            return pos.getQ() == 0 || pos.getR() == 0 || pos.getQ() == n - 1 || pos.getR() == n - 1;
        };

        Queue<HexPosition> queue = new ArrayDeque<>();
        Map<HexPosition, HexPosition> parents = new HashMap<>();
        Set<HexPosition> visited = new HashSet<>();
        queue.add(from);
        visited.add(from);

        while (!queue.isEmpty()) {
            HexPosition current = queue.poll();
            if (isGoal.test(current)) {
                return buildPath(current, parents);
            }
            for (HexPosition neighbor : board.getAdjacentPositions(current)) {
                if (!visited.contains(neighbor) && !board.isBlocked(neighbor)) {
                    visited.add(neighbor);
                    parents.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<HexPosition> buildPath(HexPosition goal, Map<HexPosition, HexPosition> parents) {
        LinkedList<HexPosition> path = new LinkedList<>();
        HexPosition current = goal;
        while (current != null) {
            path.addFirst(current);
            current = parents.get(current);
        }
        return path;
    }
}
