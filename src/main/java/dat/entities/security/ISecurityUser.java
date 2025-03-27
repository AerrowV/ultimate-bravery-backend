package dat.entities.security;

import dat.entities.Role;

public interface ISecurityUser {
    boolean verifyPassword(String pw);

    void addRole(Role role);

    void removeRole(String role);
}

