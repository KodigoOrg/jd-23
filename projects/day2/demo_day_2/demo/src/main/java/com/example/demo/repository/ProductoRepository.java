package com.example.demo.repository;

import com.example.demo.domain.Producto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductoRepository {

    private final Map<Long, Producto> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public Producto save(Producto producto) {
        if (producto.getId() == null) {
            producto.setId(sequence.getAndIncrement());
        }
        store.put(producto.getId(), producto);
        return producto;
    }

    public Optional<Producto> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Producto> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    public Producto update(Producto producto) {
        store.put(producto.getId(), producto);
        return producto;
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
