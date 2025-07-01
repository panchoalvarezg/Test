package com.atraparalagato.impl.repository;

import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.impl.model.HexGameState;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class H2GameRepository implements DataRepository<HexGameState, String> {
    private final Map<String, HexGameState> storage = new HashMap<>();

    @Override
    public HexGameState save(HexGameState state) {
        storage.put(state.getGameId(), state);
        return state;
    }

    @Override
    public Optional<HexGameState> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<HexGameState> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void cleanup() {
        storage.clear();
    }
}
