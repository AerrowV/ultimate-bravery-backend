import dat.dao.impl.GameDAO;
import dat.entities.Game;
import jakarta.persistence.EntityManager;
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
        // Fetch first game from populated data
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
        assertThat(games, hasSize(greaterThanOrEqualTo(1))); // At least 1 game from Populate class

        // Ensure our populated game is in the database
        assertThat(games, hasItem(
                hasProperty("name", is("Counter Strike")) // Adjust to match Populate.java
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
}