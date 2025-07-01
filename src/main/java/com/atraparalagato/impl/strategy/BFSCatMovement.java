// BFSCatMovement.java
package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;

public class BFSCatMovement extends CatMovementStrategy<HexPosition> {

    @Override
    public HexPosition selectBestMove(HexPosition currentPosition) {
        List<HexPosition> neighbors = board.getAdjacentPositions(currentPosition);
        if (neighbors.isEmpty()) return currentPosition;
        return neighbors.get(0); // devuelve el primero por ahora
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition from, HexPosition to) {
        Queue<List<HexPosition>> queue = new LinkedList<>();
        Set<HexPosition> visited = new HashSet<>();

        queue.add(List.of(from));
        visited.add(from);

        while (!queue.isEmpty()) {
            List<HexPosition> path = queue.poll();
            HexPosition last = path.get(path.size() - 1);
            if (last.equals(to)) return path;
            for (HexPosition neighbor : board.getAdjacentPositions(last)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    List<HexPosition> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }
        return List.of(); // sin camino
    }

    @Override
    public boolean hasPathToGoal(HexPosition from) {
        return !board.getAdjacentPositions(from).isEmpty();
    }
}
