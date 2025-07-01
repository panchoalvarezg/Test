
package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.*;
import java.util.function.Predicate;

public class HexGameBoard extends GameBoard<HexPosition> {

    private int boardSize;
    private Set<HexPosition> blocked = new HashSet<>();

    public HexGameBoard(int boardSize) {
        this.boardSize = boardSize;
        this.blocked = initializeBlockedPositions();
    }

    @Override
    protected Set<HexPosition> initializeBlockedPositions() {
        return new HashSet<>();
    }

    public void setBlockedPositions(Set<HexPosition> positions) {
        this.blocked = positions;
    }

    @Override
    public boolean isPositionInBounds(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        return q >= 0 && q < boardSize && r >= 0 && r < boardSize;
    }

    @Override
    public boolean isValidMove(HexPosition position) {
        return isPositionInBounds(position) && !blocked.contains(position);
    }

    @Override
    public void executeMove(HexPosition position) {
        blocked.add(position);
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        List<HexPosition> result = new ArrayList<>();
        for (int q = 0; q < boardSize; q++) {
            for (int r = 0; r < boardSize; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (condition.test(pos)) {
                    result.add(pos);
                }
            }
        }
        return result;
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] directions = {
            {+1,  0}, {+1, -1}, { 0, -1},
            {-1,  0}, {-1, +1}, { 0, +1}
        };
        List<HexPosition> adj = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition neighbor = new HexPosition(position.getQ() + dir[0], position.getR() + dir[1]);
            if (isValidMove(neighbor)) {
                adj.add(neighbor);
            }
        }
        return adj;
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return getAdjacentPositions(position).isEmpty();
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }
}
