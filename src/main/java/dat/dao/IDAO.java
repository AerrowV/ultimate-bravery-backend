package dat.dao;

import java.util.List;

public interface IDAO<T, I> {
    T read(I id);

    List<T> readAll();

    T create(T t);

    T update(I id, T t);

    void delete(I id);
}
