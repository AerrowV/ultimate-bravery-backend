package dat.dao.impl;

import dat.dao.IDAO;
import dat.entities.Gun;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Random;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GunDAO implements IDAO<Gun, Long> {

    private static GunDAO instance;
    private static EntityManagerFactory emf;

    public static GunDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GunDAO();
        }
        return instance;
    }

    @Override
    public Gun read(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Gun.class, id);
        }
    }

    @Override
    public List<Gun> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Gun> query = em.createQuery("SELECT g FROM Gun g", Gun.class);
            return query.getResultList();
        }
    }

    @Override
    public Gun create(Gun gun) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(gun);
            em.getTransaction().commit();
            return gun;
        }
    }

    @Override
    public Gun update(Long id, Gun updatedData) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Gun existing = em.find(Gun.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            existing.setName(updatedData.getName());
            existing.setGame(updatedData.getGame());

            em.getTransaction().commit();
            return existing;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Gun gun = em.find(Gun.class, id);
            if (gun != null) {
                em.remove(gun);
            }
            em.getTransaction().commit();
        }
    }

    public Gun getRandomByGameId(Long gameId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Gun> query = em.createQuery("SELECT g FROM Gun g WHERE g.game.id = :gameId", Gun.class);
            query.setParameter("gameId", gameId);

            List<Gun> guns = query.getResultList();
            if (guns.isEmpty()) return null;

            return guns.get(new Random().nextInt(guns.size()));
        }
    }
}
