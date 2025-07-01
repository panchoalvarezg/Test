package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import java.util.*;

public class BFSCatMovement implements CatMovementStrategy<HexPosition> {
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
        return possibleMoves.isEmpty() ? null : possibleMoves.get(0);
    }
}
