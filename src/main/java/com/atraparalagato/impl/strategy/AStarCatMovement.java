package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.model.Position;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import java.util.*;

public class AStarCatMovement implements CatMovementStrategy<HexPosition> {

    @Override
    public List<HexPosition> getPossibleMoves(HexPosition from, Object boardObj) {
        HexGameBoard board = (HexGameBoard) boardObj;
        List<HexPosition> moves = new ArrayList<>();
        for (HexPosition neighbor : board.getAdjacentPositions(from)) {
            if (!board.isBlocked(neighbor)) {
                moves.add(neighbor);
            }
        }
        return moves;
    }

    @Override
    public HexPosition selectBestMove(HexPosition from, Object boardObj, List<HexPosition> possibleMoves) {
        // Simple A* heuristic: prefer moves toward the edge (could be improved)
        HexGameBoard board = (HexGameBoard) boardObj;
        int n = board.getSize();
        HexPosition best = null;
        double bestScore = Double.POSITIVE_INFINITY;
        for (HexPosition move : possibleMoves) {
            double score = Math.min(Math.min(move.getQ(), move.getR()), Math.min(n - 1 - move.getQ(), n - 1 - move.getR()));
            if (score < bestScore) {
                bestScore = score;
                best = move;
            }
        }
        return best;
    }
}
