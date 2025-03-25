package dat.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private Set<Map> maps = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private Set<Gun> guns = new HashSet<>();

    public Game() {
    }

    public Game(String name) {
        this.name = name;
    }
}
