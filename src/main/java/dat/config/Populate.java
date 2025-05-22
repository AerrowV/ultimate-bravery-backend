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

            Strategy a1 = new Strategy("Ancient T Mid Fake", "2 terrorist push mid with flash, the rest waits in b doors until attention is mid, then rush up b ramp.", false, StrategyType.SERIOUS);
            Strategy a2 = new Strategy("Ancient T B Split", "First 4 terrorist push cave, 1 waits ramp for them to crunch 'B' site at the same time.", false, StrategyType.AVERAGE);
            Strategy a3 = new Strategy("Ancient CT Passive Split", "2 push 'A' site, 1 donut (passive player) and 2 cave(passive player)", false, StrategyType.SERIOUS);
            Strategy a4 = new Strategy("Ancient CT 4 B Hold", "4 b(2 cave, 2 long) and 1 player in mid.", false, StrategyType.AVERAGE);

            Strategy an1 = new Strategy("Anubis T Mid to B", "2 terrorists push mid -> window -> con. 3 terrorists wait 'B' main and push it with con players.", false, StrategyType.SERIOUS);
            Strategy an2 = new Strategy("Anubis T Split Temple", "5 terrorist push mid, first 2 will push mid -> temple -> 'B' site. last 3 will drop window -> con -> 'B'", false, StrategyType.SERIOUS);
            Strategy an3 = new Strategy("Anubis CT Info Peek", "1 b, flashes out 'B' main and peaks for info. 3 mid and 1 'A' site.", false, StrategyType.AVERAGE);
            Strategy an4 = new Strategy("Anubis CT Totem Boost", "3 camera and 2 on b(boost a player on totem)", false, StrategyType.TROLL);

            Strategy d1 = new Strategy("Dust2 T Mid A Split", "2 dark waits with bomb, 3 push mid to 'A' site. Dark players will crawl into b slowly and plant.", false, StrategyType.AVERAGE);
            Strategy d2 = new Strategy("Dust2 T Mid to B", "2 short -> mid -> 'B'. 3 players rush b, but waits for mid attention.", false, StrategyType.AVERAGE);
            Strategy d3 = new Strategy("Dust2 CT Short Rush", "3 rush short with flashes. 2 sit passively on 'B' site.", false, StrategyType.TROLL);
            Strategy d4 = new Strategy("Dust2 CT Utility Control", "1 goes to smoke 'B' tunnels then goes b and flashes mid. 2 long players(passive) and 2 short players(aggressive)", false, StrategyType.SERIOUS);

            Strategy i1 = new Strategy("Inferno T Mid Pop", "5 mid(smoke short) last man flashes long and all players move through smoke short into site.", false, StrategyType.SERIOUS);
            Strategy i2 = new Strategy("Inferno T Apps Crunch", "3 second mid to apps(make no sound) best position spawns go for fast banana control and retreats to mid -> short -> site and times push with apps players.", false, StrategyType.SERIOUS);
            Strategy i3 = new Strategy("Inferno CT Bedroom Control", "1 b player, smokes early. 3 players go for bedroom control, with 1 player long flashing 2nd mid.", false, StrategyType.AVERAGE);
            Strategy i4 = new Strategy("Inferno CT B Stack", "3 b players. 1 hiding coffin 2 boost new box. 1 player pit and 1 player balcony.", false, StrategyType.SERIOUS);

            Strategy m1 = new Strategy("Mirage T Palace Pop", "4 players sneak palace, 1 player flashes them out from ramp.", false, StrategyType.AVERAGE);
            Strategy m2 = new Strategy("Mirage T Split B", "3 players sneak under -> window -> kitchen -> 'B'. 2 players hold mid for teammates and will push short as soon as under players are in window.", false, StrategyType.SERIOUS);
            Strategy m3 = new Strategy("Mirage CT Con/Short Control", "3 players will stay around con/jungle and 2 will push out short. Con players will flash mid for short players.", false, StrategyType.SERIOUS);
            Strategy m4 = new Strategy("Mirage CT B Boost", "1 player gets boosted on 'B' site with a smoke. 1 will hide bench. 1 player will attempt a silent push on apps. 2 will play around con(passive)", false, StrategyType.AVERAGE);

            Strategy n1 = new Strategy("Nuke T Squeaky Pop", "1 player will run out squeaky(don't blow door) 4 will follow from hut.", false, StrategyType.SERIOUS);
            Strategy n2 = new Strategy("Nuke T Ramp to Hell", "2 wait yard(don't get seen) 3 rush ramp. When ramp players contact yard players sneak to main. Ramp pushes hell, all meet on A.", false, StrategyType.SERIOUS);
            Strategy n3 = new Strategy("Nuke CT Boost Secret", "1 boost on ramp, the player who boosts the other will run to secret through b. 3 will stay A site.", false, StrategyType.AVERAGE);
            Strategy n4 = new Strategy("Nuke CT Trophy Push", "2 will push trophy slowly. 1 will stay 'A'. Last 2 will peak yard together.", false, StrategyType.SERIOUS);

            Strategy t1 = new Strategy("Train T A Main Pop", "First 3 players will run out 'A' main and right side of red train, first will throw a right-click flash over red train. Last 2 players will push left wide of red train.", false, StrategyType.SERIOUS);
            Strategy t2 = new Strategy("Train T Ivy Crunch", "3 will sneak ivy. 2 will go 'B' halls to pub and time the push to A with ivy players.", false, StrategyType.AVERAGE);
            Strategy t3 = new Strategy("Train CT B Hold", "1 player will hide headshot on 'B'. 2 players will be around con/site 'A'. 2 players will push ivy to T-spawn slowly.", false, StrategyType.SERIOUS);
            Strategy t4 = new Strategy("Train CT Long Push", "2 players will push b long slowly. 3 will hold ivy and play for retake on a.", false, StrategyType.SERIOUS);


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
