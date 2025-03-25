package dat.dao.impl;

import dat.dao.IDAO;
import dat.entities.Strategy;
import dat.entities.StrategyType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Random;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class StrategyDAO implements IDAO<Strategy, Long> {

    private static StrategyDAO instance;
    private static EntityManagerFactory emf;

    public static StrategyDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new StrategyDAO();
        }
        return instance;
    }

    @Override
    public Strategy read(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Strategy.class, id);
        }
    }

    @Override
    public List<Strategy> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Strategy> query = em.createQuery("SELECT s FROM Strategy s", Strategy.class);
            return query.getResultList();
        }
    }

    @Override
    public Strategy create(Strategy strategy) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(strategy);
            em.getTransaction().commit();
            return strategy;
        }
    }

    @Override
    public Strategy update(Long id, Strategy updatedData) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Strategy existing = em.find(Strategy.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            existing.setTitle(updatedData.getTitle());
            existing.setDescription(updatedData.getDescription());
            existing.setType(updatedData.getType());

            em.getTransaction().commit();
            return existing;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Strategy strategy = em.find(Strategy.class, id);
            if (strategy != null) {
                em.remove(strategy);
            }
            em.getTransaction().commit();
        }
    }

    public Strategy getRandomByMapAndType(Long mapId, StrategyType type) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Strategy> query = em.createQuery(
                    "SELECT s FROM Strategy s JOIN s.maps m WHERE m.id = :mapId AND s.type = :type",
                    Strategy.class
            );
            query.setParameter("mapId", mapId);
            query.setParameter("type", type);

            List<Strategy> results = query.getResultList();
            if (results.isEmpty()) return null;

            return results.get(new Random().nextInt(results.size()));
        }
    }

    public List<Strategy> getByMapId(Long mapId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Strategy> query = em.createQuery(
                    "SELECT s FROM Strategy s JOIN s.maps m WHERE m.id = :mapId",
                    Strategy.class
            );
            query.setParameter("mapId", mapId);
            return query.getResultList();
        }
    }
}
