package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación avanzada de GameBoard para tablero hexagonal.
 */
public class HexGameBoard extends GameBoard<HexPosition> {

    private final int boardSize;
    private final Set<HexPosition> blockedPositions;

    public HexGameBoard(int boardSize) {
        this.boardSize = boardSize;
        this.blockedPositions = new HashSet<>();
    }

    @Override
    public boolean isPositionInBounds(HexPosition pos) {
        int q = pos.getQ();
        int r = pos.getR();
        return q >= 0 && q < boardSize && r >= 0 && r < boardSize;
    }

    @Override
    public boolean isValidMove(HexPosition pos) {
        return isPositionInBounds(pos) && !isBlocked(pos);
    }

    @Override
    public void executeMove(HexPosition pos) {
        if (!isValidMove(pos)) {
            throw new IllegalArgumentException("Movimiento no válido: " + pos);
        }
        blockedPositions.add(pos);
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        List<HexPosition> result = new ArrayList<>();
        for (int q = 0; q < boardSize; q++) {
            for (int r = 0; r < boardSize; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (condition.test(pos)) {
                    result.add(pos);
                }
            }
        }
        return result;
    }

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

    @Override
    public boolean isBlocked(HexPosition pos) {
        return blockedPositions.contains(pos);
    }

    public int getBoardSize() {
        return boardSize;
    }

    public Set<HexPosition> getBlockedPositions() {
        return Collections.unmodifiableSet(blockedPositions);
    }

    public void setBlockedPositions(Set<HexPosition> blocked) {
        blockedPositions.clear();
        blockedPositions.addAll(blocked);
    }

    public void unblockPosition(HexPosition pos) {
        blockedPositions.remove(pos);
    }

    public void blockPosition(HexPosition pos) {
        blockedPositions.add(pos);
    }
}
