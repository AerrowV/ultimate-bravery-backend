import dat.dao.impl.MapDAO;
import dat.dao.impl.StrategyDAO;
import dat.entities.Map;
import dat.entities.Strategy;
import dat.entities.enums.StrategyType;
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

    @BeforeEach
    void setUp() {
        strategyDAO = StrategyDAO.getInstance(emf);
        mapDAO = MapDAO.getInstance(emf);

        List<Map> maps = mapDAO.readAll();
        assertFalse(maps.isEmpty(), "There should be at least one map from Populate.java");

        testMap = maps.get(0);
    }

    @Test
    void testCreateWithDifferentStrategyTypes() {
        Strategy trollStrategy = createTestStrategy("Troll Pick", StrategyType.TROLL);
        Strategy averageStrategy = createTestStrategy("Balanced Approach", StrategyType.AVERAGE);

        assertEquals(StrategyType.TROLL, trollStrategy.getType());
        assertEquals(StrategyType.AVERAGE, averageStrategy.getType());
    }

    @Test
    void testUpdateStrategyType() {
        List<Strategy> strategies = strategyDAO.getByMapId(testMap.getId());
        assertFalse(strategies.isEmpty(), "There should be at least one strategy in the populated database");

        Strategy strategyToUpdate = strategies.get(0);
        Strategy updatedData = new Strategy();
        updatedData.setType(StrategyType.AVERAGE);

        Strategy updated = strategyDAO.update(strategyToUpdate.getId(), updatedData);
        assertEquals(StrategyType.AVERAGE, updated.getType());
    }

    @Test
    void testGetRandomByMapAndType() {
        Strategy random = strategyDAO.getRandomByMapAndType(testMap.getId(), StrategyType.AVERAGE);

        assertNotNull(random, "Expected a random strategy of type AVERAGE for the map.");
        assertEquals(StrategyType.AVERAGE, random.getType(), "Expected the strategy to be of type AVERAGE.");
    }

//    @Test
//    void testGetByMapIdWithDifferentTypes() {
//        createTestStrategy("Troll Play", StrategyType.TROLL);
//        createTestStrategy("Average Game", StrategyType.AVERAGE);
//
//        List<Strategy> strategies = strategyDAO.getByMapId(testMap.getId());
//
//        assertThat(strategies, hasSize(greaterThanOrEqualTo(2)));
//
//        assertThat(strategies, hasItems(
//                hasProperty("type", is(StrategyType.TROLL)),
//                hasProperty("type", is(StrategyType.AVERAGE))
//        ));
//    }


    private Strategy createTestStrategy(String title, StrategyType type) {
        Strategy s = new Strategy();
        s.setTitle(title);
        s.setDescription("Test " + type);
        s.setType(type);
        s.getMaps().add(testMap);

        return strategyDAO.create(s);
    }
}
