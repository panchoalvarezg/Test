package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;

/**
 * Implementación concreta de Position para coordenadas hexagonales.
 * 
 * Esta es una implementación de ejemplo que los estudiantes pueden usar como referencia
 * o pueden crear su propia implementación de Position.
 * 
 * Conceptos implementados:
 * - OOP: Herencia de la clase base abstracta
 * - Inmutabilidad: Los objetos no cambian después de creados
 * - Encapsulación: Los campos son privados con acceso controlado
 */
public class HexPosition extends Position {
    
    private final int q; // Coordenada axial q
    private final int r; // Coordenada axial r
    
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
    
    public int getS() {
        return -q - r; // Tercera coordenada axial
    }
    
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

    // Verifica si la posición está en cierta profundidad ("anillo" desde el centro)
    public boolean isAtDepth(int depth) {
        return (q + r + getS()) == 2 * depth;
    }

    // Verifica si la posición está en el borde del mapa (en el "anillo" más grande)
    public boolean isAtBorder(int boardSize) {
        return isAtDepth(boardSize);
    }
} 