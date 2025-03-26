package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.config.Populate;
import dat.controllers.IController;
import dat.dao.impl.GameDAO;
import dat.dtos.GameDTO;
import dat.entities.Game;
import dat.entities.Gun;
import dat.entities.Map;
import io.javalin.http.Context;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameController implements IController<GameDTO, Long> {

    private final GameDAO dao;
    private final EntityManagerFactory emf;

    public GameController() {
        this.emf = HibernateConfig.getEntityManagerFactory();
        this.dao = GameDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        Game game = dao.read(id);
        if (game != null) {
            ctx.status(200).json(convertToDTO(game));
        } else {
            ctx.status(404);
        }
    }

    @Override
    public void readAll(Context ctx) {
        List<Game> games = dao.readAll();
        List<GameDTO> gameDTOs = games.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        ctx.status(200).json(gameDTOs);
    }

    @Override
    public void create(Context ctx) {
        GameDTO gameDTO = validateEntity(ctx);
        Game game = convertToEntity(gameDTO);
        Game createdGame = dao.create(game);
        ctx.status(201).json(convertToDTO(createdGame));
    }

    @Override
    public void update(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        GameDTO gameDTO = validateEntity(ctx);
        Game existing = dao.read(id);
        if (existing == null) {
            ctx.status(404);
            return;
        }
        existing.setName(gameDTO.getName());
        updateRelationships(existing, gameDTO);
        Game updatedGame = dao.update(id, existing);
        ctx.status(200).json(convertToDTO(updatedGame));
    }

    @Override
    public void delete(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        dao.delete(id);
        ctx.status(204);
    }

    @Override
    public boolean validatePrimaryKey(Long id) {
        return id != null && id > 0;
    }

    @Override
    public GameDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(GameDTO.class)
                .check(g -> g.getName() != null && !g.getName().isEmpty(), "Game name is required")
                .check(g -> g.getMapIds() != null, "Map IDs must be provided")
                .check(g -> g.getGunIds() != null, "Gun IDs must be provided")
                .get();
    }

    public void getByMap(Context ctx) {
        Long mapId = ctx.pathParamAsClass("mapId", Long.class)
                .check(id -> id != null && id > 0, "Invalid map ID")
                .get();
        try (EntityManager em = emf.createEntityManager()) {
            List<Game> games = em.createQuery(
                            "SELECT g FROM Game g JOIN g.maps m WHERE m.id = :mapId", Game.class)
                    .setParameter("mapId", mapId)
                    .getResultList();
            ctx.status(200).json(games.stream().map(this::convertToDTO).collect(Collectors.toList()));
        }
    }

    public void getByGun(Context ctx) {
        Long gunId = ctx.pathParamAsClass("gunId", Long.class)
                .check(id -> id != null && id > 0, "Invalid gun ID")
                .get();
        try (EntityManager em = emf.createEntityManager()) {
            List<Game> games = em.createQuery(
                            "SELECT g FROM Game g JOIN g.guns gu WHERE gu.id = :gunId", Game.class)
                    .setParameter("gunId", gunId)
                    .getResultList();
            ctx.status(200).json(games.stream().map(this::convertToDTO).collect(Collectors.toList()));
        }
    }

    private GameDTO convertToDTO(Game game) {
        List<Long> mapIds = game.getMaps().stream().map(Map::getId).collect(Collectors.toList());
        List<Long> gunIds = game.getGuns().stream().map(Gun::getId).collect(Collectors.toList());
        return new GameDTO(game.getId(), game.getName(), mapIds, gunIds);
    }

    private Game convertToEntity(GameDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Game game = new Game();
            game.setName(dto.getName());

            if (!dto.getMapIds().isEmpty()) {
                Set<Map> maps = new HashSet<>(em.createQuery(
                                "SELECT m FROM Map m WHERE m.id IN :ids", Map.class)
                        .setParameter("ids", dto.getMapIds())
                        .getResultList());
                game.setMaps(maps);
            }

            if (!dto.getGunIds().isEmpty()) {
                Set<Gun> guns = new HashSet<>(em.createQuery(
                                "SELECT g FROM Gun g WHERE g.id IN :ids", Gun.class)
                        .setParameter("ids", dto.getGunIds())
                        .getResultList());
                game.setGuns(guns);
            }

            em.persist(game);
            em.getTransaction().commit();
            return game;
        }
    }

    private void updateRelationships(Game game, GameDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Game managedGame = em.merge(game);
            if (dto.getMapIds() != null) {
                List<Map> maps = em.createQuery(
                                "SELECT m FROM Map m WHERE m.id IN :ids", Map.class)
                        .setParameter("ids", dto.getMapIds())
                        .getResultList();
                managedGame.getMaps().clear();
                managedGame.getMaps().addAll(maps);
            }
            if (dto.getGunIds() != null) {
                List<Gun> guns = em.createQuery(
                                "SELECT g FROM Gun g WHERE g.id IN :ids", Gun.class)
                        .setParameter("ids", dto.getGunIds())
                        .getResultList();
                managedGame.getGuns().clear();
                managedGame.getGuns().addAll(guns);
            }
            em.getTransaction().commit();
        }
    }

    public void populate(Context ctx) {
        Populate.populate(HibernateConfig.getEntityManagerFactory());
        ctx.result("Populated database with game, maps, guns, and strategies.");
    }
}