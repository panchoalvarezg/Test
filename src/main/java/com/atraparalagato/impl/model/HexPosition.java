package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa una posición en un tablero hexagonal usando coordenadas axiales (q, r).
 */
public class HexPosition extends Position implements Serializable {

    private final int q;
    private final int r;

    // Vectores de dirección para hexágonos (arriba, arriba-derecha, abajo-derecha, abajo, abajo-izquierda, arriba-izquierda)
    public static final HexPosition[] DIRECTIONS = {
        new HexPosition(1, 0),    // derecha
        new HexPosition(1, -1),   // arriba-derecha
        new HexPosition(0, -1),   // arriba
        new HexPosition(-1, 0),   // izquierda
        new HexPosition(-1, 1),   // abajo-izquierda
        new HexPosition(0, 1)     // abajo
    };

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

    /**
     * Suma dos posiciones hexagonales (útil para calcular vecinos).
     */
    public HexPosition add(HexPosition other) {
        return new HexPosition(this.q + other.q, this.r + other.r);
    }

    /**
     * Distancia hexagonal (axial) entre dos posiciones.
     */
    public int distanceTo(HexPosition other) {
        int dq = Math.abs(this.q - other.q);
        int dr = Math.abs(this.r - other.r);
        int ds = Math.abs((-this.q - this.r) - (-other.q - other.r));
        return Math.max(dq, Math.max(dr, ds));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HexPosition)) return false;
        HexPosition that = (HexPosition) o;
        return q == that.q && r == that.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    @Override
    public String toString() {
        return "HexPosition{" + "q=" + q + ", r=" + r + '}';
    }
}
