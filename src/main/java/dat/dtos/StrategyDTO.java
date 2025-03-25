package dat.dtos;

import lombok.Data;

import java.util.List;

@Data
public class StrategyDTO {
    private Long id;
    private String title;
    private String description;
    private boolean teamId;
    private String type;

    private List<Long> mapIds;

    public StrategyDTO() {
    }

    public StrategyDTO(Long id, String title, String description, boolean teamId, String type, List<Long> mapIds) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.teamId = teamId;
        this.type = type;
        this.mapIds = mapIds;
    }
}
