package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación avanzada de GameBoard para un tablero hexagonal.
 */
public class HexGameBoard extends GameBoard<HexPosition> {

    private final Set<HexPosition> blockedPositions;

    /**
     * Constructor, inicializa el tablero de tamaño boardSize.
     */
    public HexGameBoard(int boardSize) {
        super(boardSize);
        this.blockedPositions = new HashSet<>();
    }

    /**
     * Inicializa las posiciones bloqueadas si se requiere (implementación base).
     */
    @Override
    protected void initializeBlockedPositions() {
        blockedPositions.clear();
    }

    /**
     * Determina si la posición está dentro de los límites del tablero.
     */
    @Override
    public boolean isPositionInBounds(HexPosition pos) {
        return pos.isWithinBounds(getBoardSize());
    }

    /**
     * Determina si un movimiento a esa posición es válido.
     */
    @Override
    public boolean isValidMove(HexPosition pos) {
        return isPositionInBounds(pos) && !isBlocked(pos);
    }

    /**
     * Ejecuta un movimiento bloqueando la posición.
     */
    @Override
    public void executeMove(HexPosition pos) {
        if (!isValidMove(pos)) {
            throw new IllegalArgumentException("Movimiento no válido: " + pos);
        }
        blockPosition(pos);
    }

    /**
     * Devuelve una lista de posiciones que cumplen una condición.
     */
    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        int n = getBoardSize();
        List<HexPosition> result = new ArrayList<>();
        for (int q = 0; q < n; q++) {
            for (int r = 0; r < n; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (condition.test(pos)) {
                    result.add(pos);
                }
            }
        }
        return result;
    }

    /**
     * Obtiene las posiciones vecinas adyacentes a la dada.
     */
    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition pos) {
        List<HexPosition> neighbors = new ArrayList<>();
        for (HexPosition dir : HexPosition.DIRECTIONS) {
            HexPosition neighbor = pos.add(dir);
            if (isPositionInBounds(neighbor)) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * Indica si la posición está bloqueada.
     */
    @Override
    public boolean isBlocked(HexPosition pos) {
        return blockedPositions.contains(pos);
    }

    /**
     * Bloquea una posición en el tablero.
     */
    public void blockPosition(HexPosition pos) {
        blockedPositions.add(pos);
    }

    /**
     * Desbloquea una posición en el tablero.
     */
    public void unblockPosition(HexPosition pos) {
        blockedPositions.remove(pos);
    }

    /**
     * Devuelve un set inmutable de posiciones bloqueadas.
     */
    public Set<HexPosition> getBlockedPositionsSet() {
        return Collections.unmodifiableSet(blockedPositions);
    }

    /**
     * Permite establecer el set de posiciones bloqueadas (usado en restauración de estado).
     */
    public void setBlockedPositions(Set<HexPosition> positions) {
        blockedPositions.clear();
        blockedPositions.addAll(positions);
    }
}
