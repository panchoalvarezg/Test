package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Estrategia avanzada de movimiento del gato usando A*.
 */
public class AStarCatMovement implements CatMovementStrategy<HexPosition, HexGameBoard> {

    @Override
    public List<HexPosition> getPossibleMoves(HexPosition from, HexGameBoard board) {
        return board.getAdjacentPositions(from).stream()
                .filter(p -> !board.isBlocked(p))
                .toList();
    }

    @Override
    public HexPosition selectBestMove(HexPosition from, HexGameBoard board, List<HexPosition> possibleMoves) {
        return getFullPath(from, board)
                .stream().skip(1).findFirst().orElse(null); // Primer paso hacia el objetivo
    }

    /**
     * Heurística Manhattan para hexágonos (distancia mínima a borde).
     */
    protected Function<HexPosition, Double> getHeuristicFunction(HexGameBoard board) {
        int n = board.getBoardSize();
        return pos -> (double) Math.min(Math.min(pos.getQ(), pos.getR()), Math.min(n - 1 - pos.getQ(), n - 1 - pos.getR()));
    }

    protected Predicate<HexPosition> getGoalPredicate(HexGameBoard board) {
        int n = board.getBoardSize();
        return pos -> pos.getQ() == 0 || pos.getR() == 0 || pos.getQ() == n - 1 || pos.getR() == n - 1;
    }

    @Override
    public boolean hasPathToGoal(HexPosition from, HexGameBoard board) {
        return !getFullPath(from, board).isEmpty();
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition from, HexGameBoard board) {
        Predicate<HexPosition> isGoal = getGoalPredicate(board);
        Function<HexPosition, Double> heuristic = getHeuristicFunction(board);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Map<HexPosition, Node> allNodes = new HashMap<>();
        Set<HexPosition> closed = new HashSet<>();

        Node start = new Node(from, null, 0, heuristic.apply(from));
        open.add(start);
        allNodes.put(from, start);

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (isGoal.test(current.pos)) {
                return reconstructPath(current);
            }
            closed.add(current.pos);

            for (HexPosition neighbor : board.getAdjacentPositions(current.pos)) {
                if (board.isBlocked(neighbor) || closed.contains(neighbor)) continue;
                double tentativeG = current.g + 1;
                Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor));
                if (tentativeG < neighborNode.g) {
                    neighborNode.g = tentativeG;
                    neighborNode.f = neighborNode.g + heuristic.apply(neighbor);
                    neighborNode.parent = current;
                    allNodes.put(neighbor, neighborNode);
                    open.remove(neighborNode);
                    open.add(neighborNode);
                }
            }
        }
        return Collections.emptyList(); // Sin camino
    }

    private List<HexPosition> reconstructPath(Node goal) {
        LinkedList<HexPosition> path = new LinkedList<>();
        Node node = goal;
        while (node != null) {
            path.addFirst(node.pos);
            node = node.parent;
        }
        return path;
    }

    private static class Node {
        HexPosition pos;
        Node parent;
        double g;
        double f;

        Node(HexPosition pos) {
            this(pos, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        Node(HexPosition pos, Node parent, double g, double f) {
            this.pos = pos;
            this.parent = parent;
            this.g = g;
            this.f = f;
        }

        @Override public boolean equals(Object o) {
            return o instanceof Node n && pos.equals(n.pos);
        }

        @Override public int hashCode() {
            return pos.hashCode();
        }
    }
}
