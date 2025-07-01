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
        // Permite todas las celdas que aparecen en el frontend (grid rectangular)
        return q >= 0 && r >= 0 && q < size && r < size;
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
        for (int q = 0; q < size; q++) {
            for (int r = 0; r < size; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (condition.test(pos)) {
                    list.add(pos);
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

    // Extra: útil para lógica de estado
    public boolean isCatTrapped(HexPosition catPos) {
        for (HexPosition n : getAdjacentPositions(catPos)) {
            if (!isBlocked(n)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAtEdge(HexPosition pos) {
        // Está en el borde si alguna coordenada es 0 o size-1
        int q = pos.getQ();
        int r = pos.getR();
        return q == 0 || r == 0 || q == size - 1 || r == size - 1;
    }
}
