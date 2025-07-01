
package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Predicate;

public class BFSCatMovement implements CatMovementStrategy<HexPosition> {

    private HexGameBoard board;
    private HexGameState state;

    public void setBoard(HexGameBoard board) {
        this.board = board;
    }

    public void setGameState(HexGameState state) {
        this.state = state;
    }

    @Override
    public HexPosition getSuggestedMove(HexPosition start) {
        Queue<HexPosition> queue = new LinkedList<>();
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Set<HexPosition> visited = new HashSet<>();
        Predicate<HexPosition> isGoal = getGoalPredicate();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            HexPosition current = queue.poll();
            if (isGoal.test(current)) {
                return reconstructPath(cameFrom, current);
            }
            for (HexPosition neighbor : board.getAdjacentPositions(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return null;
    }

    private HexPosition reconstructPath(Map<HexPosition, HexPosition> cameFrom, HexPosition current) {
        while (cameFrom.containsKey(current) && !cameFrom.get(current).equals(state.getCatPosition())) {
            current = cameFrom.get(current);
        }
        return current;
    }

    @Override
    public int getMoveCost(HexPosition from, HexPosition to) {
        return 1;
    }

    @Override
    public Predicate<HexPosition> getGoalPredicate() {
        int size = board.getBoardSize();
        return pos -> pos.getQ() == 0 || pos.getR() == 0 ||
                      pos.getQ() == size - 1 || pos.getR() == size - 1;
    }
}
