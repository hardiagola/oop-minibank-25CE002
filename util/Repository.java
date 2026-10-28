package util;

import model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Generic repository that stores any type T safely in a HashMap<String, T>.
 * The map key is discovered at runtime by looking for a field marked with @Id.
 */
public class Repository<T> {

    private final HashMap<String, T> store = new HashMap<>();

    // -------------------------------------------------------
    // save() — uses reflection to find the @Id field value
    // -------------------------------------------------------
    public T save(T entity) {
        String id = resolveId(entity);
        store.put(id, entity);
        return entity;
    }

    // @SafeVarargs requires the method to be final (or static/private)
    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) {
            save(item);
        }
    }

    public T findById(String id) {
        return store.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean delete(String id) {
        return store.remove(id) != null;
    }

    public int size() {
        return store.size();
    }

    // -------------------------------------------------------
    // Reflection: walk class hierarchy to find the @Id field
    // -------------------------------------------------------
    private String resolveId(T entity) {
        Class<?> clazz = entity.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(entity);
                        if (value == null) {
                            throw new IllegalArgumentException(
                                    "@Id field is null in: "
                                    + entity.getClass().getSimpleName());
                        }
                        return value.toString();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(
                                "Cannot read @Id field: " + field.getName(), e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        throw new IllegalArgumentException(
                "No @Id field found on: " + entity.getClass().getSimpleName());
    }

    // -------------------------------------------------------
    // Generic static method with bounded type parameter
    // <N extends Number> means any numeric type (Long, Double …)
    // -------------------------------------------------------
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) return 0.0;
        double sum = 0.0;
        for (N value : values) {
            sum += value.doubleValue();
        }
        return sum / values.length;
    }
}
