package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.ArrayList;

public class HexGameBoard extends GameBoard<HexPosition> {

    public HexGameBoard(int size) {
        super(size);
    }

    @Override
    protected Set<HexPosition> initializeBlockedPositions() {
        return new HashSet<>();
    }

    @Override
    protected boolean isPositionInBounds(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        int radius = (size - 1) / 2;
        // Permite posiciones axiales centradas, como usa el frontend (pueden ser negativas)
        // Ejemplo: para size=5, radius=2, admite q y r entre -2 y 2, con |q|<=2, |r|<=2, |q+r|<=2
        return Math.abs(q) <= radius && Math.abs(r) <= radius && Math.abs(-q - r) <= radius;
    }

    @Override
    protected boolean isValidMove(HexPosition position) {
        return isPositionInBounds(position) && !isBlocked(position);
    }

    @Override
    protected void executeMove(HexPosition position) {
        blockedPositions.add(position);
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        List<HexPosition> list = new ArrayList<>();
        int radius = (size - 1) / 2;
        for (int q = -radius; q <= radius; q++) {
            for (int r = -radius; r <= radius; r++) {
                int s = -q - r;
                if (Math.abs(s) <= radius) {
                    HexPosition pos = new HexPosition(q, r);
                    if (condition.test(pos)) {
                        list.add(pos);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] dirs = {
            {1, 0}, {1, -1}, {0, -1},
            {-1, 0}, {-1, 1}, {0, 1}
        };
        List<HexPosition> adj = new ArrayList<>();
        for (int[] d : dirs) {
            HexPosition n = new HexPosition(position.getQ() + d[0], position.getR() + d[1]);
            if (isPositionInBounds(n)) {
                adj.add(n);
            }
        }
        return adj;
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }

    public boolean isCatTrapped(HexPosition catPos) {
        for (HexPosition n : getAdjacentPositions(catPos)) {
            if (!isBlocked(n)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAtEdge(HexPosition pos) {
        int radius = (size - 1) / 2;
        int q = pos.getQ();
        int r = pos.getR();
        int s = -q - r;
        return Math.abs(q) == radius || Math.abs(r) == radius || Math.abs(s) == radius;
    }
}
