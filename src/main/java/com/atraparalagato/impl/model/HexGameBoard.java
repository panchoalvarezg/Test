package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implementación concreta de GameBoard para un tablero hexagonal.
 */
public class HexGameBoard extends GameBoard<HexPosition> {

    public HexGameBoard(int size) {
        super(size);
    }

    @Override
    public Set<HexPosition> initializeBlockedPositions() {
        return new LinkedHashSet<>();
    }

    @Override
    public HexPosition getInitialCatPosition() {
        int center = getBoardSize() / 2;
        return new HexPosition(center, center);
    }

    @Override
    public Set<HexPosition> getAllPositions() {
        Set<HexPosition> positions = new LinkedHashSet<>();
        for (int q = 0; q < getBoardSize(); q++) {
            for (int r = 0; r < getBoardSize(); r++) {
                positions.add(new HexPosition(q, r));
            }
        }
        return positions;
    }

    @Override
    public boolean isValidPosition(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        return q >= 0 && q < getBoardSize() && r >= 0 && r < getBoardSize();
    }
}
