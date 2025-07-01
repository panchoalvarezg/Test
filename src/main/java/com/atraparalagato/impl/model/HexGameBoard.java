// HexGameBoard.java
package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HexGameBoard extends GameBoard<HexPosition> {

    private final int size = 11;
    private final Set<HexPosition> blockedPositions = new HashSet<>();

    @Override
    protected void initializeBlockedPositions() {
        // No inicialización por defecto
    }

    @Override
    public boolean isPositionInBounds(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        return q >= 0 && q < size && r >= 0 && r < size;
    }

    @Override
    public boolean isValidMove(HexPosition position) {
        return isPositionInBounds(position) && !blockedPositions.contains(position);
    }

    @Override
    public void executeMove(HexPosition position) {
        if (isValidMove(position)) {
            blockedPositions.add(position);
        }
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        List<HexPosition> positions = new ArrayList<>();
        for (int q = 0; q < size; q++) {
            for (int r = 0; r < size; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (condition.test(pos)) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] directions = {
            {+1,  0}, {+1, -1}, { 0, -1},
            {-1,  0}, {-1, +1}, { 0, +1}
        };

        List<HexPosition> adjacents = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition neighbor = new HexPosition(position.getQ() + dir[0], position.getR() + dir[1]);
            if (isPositionInBounds(neighbor) && !blockedPositions.contains(neighbor)) {
                adjacents.add(neighbor);
            }
        }
        return adjacents;
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return getAdjacentPositions(position).isEmpty();
    }
} 
