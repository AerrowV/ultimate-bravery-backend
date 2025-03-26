import dat.dao.impl.UserDAO;
import dat.entities.Role;
import dat.entities.User;
import dat.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest extends DAOTestBase {
    private UserDAO userDAO;
    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userDAO = UserDAO.getInstance();
        userRole = new Role("User");
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(userRole);
            em.getTransaction().commit();
        }
        testUser = new User("testuser", "password123");
        testUser.addRole(userRole);
        userDAO.createUser(testUser);
    }

    @Test
    void testCreateUser() {
        User newUser = new User("newuser", "newpass");
        newUser.addRole(userRole);
        User createdUser = userDAO.createUser(newUser);
        assertNotNull(createdUser);
        assertEquals("newuser", createdUser.getUsername());
        assertThat(createdUser.getRoles(), hasSize(1));
    }

    @Test
    void testCreateUserWithDefaultRole() {
        User newUser = new User("defaultuser", "defaultpass");
        User createdUser = userDAO.createUser(newUser);
        assertNotNull(createdUser);
        assertEquals("defaultuser", createdUser.getUsername());
        assertThat(createdUser.getRoles(), hasSize(1));
    }

    @Test
    void testCreateRole() {
        Role adminRole = new Role("Admin");
        userDAO.createRole(adminRole);
        try (EntityManager em = emf.createEntityManager()) {
            Role foundRole = em.find(Role.class, "Admin");
            assertNotNull(foundRole);
            assertEquals("Admin", foundRole.getName());
        }
    }

    @Test
    void testGetVerifiedUser() throws ValidationException {
        var verifiedUser = userDAO.getVerifiedUser("testuser", "password123");
        assertNotNull(verifiedUser);
        assertEquals("testuser", verifiedUser.getUsername());
        assertThrows(ValidationException.class, () ->
                userDAO.getVerifiedUser("testuser", "wrongpassword"));
        assertThrows(EntityNotFoundException.class, () ->
                userDAO.getVerifiedUser("nonexistent", "password"));
    }

    @Test
    void testPasswordHashing() {
        try (EntityManager em = emf.createEntityManager()) {
            User foundUser = em.find(User.class, "testuser");
            assertNotNull(foundUser);
            assertNotEquals("password123", foundUser.getPassword());
            assertTrue(foundUser.verifyPassword("password123"));
        }
    }
}