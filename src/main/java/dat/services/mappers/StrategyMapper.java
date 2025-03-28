package dat.services.mappers;

import dat.dtos.StrategyDTO;
import dat.entities.Strategy;
import dat.entities.enums.StrategyType;

import java.util.stream.Collectors;

public class StrategyMapper {

    public static StrategyDTO toDTO(Strategy strategy) {
        return new StrategyDTO(
                strategy.getId(),
                strategy.getTitle(),
                strategy.getDescription(),
                strategy.isTeamId(),
                strategy.getType() != null ? strategy.getType().toString() : null,
                strategy.getMaps().stream().map(m -> m.getId()).collect(Collectors.toList())
        );
    }

    public static Strategy toEntity(StrategyDTO strategyDTO) {
        Strategy strategy = new Strategy();
        strategy.setId(strategyDTO.getId());
        strategy.setTitle(strategyDTO.getTitle());
        strategy.setDescription(strategyDTO.getDescription());
        strategy.setTeamId(strategyDTO.isTeamId());
        if (strategyDTO.getType() != null) {
            strategy.setType(StrategyType.valueOf(strategyDTO.getType()));
        }
        return strategy;
    }
}
