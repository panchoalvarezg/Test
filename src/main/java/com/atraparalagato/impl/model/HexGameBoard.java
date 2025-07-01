package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HexGameBoard extends GameBoard<HexPosition> {

    private final Set<HexPosition> blockedPositions;

    public HexGameBoard(int size) {
        super(size);
        this.blockedPositions = new LinkedHashSet<>();
    }

    @Override
    protected Set<HexPosition> initializeBlockedPositions() {
        return blockedPositions;
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }

    @Override
    public boolean isPositionInBounds(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        int s = position.getS();
        int border = getSize() - 1;
        return Math.abs(q) <= border && Math.abs(r) <= border && Math.abs(s) <= border;
    }

    @Override
    public boolean isValidMove(HexPosition position) {
        return isPositionInBounds(position) && !isBlocked(position);
    }

    @Override
    protected void executeMove(HexPosition position) {
        blockedPositions.add(position);
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] deltas = {
            { 1, 0 },
            { 0, 1 },
            { -1, 1 },
            { -1, 0 },
            { 0, -1 },
            { 1, -1 }
        };
        List<HexPosition> neighbors = new ArrayList<>();
        for (int[] d : deltas) {
            HexPosition neighbor = new HexPosition(position.getQ() + d[0], position.getR() + d[1]);
            if (isPositionInBounds(neighbor)) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * Retorna true si el gato está en el borde del tablero.
     */
    public boolean isAtEdge(HexPosition pos) {
        int border = getSize() - 1;
        int q = pos.getQ();
        int r = pos.getR();
        int s = pos.getS();
        return Math.abs(q) == border || Math.abs(r) == border || Math.abs(s) == border;
    }

    /**
     * Retorna true si el gato está completamente atrapado (todas las adyacentes bloqueadas o fuera de rango).
     */
    public boolean isCatTrapped(HexPosition catPos) {
        for (HexPosition neighbor : getAdjacentPositions(catPos)) {
            if (!isBlocked(neighbor)) {
                return false;
            }
        }
        return true;
    }

    // IMPLEMENTACIÓN REQUERIDA POR LA BASE
    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> predicate) {
        // Devuelve todas las posiciones del tablero que cumplen el predicado
        List<HexPosition> result = new ArrayList<>();
        int border = getSize() - 1;
        for (int q = -border; q <= border; q++) {
            for (int r = -border; r <= border; r++) {
                int s = -q - r;
                if (Math.abs(s) <= border) {
                    HexPosition pos = new HexPosition(q, r);
                    if (predicate.test(pos)) {
                        result.add(pos);
                    }
                }
            }
        }
        return result;
    }

    // El método getBlockedPositions() es FINAL en la clase base, así que NO lo sobreescribas.
    // Usa el método de la clase base cuando lo necesites.

    public void setBlockedPositions(Set<HexPosition> blocked) {
        blockedPositions.clear();
        blockedPositions.addAll(blocked);
    }
}
