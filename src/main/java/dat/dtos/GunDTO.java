package dat.dtos;

import lombok.Data;

@Data
public class GunDTO {
    private Long id;
    private String name;
    private Long gameId;
    private boolean teamId;

    public GunDTO() {
    }

    public GunDTO(Long id, String name, Long gameId, boolean teamId) {
        this.id = id;
        this.name = name;
        this.gameId = gameId;
        this.teamId = teamId;
    }

}
