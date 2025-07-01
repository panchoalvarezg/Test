package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

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

    public Set<HexPosition> getBlockedPositions() {
        return new LinkedHashSet<>(blockedPositions);
    }

    public void setBlockedPositions(Set<HexPosition> blocked) {
        blockedPositions.clear();
        blockedPositions.addAll(blocked);
    }
}
