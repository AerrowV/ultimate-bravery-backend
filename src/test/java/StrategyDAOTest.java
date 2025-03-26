import dat.dao.impl.MapDAO;
import dat.dao.impl.StrategyDAO;
import dat.entities.Map;
import dat.entities.Strategy;
import dat.entities.StrategyType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class StrategyDAOTest extends DAOTestBase {
    private StrategyDAO strategyDAO;
    private MapDAO mapDAO;
    private Map testMap;
    private Strategy testStrategy;

    @BeforeEach
    void setUp() {
        strategyDAO = StrategyDAO.getInstance(emf);
        mapDAO = MapDAO.getInstance(emf);
        testMap = new Map();
        testMap.setName("Test Map");
        mapDAO.create(testMap);
        testStrategy = new Strategy();
        testStrategy.setTitle("Serious Strategy");
        testStrategy.setDescription("Competitive play");
        testStrategy.setType(StrategyType.SERIOUS);
        testStrategy.getMaps().add(testMap);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(testStrategy);
            em.getTransaction().commit();
        }
    }

    @Test
    void testCreateWithDifferentStrategyTypes() {
        Strategy trollStrategy = new Strategy();
        trollStrategy.setTitle("Troll Pick");
        trollStrategy.setDescription("Just for fun");
        trollStrategy.setType(StrategyType.TROLL);
        trollStrategy.getMaps().add(testMap);
        Strategy createdTroll = strategyDAO.create(trollStrategy);

        Strategy averageStrategy = new Strategy();
        averageStrategy.setTitle("Balanced Approach");
        averageStrategy.setDescription("Middle ground");
        averageStrategy.setType(StrategyType.AVERAGE);
        averageStrategy.getMaps().add(testMap);
        Strategy createdAverage = strategyDAO.create(averageStrategy);

        assertEquals(StrategyType.TROLL, createdTroll.getType());
        assertEquals(StrategyType.AVERAGE, createdAverage.getType());
    }

    @Test
    void testUpdateStrategyType() {
        Strategy updatedData = new Strategy();
        updatedData.setType(StrategyType.AVERAGE);
        Strategy updated = strategyDAO.update(testStrategy.getId(), updatedData);
        assertEquals(StrategyType.AVERAGE, updated.getType());
    }

    @Test
    void testGetRandomByMapAndType() {
        Strategy serious1 = createTestStrategy("Serious 1", StrategyType.SERIOUS);
        Strategy serious2 = createTestStrategy("Serious 2", StrategyType.SERIOUS);
        Strategy troll = createTestStrategy("Troll", StrategyType.TROLL);

        for (int i = 0; i < 10; i++) {
            Strategy random = strategyDAO.getRandomByMapAndType(testMap.getId(), StrategyType.SERIOUS);
            assertNotNull(random);
            assertEquals(StrategyType.SERIOUS, random.getType());
            assertTrue(List.of("Serious Strategy", "Serious 1", "Serious 2").contains(random.getTitle()));
        }

        Strategy shouldBeNull = strategyDAO.getRandomByMapAndType(testMap.getId(), StrategyType.AVERAGE);
        assertNull(shouldBeNull);
    }

    @Test
    void testGetByMapIdWithDifferentTypes() {
        createTestStrategy("Troll Play", StrategyType.TROLL);
        createTestStrategy("Average Game", StrategyType.AVERAGE);

        List<Strategy> strategies = strategyDAO.getByMapId(testMap.getId());
        assertThat(strategies, hasSize(3));
        assertThat(strategies, hasItems(
                hasProperty("type", is(StrategyType.SERIOUS)),
                hasProperty("type", is(StrategyType.TROLL)),
                hasProperty("type", is(StrategyType.AVERAGE))
        ));
    }

    private Strategy createTestStrategy(String title, StrategyType type) {
        Strategy s = new Strategy();
        s.setTitle(title);
        s.setDescription("Test " + type);
        s.setType(type);
        s.getMaps().add(testMap);
        return strategyDAO.create(s);
    }
}