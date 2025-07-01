package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import java.util.*;

public class AStarCatMovement implements CatMovementStrategy<HexPosition> {
    @Override
    public List<HexPosition> getPossibleMoves(HexPosition from, Object board) {
        HexGameBoard hexBoard = (HexGameBoard) board;
        List<HexPosition> moves = new ArrayList<>();
        for (HexPosition neighbor : hexBoard.getAdjacentPositions(from)) {
            if (!hexBoard.isBlocked(neighbor)) {
                moves.add(neighbor);
            }
        }
        return moves;
    }

    @Override
    public HexPosition selectBestMove(HexPosition from, Object board, List<HexPosition> possibleMoves) {
        HexGameBoard hexBoard = (HexGameBoard) board;
        int n = hexBoard.getSize();
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
