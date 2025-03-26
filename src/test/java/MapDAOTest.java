import dat.dao.impl.GameDAO;
import dat.dao.impl.MapDAO;
import dat.entities.Game;
import dat.entities.Map;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class MapDAOTest extends DAOTestBase {
    private MapDAO mapDAO;
    private GameDAO gameDAO;
    private Game testGame;
    private Map testMap;

    @BeforeEach
    void setUp() {
        mapDAO = MapDAO.getInstance(emf);
        gameDAO = GameDAO.getInstance(emf);
        testGame = new Game();
        testGame.setName("Test Game");
        gameDAO.create(testGame);
        testMap = new Map();
        testMap.setName("Test Map");
        testMap.setGame(testGame);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(testMap);
            em.getTransaction().commit();
        }
    }

    @Test
    void testCreate() {
        Map newMap = new Map();
        newMap.setName("New Map");
        newMap.setGame(testGame);
        Map createdMap = mapDAO.create(newMap);
        assertNotNull(createdMap);
        assertNotNull(createdMap.getId());
        assertEquals("New Map", createdMap.getName());
    }

    @Test
    void testRead() {
        Map foundMap = mapDAO.read(testMap.getId());
        assertNotNull(foundMap);
        assertEquals(testMap.getId(), foundMap.getId());
        assertEquals("Test Map", foundMap.getName());
    }

    @Test
    void testReadAll() {
        Map anotherMap = new Map();
        anotherMap.setName("Another Map");
        anotherMap.setGame(testGame);
        mapDAO.create(anotherMap);
        List<Map> maps = mapDAO.readAll();
        assertThat(maps, hasSize(2));
        assertThat(maps, hasItems(
                hasProperty("name", is("Test Map")),
                hasProperty("name", is("Another Map"))
        ));
    }

    @Test
    void testUpdate() {
        Map updatedData = new Map();
        updatedData.setName("Updated Map Name");
        Game anotherGame = new Game();
        anotherGame.setName("Another Game");
        gameDAO.create(anotherGame);
        updatedData.setGame(anotherGame);
        Map updatedMap = mapDAO.update(testMap.getId(), updatedData);
        assertNotNull(updatedMap);
        assertEquals(testMap.getId(), updatedMap.getId());
        assertEquals("Updated Map Name", updatedMap.getName());
    }

    @Test
    void testDelete() {
        mapDAO.delete(testMap.getId());
        Map deletedMap = mapDAO.read(testMap.getId());
        assertNull(deletedMap);
    }
}