package dat.services.mappers;

import dat.dtos.GameDTO;
import dat.entities.Game;

public class GameMapper {

    public static GameDTO toDTO(Game game) {
        return new GameDTO(
                game.getId(),
                game.getName()
        );
    }

    public static Game toEntity(GameDTO gameDTO) {
        Game game = new Game();
        game.setId(gameDTO.getId());
        game.setName(gameDTO.getName());
        return game;
    }
}