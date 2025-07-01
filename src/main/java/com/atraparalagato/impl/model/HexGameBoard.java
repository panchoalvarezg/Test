package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import java.util.*;
import java.util.function.Predicate;

public class HexGameBoard extends GameBoard<HexPosition> {

    public HexGameBoard(int size) {
        super(size);
    }

    @Override
    protected Set<HexPosition> initializeBlockedPositions() {
        return new HashSet<>();
    }

    @Override
    protected boolean isPositionInBounds(HexPosition pos) {
        return pos.isWithinBounds(getSize());
    }

    @Override
    protected boolean isValidMove(HexPosition pos) {
        return isPositionInBounds(pos) && !isBlocked(pos);
    }

    @Override
    protected void executeMove(HexPosition pos) {
        getBlockedPositions().add(pos);
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        List<HexPosition> positions = new ArrayList<>();
        for (int q = 0; q < getSize(); q++) {
            for (int r = 0; r < getSize(); r++) {
                HexPosition pos = new HexPosition(q, r);
                if (condition.test(pos)) positions.add(pos);
            }
        }
        return positions;
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition pos) {
        List<HexPosition> result = new ArrayList<>();
        for (HexPosition dir : HexPosition.DIRECTIONS) {
            HexPosition neighbor = (HexPosition) pos.add(dir);
            if (isPositionInBounds(neighbor)) result.add(neighbor);
        }
        return result;
    }

    @Override
    public boolean isBlocked(HexPosition pos) {
        return getBlockedPositions().contains(pos);
    }

    // Método público para acceder a posiciones bloqueadas
    public Set<HexPosition> getBlockedPositionsPublic() {
        return new HashSet<>(getBlockedPositions());
    }

    // Si necesitas limpiar y setear las posiciones bloqueadas desde HexGameState
    public void setBlockedPositions(Collection<HexPosition> positions) {
        getBlockedPositions().clear();
        getBlockedPositions().addAll(positions);
    }
}
