package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.dao.impl.GameDAO;
import dat.dao.impl.MapDAO;
import dat.dao.impl.StrategyDAO;
import dat.services.mappers.MapMapper;
import dat.dtos.MapDTO;
import dat.entities.Game;
import dat.entities.Map;
import dat.entities.Strategy;
import io.javalin.http.Context;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MapController implements IController<MapDTO, Long> {

    private final MapDAO mapDao;
    private final GameDAO gameDao;
    private final StrategyDAO strategyDao;
    private final EntityManagerFactory emf;

    public MapController() {
        this.emf = HibernateConfig.getEntityManagerFactory();
        this.mapDao = MapDAO.getInstance(emf);
        this.gameDao = GameDAO.getInstance(emf);
        this.strategyDao = StrategyDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        Map map = mapDao.read(id);
        if (map != null) {
            ctx.status(200).json(MapMapper.toDTO(map));
        } else {
            ctx.status(404);
        }
    }

    @Override
    public void readAll(Context ctx) {
        ctx.status(200).json(mapDao.readAll().stream()
                .map(MapMapper::toDTO)
                .toList());
    }

    @Override
    public void create(Context ctx) {
        MapDTO mapDTO = validateEntity(ctx);
        Map map = MapMapper.toEntity(mapDTO);
        Map createdMap = mapDao.create(map);
        ctx.status(201).json(MapMapper.toDTO(createdMap));
    }

    @Override
    public void update(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        MapDTO mapDTO = validateEntity(ctx);
        Map existing = mapDao.read(id);

        if (existing == null) {
            ctx.status(404);
            return;
        }

        existing.setName(mapDTO.getName());
        updateRelationships(existing, mapDTO);

        Map updatedMap = mapDao.update(id, existing);
        ctx.status(200).json(MapMapper.toDTO(updatedMap));
    }

    @Override
    public void delete(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        mapDao.delete(id);
        ctx.status(204);
    }

    @Override
    public boolean validatePrimaryKey(Long id) {
        return id != null && id > 0;
    }

    @Override
    public MapDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(MapDTO.class)
                .check(m -> m.getName() != null && !m.getName().isEmpty(), "Map name is required")
                .check(m -> m.getGameId() != null, "Game ID must be provided")
                .check(m -> m.getStrategyIds() != null, "Strategy IDs must be provided")
                .get();
    }

    public void readWithStrategies(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        Map map = mapDao.readWithStrategies(id);
        if (map != null) {
            ctx.status(200).json(MapMapper.toDTO(map));
        } else {
            ctx.status(404);
        }
    }


    private void updateRelationships(Map map, MapDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Map managedMap = em.merge(map);

            Game game = em.find(Game.class, dto.getGameId());
            managedMap.setGame(game);

            if (dto.getStrategyIds() != null) {
                Set<Strategy> strategies = em.createQuery(
                                "SELECT s FROM Strategy s WHERE s.id IN :ids", Strategy.class)
                        .setParameter("ids", dto.getStrategyIds())
                        .getResultStream()
                        .collect(Collectors.toSet());
                managedMap.getStrategies().clear();
                managedMap.getStrategies().addAll(strategies);
            }

            em.getTransaction().commit();
        }
    }
}