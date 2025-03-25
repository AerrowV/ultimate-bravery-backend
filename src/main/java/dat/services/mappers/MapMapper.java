package dat.services.mappers;

import dat.dtos.MapDTO;
import dat.entities.Map;

import java.util.stream.Collectors;

public class MapMapper {

    public static MapDTO toDTO(Map map) {
        return new MapDTO(
                map.getId(),
                map.getName(),
                map.getGame() != null ? map.getGame().getId() : null, // Handle potential null reference
                map.getStrategies().stream().map(s -> s.getId()).collect(Collectors.toList()) // Extract strategy IDs
        );
    }

    public static Map toEntity(MapDTO mapDTO) {
        Map map = new Map();
        map.setId(mapDTO.getId());
        map.setName(mapDTO.getName());
        // The `game` and `strategies` fields should be set later in the service layer
        return map;
    }
}
