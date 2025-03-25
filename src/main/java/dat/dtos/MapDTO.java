package dat.dtos;

import lombok.Data;

import java.util.List;

@Data
public class MapDTO {
    private Long id;
    private String name;
    private Long gameId;

    private List<Long> strategyIds;

    public MapDTO() {
    }

    public MapDTO(Long id, String name, Long gameId, List<Long> strategyIds) {
        this.id = id;
        this.name = name;
        this.gameId = gameId;
        this.strategyIds = strategyIds;
    }
}
