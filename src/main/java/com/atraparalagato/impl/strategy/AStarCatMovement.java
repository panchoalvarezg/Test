package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Estrategia de movimiento del gato usando búsqueda A*.
 */
public class AStarCatMovement implements CatMovementStrategy<HexPosition> {

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
        Set<HexPosition> goals = getGoalPositions();
        if (goals.isEmpty()) return Optional.empty();

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fScore));
        Map<HexPosition, Integer> gScore = new HashMap<>();
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();

        gScore.put(from, 0);
        openSet.add(new Node(from, heuristic(from)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (getGoalPredicate().test(current.position)) {
                return Optional.of(reconstructPath(cameFrom, current.position));
            }

            for (HexPosition neighbor : board.getNeighbors(current.position)) {
                if (board.getBlockedPositions().contains(neighbor)) continue;

                int tentativeG = gScore.get(current.position) + getMoveCost(current.position, neighbor);
                if (tentativeG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current.position);
                    gScore.put(neighbor, tentativeG);
                    int fScore = tentativeG + heuristic(neighbor);
                    openSet.add(new Node(neighbor, fScore));
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
        return this::heuristic;
    }

    @Override
    public int getMoveCost(HexPosition from, HexPosition to) {
        return 1;
    }

    private int heuristic(HexPosition pos) {
        int q = pos.getQ();
        int r = pos.getR();
        int size = board.getBoardSize();
        return Math.min(Math.min(q, r), Math.min(size - 1 - q, size - 1 - r));
    }

    private HexPosition reconstructPath(Map<HexPosition, HexPosition> cameFrom, HexPosition goal) {
        HexPosition current = goal;
        while (cameFrom.containsKey(current) && cameFrom.get(cameFrom.get(current)) != null) {
            current = cameFrom.get(current);
        }
        return current;
    }

    private Set<HexPosition> getGoalPositions() {
        Set<HexPosition> goals = new HashSet<>();
        int size = board.getBoardSize();

        for (int i = 0; i < size; i++) {
            goals.add(new HexPosition(0, i));
            goals.add(new HexPosition(size - 1, i));
            goals.add(new HexPosition(i, 0));
            goals.add(new HexPosition(i, size - 1));
        }

        return goals;
    }

    private static class Node {
        HexPosition position;
        int fScore;

        Node(HexPosition position, int fScore) {
            this.position = position;
            this.fScore = fScore;
        }
    }
}
