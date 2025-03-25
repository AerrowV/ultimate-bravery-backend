package dat.dao.impl;

import dat.dao.IDAO;
import dat.entities.Map;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MapDAO implements IDAO<Map, Long> {

    private static MapDAO instance;
    private static EntityManagerFactory emf;

    public static MapDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new MapDAO();
        }
        return instance;
    }

    @Override
    public Map read(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Map.class, id);
        }
    }

    @Override
    public List<Map> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Map> query = em.createQuery("SELECT m FROM Map m", Map.class);
            return query.getResultList();
        }
    }

    @Override
    public Map create(Map map) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(map);
            em.getTransaction().commit();
            return map;
        }
    }

    @Override
    public Map update(Long id, Map updatedData) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Map existing = em.find(Map.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            existing.setName(updatedData.getName());
            existing.setGame(updatedData.getGame());
            existing.setStrategies(updatedData.getStrategies());

            em.getTransaction().commit();
            return existing;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Map map = em.find(Map.class, id);
            if (map != null) {
                em.remove(map);
            }
            em.getTransaction().commit();
        }
    }
}
