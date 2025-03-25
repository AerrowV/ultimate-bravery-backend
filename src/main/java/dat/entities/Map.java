package dat.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "maps")
public class Map {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Game game;

    @ManyToMany
    @JoinTable(
            name = "map_strategies",
            joinColumns = @JoinColumn(name = "map_id"),
            inverseJoinColumns = @JoinColumn(name = "strategy_id")
    )
    private Set<Strategy> strategies = new HashSet<>();

    public Map() {
    }

    public Map(String name, Game game) {
        this.name = name;
        this.game = game;
    }
}
