package de.learnlib.alex.rest;

import de.learnlib.alex.core.dao.UserDAO;
import de.learnlib.alex.core.entities.User;
import de.learnlib.alex.core.entities.UserRole;
import de.learnlib.alex.security.RsaKeyHolder;
import de.learnlib.alex.security.UserPrincipal;
import de.learnlib.alex.utils.ResourceErrorHandler;
import de.learnlib.alex.utils.ResponseHelper;
import org.apache.shiro.authz.UnauthorizedException;
import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

/**
 * REST resource to handle users
 */
@Path("/users")
public class UserResource {

    /**
     * The UserDAO to user
     */
    @Inject
    private UserDAO userDAO;

    /** The security context containing the user of the request */
    @Context
    SecurityContext securityContext;

    /**
     * Creates a new user
     *
     * @param user The user to create
     * @return An HTTP response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(User user) {
        try {
            // validate email address
            if (!new EmailValidator().isValid(user.getEmail(), null)) {
                throw new ValidationException("The email is not valid");
            }

            user.setEncryptedPassword(user.getPassword());

            // create user
            userDAO.create(user);
            return Response.status(Status.CREATED).entity(user).build();
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("UserResource.create", Status.BAD_REQUEST, e);
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"REGISTERED"})
    public Response get(@PathParam("id") Long userId) {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();

        if (user.getRole().equals(UserRole.ADMIN) || user.getId().equals(userId)) {
            return Response.ok(userDAO.getById(userId)).build();
        } else {
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    /**
     * Get all users. Should only be allowed to call by an admin
     *
     * @return An HTTP response containing all registered users
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response getAll() {
        List<User> users = userDAO.getAll();
        return ResponseHelper.renderList(users, Status.OK);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"REGISTERED"})
    public Response delete(@PathParam("id") long userId) {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();

        try {
            if (user.getRole().equals(UserRole.ADMIN) || user.getId().equals(userId)) {
                userDAO.delete(userId);
                return Response.status(Status.OK).build();
            } else {
                throw new UnauthorizedException("You are not allowed to delete this user");
            }
        } catch (de.learnlib.alex.exceptions.NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("UserResource.delete", Status.NOT_FOUND, e);
        } catch (UnauthorizedException e) {
            return ResourceErrorHandler.createRESTErrorMessage("UserResource.delete", Status.UNAUTHORIZED, e);
        }
    }

    /**
     * Logs in a user by generating a unique JWT for him that needs to be send in every request
     *
     * @param user The user to login
     * @return An HTTP response containing a signed JWT of the user on success
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response login(User user) {
        User realUser = userDAO.getByEmail(user.getEmail());
        if (realUser != null) {
            try {
                if (realUser.isValidPassword(user.getPassword())) {
                    return Response.ok(generateJWT(realUser)).build();
                } else {
                    return Response.status(Status.BAD_REQUEST).build();
                }
            } catch (JoseException e) {
                e.printStackTrace();
                return Response.status(Status.NOT_FOUND).build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    /**
     * Generates a JWT as String representation with JSON {"token": [the-encoded-token]}
     * Encodes the id and the role of the user as "userId" and "userRole" in the claims of the jwt
     *
     * @param user The user to generate the JWT from
     * @return The string representation of the jwt
     * @throws JoseException
     */
    private String generateJWT(User user) throws JoseException {

        // generate claims with user data
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("ALEX");
        claims.setGeneratedJwtId();
        claims.setClaim("userId", user.getId());
        claims.setClaim("userRole", user.getRole());

        // create signature
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(RsaKeyHolder.getKey().getPrivateKey());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        // return signed jwt
        return "{\"token\": \"" + jws.getCompactSerialization() + "\"}";
    }
}