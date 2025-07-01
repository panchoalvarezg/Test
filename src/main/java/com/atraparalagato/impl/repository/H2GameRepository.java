package com.atraparalagato.impl.repository;

import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.impl.model.HexGameState;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class H2GameRepository extends DataRepository<HexGameState, String> {

    private final Map<String, HexGameState> storage = new HashMap<>();

    @Override
    public HexGameState save(HexGameState entity) {
        storage.put(entity.getGameId(), entity);
        return entity;
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
            if (condition.test(state)) result.add(state);
        }
        return result;
    }

    @Override
    public <R> List<R> findAndTransform(Predicate<HexGameState> condition, Function<HexGameState, R> transformer) {
        List<R> result = new ArrayList<>();
        for (HexGameState state : storage.values()) {
            if (condition.test(state)) result.add(transformer.apply(state));
        }
        return result;
    }

    @Override
    public long countWhere(Predicate<HexGameState> condition) {
        return storage.values().stream().filter(condition).count();
    }

    @Override
    public boolean deleteById(String id) {
        return storage.remove(id) != null;
    }

    @Override
    public long deleteWhere(Predicate<HexGameState> condition) {
        long initialSize = storage.size();
        storage.values().removeIf(condition);
        return initialSize - storage.size();
    }

    @Override
    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    @Override
    public <R> R executeInTransaction(Function<DataRepository<HexGameState, String>, R> operation) {
        return operation.apply(this);
    }

    @Override
    public List<HexGameState> findWithPagination(int page, int size) {
        List<HexGameState> all = findAll();
        int fromIndex = Math.min(page * size, all.size());
        int toIndex = Math.min(fromIndex + size, all.size());
        return all.subList(fromIndex, toIndex);
    }

    @Override
    public List<HexGameState> findAllSorted(Function<HexGameState, ? extends Comparable<?>> sortKeyExtractor, boolean ascending) {
        List<HexGameState> all = findAll();
        all.sort((o1, o2) -> {
            Comparable<?> key1 = sortKeyExtractor.apply(o1);
            Comparable<?> key2 = sortKeyExtractor.apply(o2);
            int cmp = key1.compareTo(key2);
            return ascending ? cmp : -cmp;
        });
        return all;
    }

    @Override
    public <R> List<R> executeCustomQuery(String query, Function<Object, R> resultMapper) {
        return Collections.emptyList();
    }

    @Override
    protected void initialize() {}

    @Override
    protected void cleanup() {
        storage.clear();
    }
}
