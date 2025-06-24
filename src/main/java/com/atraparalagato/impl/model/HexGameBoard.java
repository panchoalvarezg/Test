package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// TODO: [REVISAR QUE ESTO SE CUMPLA]
/**
 * Implementación esqueleto de GameBoard para tableros hexagonales.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar:
 * - Modularización: Separación de lógica de tablero hexagonal
 * - OOP: Herencia y polimorfismo
 * - Programación Funcional: Uso de Predicate y streams
 */
public class HexGameBoard extends GameBoard<HexPosition> {
	
	public HexGameBoard(int size) {
		super(size);
	}
	
	// Inicializa la lista de posiciones bloqueadas como un LinkedHashSet
	@Override
	protected Set<HexPosition> initializeBlockedPositions() {
		// Se usa un LinkedHashSet por su eficiente rendimiento y por mantener el orden de inserción, que es útil para implementar una función de undo (deshacer) en el juego
		return new LinkedHashSet<>();
	}

	// Reinicia y reasigna las posiciones bloqueadas del mapa según el set entregado
	public void setBlockedPositions(Set<HexPosition> blockedPositions) {
		this.blockedPositions = blockedPositions;
	}

	// Verifica si una posición ya está bloqueada al revisar si la posición está en el LinkedHashSet de posiciones bloqueadas
	@Override
	public boolean isBlocked(HexPosition position) {
		return blockedPositions.contains(position);
	}
	
	// Verifica si una posición dada está dentro de los bordes del mapa al comparar si alguna variable sobrepasa el límite del tamaño del mapa
	@Override
	protected boolean isPositionInBounds(HexPosition position) {
		return Math.abs(position.getQ()) <= size &&
			Math.abs(position.getR()) <= size &&
			Math.abs(position.getS()) <= size;
	}

	// Verifica si una posición es válida para que el jugador la bloquee, desde la perspectiva del tablero de juego
	@Override
	protected boolean isValidMove(HexPosition position) {
		return isPositionInBounds(position) &&
			!isBlocked(position) &&
			!position.isAtBorder(size);
	}
	
	// Agrega la posición entregada a las posiciones bloqueadas, no verifica ninguna condición más porque en el contexto de uso ya se validará que la acción es posible antes de ejecutarla
	@Override
	protected void executeMove(HexPosition position) {
		blockedPositions.add(position);
	}
	
	// Obtiene todas las posiciones con las que puede interactuar el jugador
	private List<HexPosition> getAllPossiblePlayerPositions() {
		// Inicializa la lista en la que se guardarán las posiciones
		List<HexPosition> _positions = new ArrayList<>();

		// Itera sobre el rango de q y r entre -size + 1 y size - 1 verificando si las posiciones están dentro del rango del jugador, y si es que sí, las agrega a la lista final de posibles posiciones para el jugador
		for (int q = -size + 1; q < size; q++) {
			for (int r = -size + 1; r < size; r++) {
				HexPosition _position = new HexPosition(q, r);

				if (isPositionInBounds(_position) && !_position.isAtBorder(size)) {
					_positions.add(_position);
				}
			}
		}
		
		return _positions;
	}

	// Filtra todas las posiciones con las que puede interactuar el jugador según un filtro (Predicate) dado, y regresa una lista mutable de las posiciones que pasen ese filtro
	@Override
	public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
		return getAllPossiblePlayerPositions().stream()
			.filter(condition)
			.collect(Collectors.toList());
	}
	
	// Obtiene las posiciones adyacentes a una posición dada mientras estas estén en el rango accesible para el jugador y este no las haya bloqueado
	@Override
	public List<HexPosition> getAdjacentPositions(HexPosition position) {
		// Lista de vectores unitarios en las 6 posibles direcciones desde una misma casilla hexagonal
		HexPosition[] _directions = {
			new HexPosition(1, 0),	// Eje \ - Derecha-Abajo: \>
			new HexPosition(1, -1),	// Eje / - Derecha-Arriba: />
			new HexPosition(0, -1),	// Eje | - Arriba: ↑
			new HexPosition(-1, 0),	// Eje \ - Izquierda-Arriba: <\
			new HexPosition(-1, 1),	// Eje / - Izquierda-Abajo: </
			new HexPosition(0, 1)	// Eje | - Abajo: ↓
		};

		return Arrays.stream(_directions)
			.map((_direction) -> (HexPosition) position.add(_direction))
			.filter(this::isPositionInBounds)
			.filter(Predicate.not(this::isBlocked))
			.collect(Collectors.toList());
	}
	
	// Hook method override - 
	@Override
	protected void onMoveExecuted(HexPosition position) {
		// TODO: [REVISAR SI AQUÍ SE PODRÍA CALCULAR EL PUNTAJE DE ESE MOVIMIENTO PARA AÑADIRLO AL TOTAL]
		// Ejemplos: logging, notificaciones, validaciones post-movimiento
		super.onMoveExecuted(position);
	}
} 