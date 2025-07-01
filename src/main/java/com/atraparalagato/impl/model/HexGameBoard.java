package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.List;
import java.util.ArrayList;

/**
 * Implementación avanzada de un tablero hexagonal para el juego Atrapar al Gato.
 * Debe ser más robusta y sofisticada que ExampleGameBoard.
 */
public class HexGameBoard extends GameBoard {
    private final int boardSize;
    private final Set<HexPosition> blockedPositions;

    public HexGameBoard(int boardSize) {
        this.boardSize = boardSize;
        this.blockedPositions = new HashSet<>();
        // Puedes inicializar bloqueos predeterminados aquí si lo deseas
    }

    public int getBoardSize() {
        return boardSize;
    }

    @Override
    public boolean isPositionInBounds(Object pos) {
        if (!(pos instanceof HexPosition)) return false;
        HexPosition p = (HexPosition) pos;
        // El tablero es un hexágono: q y r en [0, boardSize-1], y q + r < boardSize*2-1
        int q = p.getQ();
        int r = p.getR();
        return q >= 0 && r >= 0 && q < boardSize && r < boardSize
            && (q + r) >= (boardSize / 2) && (q + r) <= (boardSize * 3 / 2);
    }

    public boolean isPositionInBounds(HexPosition p) {
        int q = p.getQ();
        int r = p.getR();
        return q >= 0 && r >= 0 && q < boardSize && r < boardSize
            && (q + r) >= (boardSize / 2) && (q + r) <= (boardSize * 3 / 2);
    }

    @Override
    public boolean isBlocked(int q, int r) {
        return blockedPositions.contains(new HexPosition(q, r));
    }

    public boolean isBlocked(HexPosition pos) {
        return blockedPositions.contains(pos);
    }

    public void blockPosition(HexPosition pos) {
        blockedPositions.add(pos);
    }

    public Set<HexPosition> getBlockedPositions() {
        return new HashSet<>(blockedPositions);
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<Object> condition) {
        List<HexPosition> positions = new ArrayList<>();
        for (int q = 0; q < boardSize; q++) {
            for (int r = 0; r < boardSize; r++) {
                HexPosition p = new HexPosition(q, r);
                if (isPositionInBounds(p) && condition.test(p)) {
                    positions.add(p);
                }
            }
        }
        return positions;
    }

    public List<HexPosition> getAdjacentPositions(HexPosition pos) {
        // Direcciones hexagonales: [q, r] offsets
        int[][] directions = {
            {1, 0}, {0, 1}, {-1, 1},
            {-1, 0}, {0, -1}, {1, -1}
        };
        List<HexPosition> adj = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition neighbor = new HexPosition(pos.getQ() + dir[0], pos.getR() + dir[1]);
            if (isPositionInBounds(neighbor) && !isBlocked(neighbor)) {
                adj.add(neighbor);
            }
        }
        return adj;
    }

    public boolean isCatTrapped(HexPosition catPos) {
        // El gato está atrapado si todas las adyacencias están bloqueadas o fuera de los límites
        List<HexPosition> adj = getAdjacentPositionsRaw(catPos);
        for (HexPosition n : adj) {
            if (isPositionInBounds(n) && !isBlocked(n)) {
                return false;
            }
        }
        return true;
    }

    private List<HexPosition> getAdjacentPositionsRaw(HexPosition pos) {
        int[][] directions = {
            {1, 0}, {0, 1}, {-1, 1},
            {-1, 0}, {0, -1}, {1, -1}
        };
        List<HexPosition> adj = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition neighbor = new HexPosition(pos.getQ() + dir[0], pos.getR() + dir[1]);
            adj.add(neighbor);
        }
        return adj;
    }

    public boolean isAtEdge(HexPosition pos) {
        // El gato está en el borde si q o r es 0 o boardSize-1
        int q = pos.getQ();
        int r = pos.getR();
        return q == 0 || r == 0 || q == boardSize - 1 || r == boardSize - 1;
    }

    @Override
    public String toString() {
        return "HexGameBoard(" + boardSize + "x" + boardSize + ", blocked=" + blockedPositions.size() + ")";
    }
}
