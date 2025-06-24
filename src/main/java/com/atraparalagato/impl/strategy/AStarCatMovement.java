package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación esqueleto de estrategia de movimiento usando algoritmo A*.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar:
 * - Algoritmos: A* pathfinding
 * - Programación Funcional: Function, Predicate
 * - Estructuras de Datos: PriorityQueue, Map, Set
 */
public class AStarCatMovement extends CatMovementStrategy<HexPosition> {
	
	public AStarCatMovement(GameBoard<HexPosition> board) {
		super(board);
	}
	
	// Obtiene las posiciones posible a las que se puede mover el gato en siguiente turno (es decir, cualquier casilla adyacente que no esté bloqueada)
	@Override
	protected List<HexPosition> getPossibleMoves(HexPosition currentPosition) {
		return board.getAdjacentPositions(currentPosition).stream()
			.filter(Predicate.not(board::isBlocked))
			.collect(Collectors.toList());
	}
	
	// Obtiene el mejor movimiento de entre las opciones según la heurística, seleccionando el menor costo que se encuentre
	@Override
	protected Optional<HexPosition> selectBestMove(List<HexPosition> possibleMoves, 
													HexPosition currentPosition, 
													HexPosition targetPosition) {
		if (possibleMoves.isEmpty()) {
			return Optional.empty();
		}

		Function<HexPosition, Double> heuristicFunction = getHeuristicFunction(targetPosition);

		return possibleMoves.stream()
			.min(Comparator.comparing(heuristicFunction::apply));
	}
	
	// Obtiene la distancia de una posición al borde más cercano
	public double getDistanceToBorder(HexPosition position) {
        return Math.min(
			Math.min(board.getSize() - Math.abs(position.getQ()),
				board.getSize() - Math.abs(position.getR())),
			Math.abs(position.getS()));
    }
	
	// Obtiene la función heurística para A*, la cual utiliza la distancia al borde más cercano como base y favorece las direcciones en las que haya menos casillas bloqueadas
	@Override
	protected Function<HexPosition, Double> getHeuristicFunction(HexPosition targetPosition) {
		return (_position) -> {
			Set<HexPosition> blockedPositions = board.getBlockedPositions();
			double blockedPositionsWeight = blockedPositions.stream()
				.map((__position) -> __position.distanceTo(_position))
				.reduce(0.0, Double::sum);
			
			return Math.max(0, getDistanceToBorder(_position) - (blockedPositionsWeight / (2 * blockedPositions.size() * board.getSize())));
		};
	}
	
	// Genera el Predicate que filtra las posiciones a las que el gato debe llegar, es decir, a las posiciones del borde del mapa
	@Override
	protected Predicate<HexPosition> getGoalPredicate() {
		return (_position) -> {
			return _position.isAtBorder(board.getSize());
		};
	}
	
	// Obtiene el costo de moverse de una posición a otra adyacente, esta implementación considera un coste uniforme de una unidad (1.0) para todas las direcciones
	@Override
	protected double getMoveCost(HexPosition from, HexPosition to) {
		return 1.0;
	}
	
	// Verifica si desde una posición dada se puede alcanzar alguna posición del borde del mapa
	@Override
	public boolean hasPathToGoal(HexPosition currentPosition) {
		return !getFullPath(currentPosition, new HexPosition(board.getSize(), 0)).isEmpty();
	}
	
	// Genera la lista del camino completo desde la posición inicial hasta la posición del borde final
	private List<HexPosition> reconstructPath(AStarNode goalNode) {
		List<HexPosition> path = new LinkedList<>();
		AStarNode currentNode = goalNode;

		while (currentNode.parent != null) {
			path.add(currentNode.position);

			currentNode = currentNode.parent;
		}

		Collections.reverse(path);
		return path;
	}
	
	// Algoritmo A* para obtener el camino completo desde la posición de inicio hasta la posición del borde final
	@Override
	public List<HexPosition> getFullPath(HexPosition currentPosition, HexPosition targetPosition) {
		// Inicializa estructuras de datos necesarias para almacenar posiciones y nodos para A*
		PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparing((_Node) -> _Node.fScore));
		Set<HexPosition> closedSet = new HashSet<>();
		Map<HexPosition, AStarNode> allNodes = new HashMap<>();

		// Obtiene la función heurística
		Function<HexPosition, Double> heuristicFunction = getHeuristicFunction(targetPosition);

		// Crea el nodo para A* de la posición inicial
		AStarNode startNode = new AStarNode(currentPosition, 0, heuristicFunction.apply(currentPosition), null);
		openSet.offer(startNode);
		allNodes.put(currentPosition, startNode);

		// Para el nodo con mejor FScore, revisa si es una posición final, y si no lo es, revisa las posiciones vecinas y agrega las no exploradas a la lista con su FScore
		do {
			AStarNode currentNode = openSet.poll();

			if (currentNode.position.equals(targetPosition) || getGoalPredicate().test(currentNode.position)) {
				return reconstructPath(currentNode);
			}

			closedSet.add(currentNode.position);

			for (HexPosition neighbourPosition : getPossibleMoves(currentNode.position)) {
				if (closedSet.contains(neighbourPosition)) continue;

				double neighbourGScore = currentNode.gScore + getMoveCost(currentNode.position, neighbourPosition);

				if (!allNodes.containsKey(neighbourPosition) || neighbourGScore < allNodes.get(neighbourPosition).gScore) {
					double neighbourFScore = neighbourGScore + heuristicFunction.apply(neighbourPosition);

					AStarNode neighbourNode = new AStarNode(neighbourPosition, neighbourGScore, neighbourFScore, currentNode);

					allNodes.put(neighbourPosition, neighbourNode);
				}
			}
		} while (!openSet.isEmpty());


		// Si no hay camino válido para llegar a una posición del borde, se regresa una lista vacía
		return Collections.emptyList();
	}
	
	// Clase auxiliar para nodos del algoritmo A*
	private static class AStarNode {
		public final HexPosition position;
		public final double gScore; // Costo desde inicio
		public final double fScore; // gScore + heurística
		public final AStarNode parent;
		
		public AStarNode(HexPosition position, double gScore, double fScore, AStarNode parent) {
			this.position = position;
			this.gScore = gScore;
			this.fScore = fScore;
			this.parent = parent;
		}
	}
	
	// Hook methods - los estudiantes pueden override para debugging
	@Override
	protected void beforeMovementCalculation(HexPosition currentPosition) {
		// TODO: Opcional - logging, métricas, etc.
		super.beforeMovementCalculation(currentPosition);
	}
	
	@Override
	protected void afterMovementCalculation(Optional<HexPosition> selectedMove) {
		// TODO: Opcional - logging, métricas, etc.
		super.afterMovementCalculation(selectedMove);
	}
} 