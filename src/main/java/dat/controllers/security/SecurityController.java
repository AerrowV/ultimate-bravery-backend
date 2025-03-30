package dat.controllers.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dat.config.HibernateConfig;
import dat.dao.security.ISecurityDAO;
import dat.dao.security.SecurityDAO;
import dat.entities.User;
import dat.exceptions.ApiRuntimeException;
import dat.exceptions.NotAuthorizedException;
import dat.exceptions.ValidationException;
import dat.utils.Utils;
import dk.bugelhartmann.ITokenSecurity;
import dk.bugelhartmann.TokenSecurity;
import dk.bugelhartmann.TokenVerificationException;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityController implements ISecurityController {
    private static ISecurityDAO securityDAO;
    private static SecurityController instance;
    private static Logger logger = LoggerFactory.getLogger(SecurityController.class);
    ObjectMapper objectMapper = new ObjectMapper();
    ITokenSecurity tokenSecurity = new TokenSecurity();

    private SecurityController() {
    }

    public static SecurityController getInstance() {
        if (instance == null) {
            instance = new SecurityController();
        }
        securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
        return instance;
    }

    @Override
    public Handler login() {
        return (ctx) -> {
            ObjectNode returnObject = objectMapper.createObjectNode();
            try {
                UserDTO user = ctx.bodyAsClass(UserDTO.class);
                UserDTO verifiedUser = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());
                String token = createToken(verifiedUser);

                ctx.status(200).json(returnObject
                        .put("token", token)
                        .put("username", verifiedUser.getUsername()));

            } catch (EntityNotFoundException | ValidationException e) {
                ctx.status(401);
                System.out.println(e.getMessage());
                ctx.json(returnObject.put("msg", e.getMessage()));
            }
        };
    }

    @Override
    public Handler register() {
        return (ctx) -> {
            ObjectNode returnObject = objectMapper.createObjectNode();
            try {
                UserDTO userInput = ctx.bodyAsClass(UserDTO.class);
                User created = securityDAO.createUser(userInput.getUsername(), userInput.getPassword());

                String token = createToken(new UserDTO(created.getUsername(), Set.of("USER")));
                ctx.status(HttpStatus.CREATED).json(returnObject
                        .put("token", token)
                        .put("username", created.getUsername()));
            } catch (EntityExistsException e) {
                ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
                ctx.json(returnObject.put("msg", "User already exists"));
            }
        };
    }

    @Override
    public Handler authenticate() throws UnauthorizedResponse {

        ObjectNode returnObject = objectMapper.createObjectNode();
        return (ctx) -> {
            if (ctx.method().toString().equals("OPTIONS")) {
                ctx.status(200);
                return;
            }
            String header = ctx.header("Authorization");
            if (header == null) {
                throw new UnauthorizedResponse("Authorization header missing");
            }

            String[] headerParts = header.split(" ");
            if (headerParts.length != 2) {
                throw new UnauthorizedResponse("Authorization header malformed");
            }

            String token = headerParts[1];
            UserDTO verifiedTokenUser = verifyToken(token);

            if (verifiedTokenUser == null) {
                throw new UnauthorizedResponse("Invalid User or Token");
            }
            logger.info("User verified: " + verifiedTokenUser);
            ctx.attribute("user", verifiedTokenUser);
        };
    }

    @Override
    public boolean authorize(UserDTO user, Set<RouteRole> allowedRoles) {
        if (user == null) {
            throw new UnauthorizedResponse("You need to log in, dude!");
        }
        Set<String> roleNames = allowedRoles.stream()
                .map(RouteRole::toString)
                .collect(Collectors.toSet());
        return user.getRoles().stream()
                .map(String::toUpperCase)
                .anyMatch(roleNames::contains);
    }

    @Override
    public String createToken(UserDTO user) {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }
            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiRuntimeException(500, "Could not create token");
        }
    }

    @Override
    public UserDTO verifyToken(String token) {
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET = IS_DEPLOYED ? System.getenv("SECRET_KEY") : Utils.getPropertyValue("SECRET_KEY", "config.properties");

        try {
            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                throw new NotAuthorizedException(403, "Token is not valid");
            }
        } catch (ParseException | NotAuthorizedException | TokenVerificationException e) {
            e.printStackTrace();
            throw new ApiRuntimeException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }

    public @NotNull Handler addRole() {
        return (ctx) -> {
            ObjectNode returnObject = objectMapper.createObjectNode();
            try {
                String newRole = ctx.bodyAsClass(ObjectNode.class).get("role").asText();
                UserDTO user = ctx.attribute("user");
                User updatedUser = securityDAO.addRole(user, newRole);
                ctx.status(200).json(returnObject.put("msg", "Role " + newRole + " added to user"));
            } catch (EntityNotFoundException e) {
                ctx.status(404).json("{\"msg\": \"User not found\"}");
            }
        };
    }

    public void healthCheck(@NotNull Context ctx) {
        ctx.status(200).json("{\"msg\": \"API is up and running\"}");
    }
}