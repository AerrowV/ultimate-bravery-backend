package dat.dao.impl;

import dat.dao.IDAO;
import dat.entities.Game;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GameDAO implements IDAO<Game, Long> {

    private static GameDAO instance;
    private static EntityManagerFactory emf;

    public static GameDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GameDAO();
        }
        return instance;
    }

    @Override
    public Game read(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Game.class, id);
        }
    }

    @Override
    public List<Game> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Game> query = em.createQuery("SELECT g FROM Game g", Game.class);
            return query.getResultList();
        }
    }

    @Override
    public Game create(Game game) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(game);
            em.getTransaction().commit();
            return game;
        }
    }

    @Override
    public Game update(Long id, Game updatedData) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Game existing = em.find(Game.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            existing.setName(updatedData.getName());

            em.getTransaction().commit();
            return existing;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Game game = em.find(Game.class, id);
            if (game != null) {
                em.remove(game);
            }
            em.getTransaction().commit();
        }
    }
}
