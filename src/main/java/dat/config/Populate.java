package dat.config;

import dat.entities.*;
import dat.entities.enums.StrategyType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class Populate {
    public static void populate(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Role userRole = new Role("USER");
            Role adminRole = new Role("ADMIN");
            em.persist(userRole);
            em.persist(adminRole);

            User admin = new User("admin", "admin123");
            admin.addRole(adminRole);
            em.persist(admin);

            User testUser = new User("testuser", "test123");
            testUser.addRole(userRole);
            em.persist(testUser);

            Game game1 = new Game("Counter-Strike 2");
            Game game2 = new Game("League of Legends");
            Game game3 = new Game("Overwatch 2");
            em.persist(game1);
            em.persist(game2);
            em.persist(game3);

            Map ancient = new Map("Ancient", game1);
            Map anubis = new Map("Anubis", game1);
            Map dust2 = new Map("Dust 2", game1);
            Map inferno = new Map("Inferno", game1);
            Map mirage = new Map("Mirage", game1);
            Map nuke = new Map("Nuke", game1);
            Map train = new Map("Train", game1);
            em.persist(ancient); em.persist(anubis); em.persist(dust2);
            em.persist(inferno); em.persist(mirage); em.persist(nuke); em.persist(train);

            Strategy a1 = new Strategy("Ancient T Mid Fake", "...", false, StrategyType.SERIOUS);
            Strategy a2 = new Strategy("Ancient T B Split", "...", false, StrategyType.AVERAGE);
            Strategy a3 = new Strategy("Ancient CT Passive Split", "...", false, StrategyType.SERIOUS);
            Strategy a4 = new Strategy("Ancient CT 4 B Hold", "...", false, StrategyType.AVERAGE);

            Strategy an1 = new Strategy("Anubis T Mid to B", "...", false, StrategyType.SERIOUS);
            Strategy an2 = new Strategy("Anubis T Split Temple", "...", false, StrategyType.SERIOUS);
            Strategy an3 = new Strategy("Anubis CT Info Peek", "...", false, StrategyType.AVERAGE);
            Strategy an4 = new Strategy("Anubis CT Totem Boost", "...", false, StrategyType.TROLL);

            Strategy d1 = new Strategy("Dust2 T Mid A Split", "...", false, StrategyType.AVERAGE);
            Strategy d2 = new Strategy("Dust2 T Mid to B", "...", false, StrategyType.AVERAGE);
            Strategy d3 = new Strategy("Dust2 CT Short Rush", "...", false, StrategyType.TROLL);
            Strategy d4 = new Strategy("Dust2 CT Utility Control", "...", false, StrategyType.SERIOUS);

            Strategy i1 = new Strategy("Inferno T Mid Pop", "...", false, StrategyType.SERIOUS);
            Strategy i2 = new Strategy("Inferno T Apps Crunch", "...", false, StrategyType.SERIOUS);
            Strategy i3 = new Strategy("Inferno CT Bedroom Control", "...", false, StrategyType.AVERAGE);
            Strategy i4 = new Strategy("Inferno CT B Stack", "...", false, StrategyType.SERIOUS);

            Strategy m1 = new Strategy("Mirage T Palace Pop", "...", false, StrategyType.AVERAGE);
            Strategy m2 = new Strategy("Mirage T Split B", "...", false, StrategyType.SERIOUS);
            Strategy m3 = new Strategy("Mirage CT Con/Short Control", "...", false, StrategyType.SERIOUS);
            Strategy m4 = new Strategy("Mirage CT B Boost", "...", false, StrategyType.AVERAGE);

            Strategy n1 = new Strategy("Nuke T Squeaky Pop", "...", false, StrategyType.SERIOUS);
            Strategy n2 = new Strategy("Nuke T Ramp to Hell", "...", false, StrategyType.SERIOUS);
            Strategy n3 = new Strategy("Nuke CT Boost Secret", "...", false, StrategyType.AVERAGE);
            Strategy n4 = new Strategy("Nuke CT Trophy Push", "...", false, StrategyType.SERIOUS);

            Strategy t1 = new Strategy("Train T A Main Pop", "...", false, StrategyType.SERIOUS);
            Strategy t2 = new Strategy("Train T Ivy Crunch", "...", false, StrategyType.AVERAGE);
            Strategy t3 = new Strategy("Train CT B Hold", "...", false, StrategyType.SERIOUS);
            Strategy t4 = new Strategy("Train CT Long Push", "...", false, StrategyType.SERIOUS);

            List<Strategy> allStrategies = List.of(
                    a1, a2, a3, a4,
                    an1, an2, an3, an4,
                    d1, d2, d3, d4,
                    i1, i2, i3, i4,
                    m1, m2, m3, m4,
                    n1, n2, n3, n4,
                    t1, t2, t3, t4
            );
            for (Strategy s : allStrategies) {
                s.setGame(game1);
                em.persist(s);
            }

            ancient.getStrategies().addAll(List.of(a1, a2, a3, a4));
            anubis.getStrategies().addAll(List.of(an1, an2, an3, an4));
            dust2.getStrategies().addAll(List.of(d1, d2, d3, d4));
            inferno.getStrategies().addAll(List.of(i1, i2, i3, i4));
            mirage.getStrategies().addAll(List.of(m1, m2, m3, m4));
            nuke.getStrategies().addAll(List.of(n1, n2, n3, n4));
            train.getStrategies().addAll(List.of(t1, t2, t3, t4));

            for (Map map : List.of(ancient, anubis, dust2, inferno, mirage, nuke, train)) {
                for (Strategy s : map.getStrategies()) {
                    s.getMaps().add(map);
                }
            }

            Gun gun1 = new Gun("AK-47", false, game1);
            em.persist(gun1);

            tx.commit();
            System.out.println("Database populated with sample data including maps and strategies.");
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
