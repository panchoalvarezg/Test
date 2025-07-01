package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import java.util.*;

public class AStarCatMovement implements CatMovementStrategy<HexPosition, HexGameBoard> {

    @Override
    public List<HexPosition> getPossibleMoves(HexPosition from, HexGameBoard board) {
        List<HexPosition> moves = new ArrayList<>();
        for (HexPosition neighbor : board.getAdjacentPositions(from)) {
            if (!board.isBlocked(neighbor)) {
                moves.add(neighbor);
            }
        }
        return moves;
    }

    @Override
    public HexPosition selectBestMove(HexPosition from, HexGameBoard board, List<HexPosition> possibleMoves) {
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
