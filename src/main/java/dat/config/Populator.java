package dat.config;


import dat.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class Populator {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            Game game1 = new Game("Counter Strike");

            em.persist(game1);

            Strategy strategy1 = new Strategy("Mid fake", "2 terrorist push mid with flash, the rest waits in b doors until attention is mid, then rush up b ramp.", false, StrategyType.SERIOUS);
            Strategy strategy2 = new Strategy("B split(Cave push)", "First 4 terrorist push cave, 1 waits ramp for them to crunch 'B' site at the same time.", false, StrategyType.AVERAGE);

            em.persist(strategy1);
            em.persist(strategy2);

            Map map1 = new Map("Ancient", game1);
            Map map2 = new Map("Inferno", game1);

            em.persist(map1);
            em.persist(map2);

            strategy1.getMaps().add(map1);
            strategy2.getMaps().add(map1);

            Gun gun1 = new Gun("AK-47", false, game1);

            em.persist(gun1);

            tx.commit();

            System.out.println("Database populated with sample data.");

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}