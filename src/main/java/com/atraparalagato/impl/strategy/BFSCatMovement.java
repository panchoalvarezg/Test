package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;

import java.util.*;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class BFSCatMovement extends CatMovementStrategy<HexPosition> {

    public BFSCatMovement(HexGameBoard board) {
        super(board);
    }

    @Override
    protected List<HexPosition> getPossibleMoves(HexPosition currentPosition) {
        HexGameBoard hexBoard = (HexGameBoard) board;
        List<HexPosition> moves = new ArrayList<>();
        for (HexPosition neighbor : hexBoard.getAdjacentPositions(currentPosition)) {
            if (!hexBoard.isBlocked(neighbor)) {
                moves.add(neighbor);
            }
        }
        return moves;
    }

    @Override
    protected Optional<HexPosition> selectBestMove(List<HexPosition> possibleMoves, HexPosition currentPosition, HexPosition targetPosition) {
        if (possibleMoves.isEmpty()) return Optional.empty();
        return Optional.of(possibleMoves.get(0));
    }

    @Override
    protected Function<HexPosition, Double> getHeuristicFunction(HexPosition targetPosition) {
        return pos -> 0.0;
    }

    @Override
    protected Predicate<HexPosition> getGoalPredicate() {
        HexGameBoard hexBoard = (HexGameBoard) board;
        int n = hexBoard.getSize();
        return pos -> pos.getQ() == 0 || pos.getQ() == n-1 || pos.getR() == 0 || pos.getR() == n-1;
    }

    @Override
    protected double getMoveCost(HexPosition from, HexPosition to) {
        return 1.0;
    }

    @Override
    public boolean hasPathToGoal(HexPosition currentPosition) {
        Set<HexPosition> visited = new HashSet<>();
        Queue<HexPosition> queue = new LinkedList<>();
        queue.add(currentPosition);
        Predicate<HexPosition> isGoal = getGoalPredicate();
        HexGameBoard hexBoard = (HexGameBoard) board;

        while (!queue.isEmpty()) {
            HexPosition pos = queue.poll();
            if (isGoal.test(pos)) return true;
            visited.add(pos);
            for (HexPosition neighbor : hexBoard.getAdjacentPositions(pos)) {
                if (!visited.contains(neighbor) && !hexBoard.isBlocked(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition currentPosition, HexPosition targetPosition) {
        List<HexPosition> path = new ArrayList<>();
        path.add(currentPosition);
        return path;
    }
}
