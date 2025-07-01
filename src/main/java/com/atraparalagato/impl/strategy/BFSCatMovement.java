package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.model.Position;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import java.util.*;

public class BFSCatMovement implements CatMovementStrategy<HexPosition> {

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
        // BFS: return the first available move
        return possibleMoves.isEmpty() ? null : possibleMoves.get(0);
    }
}
