package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.dao.impl.MapDAO;
import dat.dao.impl.StrategyDAO;
import dat.dtos.StrategyDTO;
import dat.entities.Map;
import dat.entities.Strategy;
import dat.entities.StrategyType;
import io.javalin.http.Context;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StrategyController implements IController<StrategyDTO, Long> {

    private final StrategyDAO strategyDao;
    private final MapDAO mapDao;
    private final EntityManagerFactory emf;

    public StrategyController() {
        this.emf = HibernateConfig.getEntityManagerFactory();
        this.strategyDao = StrategyDAO.getInstance(emf);
        this.mapDao = MapDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        Strategy strategy = strategyDao.read(id);
        if (strategy != null) {
            ctx.status(200).json(convertToDTO(strategy));
        } else {
            ctx.status(404);
        }
    }

    @Override
    public void readAll(Context ctx) {
        ctx.status(200).json(strategyDao.readAll().stream()
                .map(this::convertToDTO)
                .toList());
    }

    @Override
    public void create(Context ctx) {
        StrategyDTO strategyDTO = validateEntity(ctx);
        Strategy strategy = convertToEntity(strategyDTO);
        Strategy createdStrategy = strategyDao.create(strategy);
        ctx.status(201).json(convertToDTO(createdStrategy));
    }

    @Override
    public void update(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        StrategyDTO strategyDTO = validateEntity(ctx);
        Strategy existing = strategyDao.read(id);

        if (existing == null) {
            ctx.status(404);
            return;
        }

        existing.setTitle(strategyDTO.getTitle());
        existing.setDescription(strategyDTO.getDescription());
        existing.setTeamId(strategyDTO.isTeamId());
        existing.setType(StrategyType.valueOf(strategyDTO.getType()));

        updateRelationships(existing, strategyDTO);

        Strategy updatedStrategy = strategyDao.update(id, existing);
        ctx.status(200).json(convertToDTO(updatedStrategy));
    }

    @Override
    public void delete(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        strategyDao.delete(id);
        ctx.status(204);
    }

    @Override
    public boolean validatePrimaryKey(Long id) {
        return id != null && id > 0;
    }

    @Override
    public StrategyDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(StrategyDTO.class)
                .check(s -> s.getTitle() != null && !s.getTitle().isEmpty(), "Title is required")
                .check(s -> s.getDescription() != null, "Description is required")
                .check(s -> s.getType() != null && isValidStrategyType(s.getType()), "Invalid strategy type")
                .check(s -> s.getMapIds() != null, "Map IDs must be provided")
                .get();
    }

    public void getRandomByMapAndType(Context ctx) {
        Long mapId = ctx.pathParamAsClass("mapId", Long.class)
                .check(id -> id != null && id > 0, "Invalid map ID")
                .get();

        String typeStr = ctx.queryParamAsClass("type", String.class)
                .check(t -> t != null && isValidStrategyType(t), "Invalid strategy type")
                .get();

        StrategyType type = StrategyType.valueOf(typeStr);
        Strategy randomStrategy = strategyDao.getRandomByMapAndType(mapId, type);

        if (randomStrategy != null) {
            ctx.status(200).json(convertToDTO(randomStrategy));
        } else {
            ctx.status(404);
        }
    }

    public void getByMapId(Context ctx) {
        Long mapId = ctx.pathParamAsClass("mapId", Long.class)
                .check(id -> id != null && id > 0, "Invalid map ID")
                .get();

        List<StrategyDTO> strategies = strategyDao.getByMapId(mapId).stream()
                .map(this::convertToDTO)
                .toList();
        ctx.status(200).json(strategies);
    }

    public boolean isValidStrategyType(String type) {
        try {
            StrategyType.valueOf(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private StrategyDTO convertToDTO(Strategy strategy) {
        return new StrategyDTO(
                strategy.getId(),
                strategy.getTitle(),
                strategy.getDescription(),
                strategy.isTeamId(),
                strategy.getType().name(),
                strategy.getMaps() != null ?
                        strategy.getMaps().stream().map(Map::getId).collect(Collectors.toList()) :
                        List.of()
        );
    }

    private Strategy convertToEntity(StrategyDTO dto) {
        Strategy strategy = new Strategy();
        strategy.setTitle(dto.getTitle());
        strategy.setDescription(dto.getDescription());
        strategy.setTeamId(dto.isTeamId());
        strategy.setType(StrategyType.valueOf(dto.getType()));

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            if (!dto.getMapIds().isEmpty()) {
                Set<Map> maps = em.createQuery(
                                "SELECT m FROM Map m WHERE m.id IN :ids", Map.class)
                        .setParameter("ids", dto.getMapIds())
                        .getResultStream()
                        .collect(Collectors.toSet());
                strategy.setMaps(maps);
            }

            em.getTransaction().commit();
        }

        return strategy;
    }

    private void updateRelationships(Strategy strategy, StrategyDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Strategy managedStrategy = em.merge(strategy);

            if (dto.getMapIds() != null) {
                Set<Map> maps = em.createQuery(
                                "SELECT m FROM Map m WHERE m.id IN :ids", Map.class)
                        .setParameter("ids", dto.getMapIds())
                        .getResultStream()
                        .collect(Collectors.toSet());
                managedStrategy.getMaps().clear();
                managedStrategy.getMaps().addAll(maps);
            }

            em.getTransaction().commit();
        }
    }
}