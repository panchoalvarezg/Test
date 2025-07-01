package com.atraparalagato.impl.repository;

import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.impl.model.HexGameState;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class H2GameRepository implements DataRepository<HexGameState> {

    private final Map<String, HexGameState> storage = new HashMap<>();

    @Override
    public void save(HexGameState state) {
        storage.put(state.getGameId(), state);
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
    public List<HexGameState> findWhere(Predicate<HexGameState> condition) {
        List<HexGameState> result = new ArrayList<>();
        for (HexGameState state : storage.values()) {
            if (condition.test(state)) {
                result.add(state);
            }
        }
        return result;
    }

    @Override
    public <R> List<R> findAndTransform(Predicate<HexGameState> filter, Function<HexGameState, R> transformer) {
        List<R> result = new ArrayList<>();
        for (HexGameState state : storage.values()) {
            if (filter.test(state)) {
                result.add(transformer.apply(state));
            }
        }
        return result;
    }

    @Override
    public void executeInTransaction(Runnable action) {
        action.run();
    }
}
