package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implementación de GameState para el juego del gato en tablero hexagonal.
 */
public class HexGameState extends GameState<HexPosition> {

    private final HexGameBoard board;
    private HexPosition catPosition;

    public HexGameState(String gameId, int boardSize) {
        super(gameId);
        this.board = new HexGameBoard(boardSize);
        this.catPosition = board.getInitialCatPosition();
    }

    @Override
    public HexGameBoard getGameBoard() {
        return board;
    }

    @Override
    public HexPosition getCatPosition() {
        return catPosition;
    }

    @Override
    public void setCatPosition(HexPosition newPosition) {
        this.catPosition = newPosition;
    }

    @Override
    public JSONObject getSerializableState() {
        JSONObject json = new JSONObject();
        json.put("gameId", getGameId());
        json.put("moveCount", getMoveCount());
        json.put("boardSize", board.getBoardSize());

        JSONArray catPos = new JSONArray();
        catPos.put(catPosition.getQ());
        catPos.put(catPosition.getR());
        json.put("catPosition", catPos);

        JSONArray blocked = new JSONArray();
        for (HexPosition pos : board.getBlockedPositions()) {
            JSONArray pair = new JSONArray();
            pair.put(pos.getQ());
            pair.put(pos.getR());
            blocked.put(pair);
        }
        json.put("blockedPositions", blocked);

        return json;
    }

    @Override
    public void setMoveCount(int moveCount) {
        super.setMoveCount(moveCount);
    }

    public void setBoardSize(int size) {
        // Este método no tiene sentido en tiempo de ejecución ya que el board es final.
        // Lo incluimos para cumplir con las llamadas existentes que podrían requerirlo.
    }
}
