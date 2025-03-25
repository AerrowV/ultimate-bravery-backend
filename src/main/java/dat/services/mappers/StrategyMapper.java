package dat.services.mappers;

import dat.dtos.StrategyDTO;
import dat.entities.Strategy;
import dat.entities.StrategyType;

import java.util.stream.Collectors;

public class StrategyMapper {

    public static StrategyDTO toDTO(Strategy strategy) {
        return new StrategyDTO(
                strategy.getId(),
                strategy.getTitle(),
                strategy.getDescription(),
                strategy.isTeamId(),
                strategy.getType() != null ? strategy.getType().toString() : null, // Convert enum to String
                strategy.getMaps().stream().map(m -> m.getId()).collect(Collectors.toList()) // Extract map IDs
        );
    }

    public static Strategy toEntity(StrategyDTO strategyDTO) {
        Strategy strategy = new Strategy();
        strategy.setId(strategyDTO.getId());
        strategy.setTitle(strategyDTO.getTitle());
        strategy.setDescription(strategyDTO.getDescription());
        strategy.setTeamId(strategyDTO.isTeamId());
        // The `type` field needs to be mapped from the String back to the StrategyType enum
        if (strategyDTO.getType() != null) {
            strategy.setType(StrategyType.valueOf(strategyDTO.getType())); // Convert String back to enum
        }
        // The `maps` field should be set later in the service layer
        return strategy;
    }
}
