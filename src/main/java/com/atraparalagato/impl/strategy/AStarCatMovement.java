
package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Predicate;

public class AStarCatMovement implements CatMovementStrategy<HexPosition> {

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
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Map<HexPosition, Integer> gScore = new HashMap<>();

        gScore.put(start, 0);
        open.add(new Node(start, heuristic(start), 0));

        Set<HexPosition> visited = new HashSet<>();

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (getGoalPredicate().test(current.position)) {
                return reconstructPath(cameFrom, current.position);
            }
            visited.add(current.position);

            for (HexPosition neighbor : board.getAdjacentPositions(current.position)) {
                if (visited.contains(neighbor)) continue;
                int tentativeG = gScore.getOrDefault(current.position, Integer.MAX_VALUE) + getMoveCost(current.position, neighbor);
                if (tentativeG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current.position);
                    gScore.put(neighbor, tentativeG);
                    int f = tentativeG + heuristic(neighbor);
                    open.add(new Node(neighbor, f, tentativeG));
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

    private int heuristic(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        int size = board.getBoardSize() - 1;
        int toEdge = Math.min(Math.min(q, r), Math.min(size - q, size - r));
        return toEdge;
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
    
    private static class Node {
        HexPosition position;
        int f;
        int g;

        Node(HexPosition pos, int f, int g) {
            this.position = pos;
            this.f = f;
            this.g = g;
        }
    }
}
