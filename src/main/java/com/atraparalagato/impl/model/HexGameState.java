package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;
import java.util.Objects;

public class HexPosition extends Position {
    private final int q;
    private final int r;

    public static final HexPosition[] DIRECTIONS = {
        new HexPosition(1, 0),
        new HexPosition(1, -1),
        new HexPosition(0, -1),
        new HexPosition(-1, 0),
        new HexPosition(-1, 1),
        new HexPosition(0, 1)
    };

    public HexPosition(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() { return q; }
    public int getR() { return r; }

    @Override
    public double distanceTo(Position other) {
        if (!(other instanceof HexPosition o)) return Double.POSITIVE_INFINITY;
        int dq = Math.abs(this.q - o.q);
        int dr = Math.abs(this.r - o.r);
        int ds = Math.abs((-this.q - this.r) - (-o.q - o.r));
        return Math.max(dq, Math.max(dr, ds));
    }

    @Override
    public Position add(Position other) {
        if (!(other instanceof HexPosition o)) throw new IllegalArgumentException();
        return new HexPosition(this.q + o.q, this.r + o.r);
    }

    @Override
    public Position subtract(Position other) {
        if (!(other instanceof HexPosition o)) throw new IllegalArgumentException();
        return new HexPosition(this.q - o.q, this.r - o.r);
    }

    @Override
    public boolean isAdjacentTo(Position other) {
        if (!(other instanceof HexPosition o)) return false;
        for (HexPosition dir : DIRECTIONS) {
            if (o.q == this.q + dir.q && o.r == this.r + dir.r) return true;
        }
        return false;
    }

    @Override
    public boolean isWithinBounds(int maxSize) {
        return q >= 0 && q < maxSize && r >= 0 && r < maxSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HexPosition)) return false;
        HexPosition that = (HexPosition) o;
        return q == that.q && r == that.r;
    }

    @Override
    public String toString() {
        return "HexPosition{" + "q=" + q + ", r=" + r + '}';
    }
}
