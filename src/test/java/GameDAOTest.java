import dat.dao.impl.GameDAO;
import dat.entities.Game;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest extends DAOTestBase {
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() {
        gameDAO = GameDAO.getInstance(emf);
    }

    @Test
    void testCreate() {
        Game newGame = new Game();
        newGame.setName("New Game");
        Game createdGame = gameDAO.create(newGame);

        assertNotNull(createdGame);
        assertNotNull(createdGame.getId());
        assertEquals("New Game", createdGame.getName());
    }

    @Test
    void testRead() {
        List<Game> games = gameDAO.readAll();
        assertFalse(games.isEmpty());

        Game foundGame = gameDAO.read(games.get(0).getId());
        assertNotNull(foundGame);
        assertEquals(games.get(0).getId(), foundGame.getId());
        assertEquals(games.get(0).getName(), foundGame.getName());
    }

    @Test
    void testReadAll() {
        List<Game> games = gameDAO.readAll();
        assertThat(games, hasSize(greaterThanOrEqualTo(1)));

        assertThat(games, hasItem(
                hasProperty("name", is("Counter Strike"))
        ));
    }

    @Test
    void testUpdate() {
        List<Game> games = gameDAO.readAll();
        assertFalse(games.isEmpty());

        Game gameToUpdate = games.get(0);
        Game updatedData = new Game();
        updatedData.setName("Updated Game Name");

        Game updatedGame = gameDAO.update(gameToUpdate.getId(), updatedData);
        assertNotNull(updatedGame);
        assertEquals(gameToUpdate.getId(), updatedGame.getId());
        assertEquals("Updated Game Name", updatedGame.getName());
    }

    @Test
    void testDelete() {
        List<Game> games = gameDAO.readAll();
        assertFalse(games.isEmpty());

        Game gameToDelete = games.get(0);
        gameDAO.delete(gameToDelete.getId());

        Game deletedGame = gameDAO.read(gameToDelete.getId());
        assertNull(deletedGame);
    }

    @AfterAll
    void tearDownAll() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}