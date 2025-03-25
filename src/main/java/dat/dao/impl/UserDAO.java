package dat.dao.impl;

import dat.config.HibernateConfig;
import dat.entities.Role;
import dat.entities.User;
import dat.exceptions.ValidationException;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private static UserDAO instance;
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    private UserDAO() {
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    public User getUser(String username) {
        return null;
    }

    public User createUser(User user) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            if (user.getRoles().isEmpty()) {
                Role userRole = em.find(Role.class, "User");
                if (userRole == null) {
                    userRole = new Role("User");
                    em.persist(userRole);
                }
                user.addRole(userRole);
            }

            Set<Role> newRoleSet = new HashSet<>();
            for (Role role : user.getRoles()) {
                Role foundRole = em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                        .setParameter("name", role.getName())
                        .getResultList()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("No role found with name: " + role.getName()));

                newRoleSet.add(foundRole);
            }

            user.setRoles(newRoleSet);
            em.persist(user);
            em.getTransaction().commit();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return user;
    }

    public void createRole(Role role) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
        }
    }

    public UserDTO getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = emf.createEntityManager()) {
            User user = em.find(User.class, username);
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + username);
            user.getRoles().size();
            if (!user.verifyPassword(password))
                throw new ValidationException("Wrong password");
            return new UserDTO(user.getUsername(), user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()));
        }
    }

    public void deleteUser(String username) {
    }

    public void updateUser(User user) {
    }
}
