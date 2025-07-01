package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.ArrayList;

/**
 * Tablero hexagonal axial con área jugable restringida:
 * - Solo se pueden bloquear celdas del anillo central y del anillo de borde "válido" (radius-1).
 * - El anillo más externo (radius) es decorativo: ni se puede bloquear ni el gato puede escapar por ahí.
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
     * Solo permite bloquear y jugar en celdas dentro del anillo central y el anillo de borde válido (radius-1).
     * El anillo más exterior (radius) NO es jugable ni bloqueable.
     */
    @Override
    protected boolean isPositionInBounds(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        int s = -q - r;
        int radius = (size - 1) / 2;
        int max = Math.max(Math.abs(q), Math.max(Math.abs(r), Math.abs(s)));
        // Solo jugables las celdas donde max <= radius-1
        return max <= radius - 1;
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
                int max = Math.max(Math.abs(q), Math.max(Math.abs(r), Math.abs(s)));
                // Solo celdas jugables (no las decorativas exterior ni fuera del hexágono)
                if (max <= radius - 1) {
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
     * El gato está atrapado si todas las adyacentes jugables están bloqueadas.
     */
    public boolean isCatTrapped(HexPosition catPos) {
        for (HexPosition n : getAdjacentPositions(catPos)) {
            if (!isBlocked(n)) {
                return false;
            }
        }
        return true;
    }

    /**
     * El borde válido es el anillo de radio radius-1.
     * El anillo más exterior (radius) es decorativo y no es considerado borde para escape.
     */
    public boolean isAtEdge(HexPosition pos) {
        int q = pos.getQ();
        int r = pos.getR();
        int s = -q - r;
        int radius = (size - 1) / 2;
        int max = Math.max(Math.abs(q), Math.max(Math.abs(r), Math.abs(s)));
        return max == radius - 1;
    }
}
