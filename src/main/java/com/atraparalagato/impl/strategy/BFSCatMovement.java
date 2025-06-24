package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación esqueleto de estrategia BFS (Breadth-First Search) para el gato.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar:
 * - Algoritmo BFS para pathfinding
 * - Exploración exhaustiva de caminos
 * - Garantía de encontrar el camino más corto
 * - Uso de colas para exploración por niveles
 */
public class BFSCatMovement extends CatMovementStrategy<HexPosition> {
    
    public BFSCatMovement(GameBoard<HexPosition> board) {
        super(board);
    }
    
    // Retorna las posiciones adyacentes al gato que no están bloqueadas
    @Override
    protected List<HexPosition> getPossibleMoves(HexPosition currentPosition) {
        return board.getAdjacentPositions(currentPosition).stream()
			.filter(Predicate.not(board::isBlocked))
			.collect(Collectors.toList());
    }
    
     // Selecciona el mejor movimiento a partir de las posibles opciones usando BFS
    @Override
    protected Optional<HexPosition> selectBestMove(List<HexPosition> possibleMoves, 
                                                  HexPosition currentPosition, 
                                                  HexPosition targetPosition) {
        // TODO: Usar BFS para encontrar el mejor movimiento
        // 1. Ejecutar BFS desde cada posible movimiento
        // 2. Evaluar cuál lleva más rápido al objetivo
        // 3. Retornar el primer paso del mejor camino
       Optional<List<HexPosition>> bestPath = Optional.empty();
       double bestQuality = Double.MAX_VALUE;

        for (HexPosition move : possibleMoves) {
            // Ejecuta BFS desde cada posible movimiento
            Optional<List<HexPosition>> path = bfsToGoal(move);
            if (path.isPresent()) {
                double quality = evaluatePathQuality(path.get());
                // Selecciona el camino con mejor calidad (más corto en este caso)
                if (bestPath.isEmpty() || quality < bestQuality) {
                    bestPath = path;
                    bestQuality = quality;
                }
            }
        }

        // Devuelve el primer paso del mejor camino encontrado
        return bestPath.map(path -> path.isEmpty() ? null : path.get(0));
    }
    
    @Override
    protected Function<HexPosition, Double> getHeuristicFunction(HexPosition targetPosition) {
        // TODO: BFS no necesita heurística, pero puede usarse para desempate
        // Retornar función que calcule distancia euclidiana o Manhattan
        return pos -> (double) pos.distanceTo(targetPosition);
    }
    
    // Define la condición objetivo: llegar al borde del tablero
    @Override
    protected Predicate<HexPosition> getGoalPredicate() {
        // TODO: Definir condición de objetivo (llegar al borde)
        return pos -> pos.isAtBorder(board.getSize());
    }
    
    // Costo uniforme de movimiento entre casillas adyacentes
    @Override
    protected double getMoveCost(HexPosition from, HexPosition to) {
        // TODO: BFS usa costo uniforme (1.0 para movimientos adyacentes)
        return 1.0;
    }
    
    // Verifica si existe algún camino al borde desde la posición actual
    @Override
    public boolean hasPathToGoal(HexPosition currentPosition) {
        // TODO: Implementar BFS para verificar si existe camino al objetivo
        // 1. Usar cola para exploración por niveles
        // 2. Marcar posiciones visitadas
        // 3. Retornar true si se encuentra el objetivo
        return bfsToGoal(currentPosition).isPresent();
    }
    
    // Devuelve el camino completo desde la posición actual hasta el borde más cercano
    @Override
    public List<HexPosition> getFullPath(HexPosition currentPosition, HexPosition targetPosition) {
        // TODO: Implementar BFS completo para encontrar camino
        // 1. Usar cola con información de camino
        // 2. Reconstruir camino desde objetivo hasta inicio
        // 3. Retornar camino completo
        return bfsToGoal(currentPosition).orElse(Collections.emptyList());
    }
    
    // Métodos auxiliares que los estudiantes pueden implementar
    
    /**
     * TODO: Ejecutar BFS desde una posición hasta encontrar objetivo.
     */
    // Ejecuta BFS desde una posición dada hasta encontrar el objetivo (el borde)
    private Optional<List<HexPosition>> bfsToGoal(HexPosition start) {
        Queue<HexPosition> queue = new LinkedList<>(); // Cola para explorar por niveles
        Map<HexPosition, HexPosition> parentMap = new HashMap<>(); // Almacena el padre de cada nodo para reconstruir camino
        Set<HexPosition> visited = new HashSet<>(); // Marca las posiciones visitadas

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            HexPosition current = queue.poll();

            // Si se alcanza el objetivo (una casilla en el borde), se reconstruye el camino
            if (getGoalPredicate().test(current)) {
                return Optional.of(reconstructPath(parentMap, start, current));
            }

            // Explora vecinos no visitados
            for (HexPosition neighbor : getPossibleMoves(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // No se encontró camino al borde
        return Optional.empty();
    }
    
    /**
     * TODO: Reconstruir camino desde mapa de padres.
     */
     // Reconstruye el camino desde el nodo objetivo hasta el inicio utilizando el mapa de padres
    private List<HexPosition> reconstructPath(Map<HexPosition, HexPosition> parentMap, 
                                            HexPosition start, HexPosition goal) {
        LinkedList<HexPosition> path = new LinkedList<>();
        HexPosition current = goal;

        while (!current.equals(start)) {
            path.addFirst(current);
            current = parentMap.get(current);
        }

        return path; // El camino no incluye el nodo de inicio explícitamente
    }
    
    /**
     * TODO: Evaluar calidad de un camino encontrado.
     */
    // Evalúa la calidad de un camino: mientras más corto, mejor (solo se usa si se desea comparar caminos)
    private double evaluatePathQuality(List<HexPosition> path) {
        return path.size();
    }
} 