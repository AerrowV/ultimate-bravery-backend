import dat.dao.impl.GameDAO;
import dat.dao.impl.MapDAO;
import dat.entities.Game;
import dat.entities.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class MapDAOTest extends DAOTestBase {
    private MapDAO mapDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() {
        mapDAO = MapDAO.getInstance(emf);
        gameDAO = GameDAO.getInstance(emf);
    }

    @Test
    void testCreate() {
        List<Game> games = gameDAO.readAll();
        assertFalse(games.isEmpty(), "There should be at least one game from Populate.java");

        Game existingGame = games.get(0);
        Map newMap = new Map();
        newMap.setName("New Map");
        newMap.setGame(existingGame);

        Map createdMap = mapDAO.create(newMap);
        assertNotNull(createdMap);
        assertNotNull(createdMap.getId());
        assertEquals("New Map", createdMap.getName());
    }

    @Test
    void testRead() {
        List<Map> maps = mapDAO.readAll();
        assertFalse(maps.isEmpty(), "There should be at least one map from Populate.java");

        Map foundMap = mapDAO.read(maps.get(0).getId());
        assertNotNull(foundMap);
        assertEquals(maps.get(0).getId(), foundMap.getId());
        assertEquals(maps.get(0).getName(), foundMap.getName());
    }

    @Test
    void testReadAll() {
        List<Map> maps = mapDAO.readAll();
        assertThat(maps, hasSize(greaterThanOrEqualTo(1)));

        assertThat(maps, hasItem(
                hasProperty("name", is("Ancient"))
        ));
    }

    @Test
    void testUpdate() {
        List<Map> maps = mapDAO.readAll();
        assertFalse(maps.isEmpty());

        Map mapToUpdate = maps.get(0);
        Map updatedData = new Map();
        updatedData.setName("Updated Map Name");

        List<Game> games = gameDAO.readAll();
        assertFalse(games.isEmpty());

        updatedData.setGame(games.get(0));

        Map updatedMap = mapDAO.update(mapToUpdate.getId(), updatedData);
        assertNotNull(updatedMap);
        assertEquals(mapToUpdate.getId(), updatedMap.getId());
        assertEquals("Updated Map Name", updatedMap.getName());
    }

    @Test
    void testDelete() {
        List<Map> maps = mapDAO.readAll();
        assertFalse(maps.isEmpty());

        Map mapToDelete = maps.get(0);
        mapDAO.delete(mapToDelete.getId());

        Map deletedMap = mapDAO.read(mapToDelete.getId());
        assertNull(deletedMap);
    }

    @AfterAll
    void tearDownAll() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
