import dat.dao.impl.GameDAO;
import dat.dao.impl.GunDAO;
import dat.entities.Game;
import dat.entities.Gun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GunDAOTest extends DAOTestBase {
    private GunDAO gunDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() {
        gunDAO = GunDAO.getInstance(emf);
        gameDAO = GameDAO.getInstance(emf);
    }

    @Test
    void testCreate() {
        List<Game> games = gameDAO.readAll();
        assertFalse(games.isEmpty(), "There should be at least one game from Populate.java");

        Game existingGame = games.get(0);
        Gun newGun = new Gun();
        newGun.setName("New Gun");
        newGun.setGame(existingGame);

        Gun createdGun = gunDAO.create(newGun);
        assertNotNull(createdGun);
        assertNotNull(createdGun.getId());
        assertEquals("New Gun", createdGun.getName());
    }

    @Test
    void testRead() {
        List<Gun> guns = gunDAO.readAll();
        assertFalse(guns.isEmpty(), "There should be at least one gun from Populate.java");

        Gun foundGun = gunDAO.read(guns.get(0).getId());
        assertNotNull(foundGun);
        assertEquals(guns.get(0).getId(), foundGun.getId());
        assertEquals(guns.get(0).getName(), foundGun.getName());
    }

    @Test
    void testReadAll() {
        List<Gun> guns = gunDAO.readAll();
        assertThat(guns, hasSize(greaterThanOrEqualTo(1))); // At least one gun from Populate.java

        assertThat(guns, hasItem(
                hasProperty("name", is("AK-47")) // Match the gun from Populate.java
        ));
    }

    @Test
    void testUpdate() {
        List<Gun> guns = gunDAO.readAll();
        assertFalse(guns.isEmpty());

        Gun gunToUpdate = guns.get(0);
        Gun updatedData = new Gun();
        updatedData.setName("Updated Gun Name");

        Gun updatedGun = gunDAO.update(gunToUpdate.getId(), updatedData);
        assertNotNull(updatedGun);
        assertEquals(gunToUpdate.getId(), updatedGun.getId());
        assertEquals("Updated Gun Name", updatedGun.getName());
    }

    @Test
    void testDelete() {
        List<Gun> guns = gunDAO.readAll();
        assertFalse(guns.isEmpty());

        Gun gunToDelete = guns.get(0);
        gunDAO.delete(gunToDelete.getId());

        Gun deletedGun = gunDAO.read(gunToDelete.getId());
        assertNull(deletedGun);
    }

    @Test
    void testGetRandomByGameId() {
        List<Game> games = gameDAO.readAll();
        assertFalse(games.isEmpty());

        Game existingGame = games.get(0);
        for (int i = 0; i < 5; i++) {
            Gun gun = new Gun();
            gun.setName("Gun " + i);
            gun.setGame(existingGame);
            gunDAO.create(gun);
        }

        for (int i = 0; i < 10; i++) {
            Gun randomGun = gunDAO.getRandomByGameId(existingGame.getId());
            assertNotNull(randomGun);
            assertEquals(existingGame.getId(), randomGun.getGame().getId());
        }

        Gun shouldBeNull = gunDAO.getRandomByGameId(999L);
        assertNull(shouldBeNull);
    }
}
