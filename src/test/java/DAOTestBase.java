import dat.config.HibernateConfig;
import dat.config.Populate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DAOTestBase {
    protected EntityManagerFactory emf;

    @BeforeAll
    void setUpAll() {
        if (emf == null || !emf.isOpen()) {
            HibernateConfig.setTest(true);
            emf = HibernateConfig.getEntityManagerFactoryForTest();
        }
    }

    @BeforeEach
    void cleanDatabase() {
        if (emf != null && emf.isOpen()) {
            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();
                em.createQuery("DELETE FROM Strategy").executeUpdate();
                em.createQuery("DELETE FROM Map").executeUpdate();
                em.createQuery("DELETE FROM Gun").executeUpdate();
                em.createQuery("DELETE FROM Game").executeUpdate();
                em.createQuery("DELETE FROM User").executeUpdate();
                em.createQuery("DELETE FROM Role").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Populate.populate(emf);
    }
}
