package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.ArrayList;

/**
 * Implementación avanzada del tablero hexagonal para el juego Atrapar al Gato.
 * Debe ser más robusta y sofisticada que ExampleGameBoard.
 */
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
        int s = -q - r;
        // Tablero hexagonal axial: q, r, s deben estar en [-radius, radius]
        // Con size = cantidad de filas/columnas, radius = (size - 1) / 2
        int radius = (size - 1) / 2;
        return Math.abs(q) <= radius && Math.abs(r) <= radius && Math.abs(s) <= radius;
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

    // Métodos utilitarios específicos

    public boolean isCatTrapped(HexPosition catPos) {
        // El gato está atrapado si todas sus adyacencias están bloqueadas o fuera del tablero
        for (HexPosition n : getAdjacentPositions(catPos)) {
            if (!isBlocked(n)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAtEdge(HexPosition pos) {
        int radius = (size - 1) / 2;
        return Math.abs(pos.getQ()) == radius || Math.abs(pos.getR()) == radius || Math.abs(-pos.getQ() - pos.getR()) == radius;
    }
}
