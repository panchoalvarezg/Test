package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;

public class HexPosition extends Position {
    private final int q;
    private final int r;

    public HexPosition(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    // --- AGREGA ESTE MÉTODO ---
    public int getS() {
        return -q - r;
    }
    // --------------------------

    @Override
    public double distanceTo(Position other) {
        if (!(other instanceof HexPosition)) {
            throw new IllegalArgumentException("Cannot calculate distance to non-hex position");
        }
        HexPosition hex = (HexPosition) other;
        return (Math.abs(q - hex.q) + Math.abs(q + r - hex.q - hex.r) + Math.abs(r - hex.r)) / 2.0;
    }

    @Override
    public Position add(Position other) {
        if (!(other instanceof HexPosition)) {
            throw new IllegalArgumentException("Cannot add non-hex position");
        }
        HexPosition hex = (HexPosition) other;
        return new HexPosition(q + hex.q, r + hex.r);
    }

    @Override
    public Position subtract(Position other) {
        if (!(other instanceof HexPosition)) {
            throw new IllegalArgumentException("Cannot subtract non-hex position");
        }
        HexPosition hex = (HexPosition) other;
        return new HexPosition(q - hex.q, r - hex.r);
    }

    @Override
    public boolean isAdjacentTo(Position other) {
        return distanceTo(other) == 1.0;
    }

    @Override
    public boolean isWithinBounds(int maxSize) {
        return Math.abs(q) <= maxSize && Math.abs(r) <= maxSize && Math.abs(getS()) <= maxSize;
    }

    @Override
    public int hashCode() {
        return 31 * q + r;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HexPosition that = (HexPosition) obj;
        return q == that.q && r == that.r;
    }

    @Override
    public String toString() {
        return String.format("HexPosition(q=%d, r=%d, s=%d)", q, r, getS());
    }
}
