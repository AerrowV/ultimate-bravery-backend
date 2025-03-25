package dat.dtos;

import lombok.Data;

import java.util.List;

@Data
public class GameDTO {
    private Long id;
    private String name;

    private List<Long> mapIds;
    private List<Long> gunIds;

    public GameDTO() {
    }

    public GameDTO(Long id, String name, List<Long> mapIds, List<Long> gunIds) {
        this.id = id;
        this.name = name;
        this.mapIds = mapIds;
        this.gunIds = gunIds;
    }
}