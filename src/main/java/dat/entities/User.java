package dat.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@NamedQueries(@NamedQuery(name = "User.deleteAllRows", query = "DELETE from User"))
public class User implements ISecurityUser {
    @Id
    private String username;
    private String password;
    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_name"),
            inverseJoinColumns = @JoinColumn(name = "role_name"))
    private Set<dat.entities.Role> roles = new HashSet<>();

    public User(String username, String password) {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public User() {
    }

    @Override
    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.password);
    }

    @Override
    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    @Override
    public void removeRole(String role) {
        roles.removeIf(r -> r.getName().equals(role));
        roles.stream()
                .filter(r -> r.getName().equals(role))
                .findFirst()
                .ifPresent(r -> r.getUsers().remove(this));
    }
}
