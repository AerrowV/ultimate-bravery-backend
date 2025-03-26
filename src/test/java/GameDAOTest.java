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
    private Game testGame;

    @BeforeEach
    void setUp() {
        gameDAO = GameDAO.getInstance(emf);
        testGame = new Game();
        testGame.setName("Test Game");
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(testGame);
            em.getTransaction().commit();
        }
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
        Game foundGame = gameDAO.read(testGame.getId());
        assertNotNull(foundGame);
        assertEquals(testGame.getId(), foundGame.getId());
        assertEquals("Test Game", foundGame.getName());
    }

    @Test
    void testReadAll() {
        Game anotherGame = new Game();
        anotherGame.setName("Another Game");
        gameDAO.create(anotherGame);
        List<Game> games = gameDAO.readAll();
        assertThat(games, hasSize(2));
        assertThat(games, hasItems(
                hasProperty("name", is("Test Game")),
                hasProperty("name", is("Another Game"))
        ));
    }

    @Test
    void testUpdate() {
        Game updatedData = new Game();
        updatedData.setName("Updated Game Name");
        Game updatedGame = gameDAO.update(testGame.getId(), updatedData);
        assertNotNull(updatedGame);
        assertEquals(testGame.getId(), updatedGame.getId());
        assertEquals("Updated Game Name", updatedGame.getName());
    }

    @Test
    void testDelete() {
        gameDAO.delete(testGame.getId());
        Game deletedGame = gameDAO.read(testGame.getId());
        assertNull(deletedGame);
    }
}