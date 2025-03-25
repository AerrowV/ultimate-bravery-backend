package dat.controllers;

import dat.dao.impl.UserDAO;
import dat.entities.Role;
import dat.entities.User;

public class AccountController {


    public static void fillUpDatabaseWithUserAndAdmin() {
        UserDAO dao = UserDAO.getInstance();

        Role adminRole = new Role("Admin");
        Role userRole = new Role("User");
        dao.createRole(adminRole);
        dao.createRole(userRole);

        User user = new User("Karl", "password");
        user.addRole(adminRole);

        dao.createUser(user);
    }


    public void verifyPassword(User user) {
        boolean result = user.verifyPassword("password");
        System.out.println(user.getPassword());
        System.out.println(result);
    }
}