import dat.dao.impl.GameDAO;
import dat.dao.impl.GunDAO;
import dat.entities.Game;
import dat.entities.Gun;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GunDAOTest extends DAOTestBase {
    private GunDAO gunDAO;
    private GameDAO gameDAO;
    private Game testGame;
    private Gun testGun;

    @BeforeEach
    void setUp() {
        gunDAO = GunDAO.getInstance(emf);
        gameDAO = GameDAO.getInstance(emf);
        testGame = new Game();
        testGame.setName("Test Game");
        gameDAO.create(testGame);
        testGun = new Gun();
        testGun.setName("Test Gun");
        testGun.setGame(testGame);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(testGun);
            em.getTransaction().commit();
        }
    }

    @Test
    void testCreate() {
        Gun newGun = new Gun();
        newGun.setName("New Gun");
        newGun.setGame(testGame);
        Gun createdGun = gunDAO.create(newGun);
        assertNotNull(createdGun);
        assertNotNull(createdGun.getId());
        assertEquals("New Gun", createdGun.getName());
    }

    @Test
    void testRead() {
        Gun foundGun = gunDAO.read(testGun.getId());
        assertNotNull(foundGun);
        assertEquals(testGun.getId(), foundGun.getId());
        assertEquals("Test Gun", foundGun.getName());
    }

    @Test
    void testReadAll() {
        Gun anotherGun = new Gun();
        anotherGun.setName("Another Gun");
        anotherGun.setGame(testGame);
        gunDAO.create(anotherGun);
        List<Gun> guns = gunDAO.readAll();
        assertThat(guns, hasSize(2));
        assertThat(guns, hasItems(
                hasProperty("name", is("Test Gun")),
                hasProperty("name", is("Another Gun"))
        ));
    }

    @Test
    void testUpdate() {
        Gun updatedData = new Gun();
        updatedData.setName("Updated Gun Name");
        Game anotherGame = new Game();
        anotherGame.setName("Another Game");
        gameDAO.create(anotherGame);
        updatedData.setGame(anotherGame);
        Gun updatedGun = gunDAO.update(testGun.getId(), updatedData);
        assertNotNull(updatedGun);
        assertEquals(testGun.getId(), updatedGun.getId());
        assertEquals("Updated Gun Name", updatedGun.getName());
    }

    @Test
    void testDelete() {
        gunDAO.delete(testGun.getId());
        Gun deletedGun = gunDAO.read(testGun.getId());
        assertNull(deletedGun);
    }

    @Test
    void testGetRandomByGameId() {
        for (int i = 0; i < 5; i++) {
            Gun gun = new Gun();
            gun.setName("Gun " + i);
            gun.setGame(testGame);
            gunDAO.create(gun);
        }
        for (int i = 0; i < 10; i++) {
            Gun randomGun = gunDAO.getRandomByGameId(testGame.getId());
            assertNotNull(randomGun);
            assertEquals(testGame.getId(), randomGun.getGame().getId());
        }
        Gun shouldBeNull = gunDAO.getRandomByGameId(999L);
        assertNull(shouldBeNull);
    }
}