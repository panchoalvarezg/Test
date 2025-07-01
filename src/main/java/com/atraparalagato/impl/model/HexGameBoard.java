package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.ArrayList;

/**
 * Tablero hexagonal axial donde:
 * - Toda celda visible en el frontend (hexágono axial completo de radio (size-1)/2) es jugable/bloqueable.
 * - El gato solo escapa si sale COMPLETAMENTE del tablero (o sea, si su posición está fuera de isPositionInBounds).
 */
public class HexGameBoard extends GameBoard<HexPosition> {

    public HexGameBoard(int size) {
        super(size);
    }

    @Override
    protected Set<HexPosition> initializeBlockedPositions() {
        return new HashSet<>();
    }

    /**
     * Una posición está en el tablero si pertenece al hexágono axial completo de radio (size-1)/2.
     * Esto permite bloquear y moverse por cualquier celda que el frontend muestre.
     */
    @Override
    protected boolean isPositionInBounds(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        int s = -q - r;
        int radius = (size - 1) / 2;
        int max = Math.max(Math.abs(q), Math.max(Math.abs(r), Math.abs(s)));
        return max <= radius;
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
                int max = Math.max(Math.abs(q), Math.abs(r), Math.abs(s));
                if (max <= radius) {
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

    /**
     * El gato está atrapado si no tiene adyacentes válidos y desbloqueados.
     */
    public boolean isCatTrapped(HexPosition catPos) {
        for (HexPosition n : getAdjacentPositions(catPos)) {
            if (!isBlocked(n)) {
                return false;
            }
        }
        return true;
    }
}
