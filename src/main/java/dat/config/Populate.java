package dat.config;


import dat.entities.Game;
import dat.entities.Gun;
import dat.entities.Map;
import dat.entities.Strategy;
import dat.entities.enums.StrategyType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class Populate {
    public static void populate(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Game game1 = new Game("Counter Strike 2");
            em.persist(game1);

            Strategy strategy1 = new Strategy("Mid fake", "Two terrorists push mid with a flash. The rest wait at B doors until attention is drawn mid, then rush up B ramp.", false, StrategyType.SERIOUS);
            Strategy strategy2 = new Strategy("B split (Cave push)", "Four terrorists push through cave. One waits at ramp. All crunch B site at the same time.", false, StrategyType.AVERAGE);

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
            System.out.println("Populated database via HTTP request");

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}