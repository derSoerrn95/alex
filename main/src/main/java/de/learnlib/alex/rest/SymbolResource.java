package de.learnlib.alex.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.selenium.condition.Not;
import de.learnlib.alex.core.dao.SymbolDAO;
import de.learnlib.alex.core.entities.Symbol;
import de.learnlib.alex.core.entities.SymbolVisibilityLevel;
import de.learnlib.alex.exceptions.NotFoundException;
import de.learnlib.alex.utils.IdsList;
import de.learnlib.alex.utils.ResourceErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * REST API to manage the symbols.
 * @resourcePath symbols
 * @resourceDescription Operations about symbols
 */
@Path("/projects/{project_id}/symbols")
public class SymbolResource {

    /** Use the logger for the server part. */
    private static final Logger LOGGER = LogManager.getLogger("server");

    /** Context information about the URI. */
    @Context
    private UriInfo uri;

    /** The {@link SymbolDAO} to use. */
    @Inject
    private SymbolDAO symbolDAO;

    /**
     * Create a new Symbol.
     *
     * @param projectId
     *            The ID of the project the symbol should belong to.
     * @param symbol
     *            The symbol to add.
     * @return On success the added symbol (enhanced with information from the DB); An error message on failure.
     * @responseType de.learnlib.alex.core.entities.Symbol
     * @successResponse 201 created
     * @errorResponse   400 bad request `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSymbol(@PathParam("project_id") Long projectId, Symbol symbol) {

        try {
            checkSymbolBeforeCreation(projectId, symbol); // can throw an IllegalArgumentException
            symbolDAO.create(symbol);

            String symbolURL = uri.getBaseUri() + "projects/" + symbol.getProjectId() + "/symbols/" + symbol.getId();
            return Response.status(Status.CREATED).header("Location", symbolURL).entity(symbol).build();

        } catch (IllegalArgumentException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.createSymbol", Status.BAD_REQUEST, e);
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.createSymbol", Status.BAD_REQUEST, e);
        }
    }

    /**
     * Create a bunch of new Symbols.
     *
     * @param projectId
     *            The ID of the project the symbol should belong to.
     * @param symbols
     *            The symbols to add.
     * @return On success the added symbols (enhanced with information from the DB); An error message on failure.
     * @responseType java.util.List<de.learnlib.alex.core.entities.Symbol>
     * @successResponse 201 created
     * @errorResponse   400 bad request `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @POST
    @Path("/batch")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response batchCreateSymbols(@PathParam("project_id") Long projectId, List<Symbol> symbols) {
        try {
            for (Symbol symbol : symbols) {
                checkSymbolBeforeCreation(projectId, symbol); // can throw an IllegalArgumentException
            }
            symbolDAO.create(symbols);

            String json = createSymbolsJSON(symbols);
            return Response.status(Status.CREATED).entity(json).build();

        } catch (IllegalArgumentException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.createSymbol", Status.BAD_REQUEST, e);
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.batchCreateSymbols",
                    Status.BAD_REQUEST, e);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could write the symbols from the DB into proper JSON!", e);
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.batchCreateSymbols",
                    Status.INTERNAL_SERVER_ERROR, null);
        }
    }

    private void checkSymbolBeforeCreation(Long projectId, Symbol symbol) {
        if (symbol.getProjectId() == 0L) {
            symbol.setProjectId(projectId);
        } else if (!Objects.equals(symbol.getProjectId(), projectId)) {
            throw new IllegalArgumentException("The symbol should not have a project"
                    + " or at least the project id should be the one provided via the get parameter");
        }
    }

    /**
     * Get all the Symbols of a specific Project.
     *
     * @param projectId
     *         The ID of the project.
     * @param visibilityLevel
     *         Specify the visibility level of the symbols you want to get.
     *         Valid values are: 'all'/ 'unknown', 'visible', 'hidden'.
     *         Optional.
     * @return A list of all Symbols belonging to the project.
     * @responseType java.util.List<de.learnlib.alex.core.entities.Symbol>
     * @successResponse 200 OK
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@PathParam("project_id") Long projectId,
                           @QueryParam("visibility") @DefaultValue("VISIBLE") SymbolVisibilityLevel visibilityLevel) {
        try {
            List<Symbol> symbols = null;
            try {
                symbols = symbolDAO.getAllWithLatestRevision(projectId, visibilityLevel);
            } catch (NotFoundException e) {
                symbols = new LinkedList<>();
            }

            String json = createSymbolsJSON(symbols);
            return Response.status(Status.OK).header("X-Total-Count", symbols.size()).entity(json).build();
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not write the symbols from the DB into proper JSON!", e);
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.getAll", Status.INTERNAL_SERVER_ERROR,
                    null);
        }
    }

    /**
     * Get a Symbol by its ID.
     * This returns only the latest revision of the symbol.
     * 
     * @param projectId
     *            The ID of the project.
     * @param id
     *            The ID of the symbol.
     * @return A Symbol matching the projectID & ID or a not found response.
     * @responseType de.learnlib.alex.core.entities.Symbol
     * @successResponse 200 OK
     * @errorResponse   404 not found `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("project_id") Long projectId, @PathParam("id") Long id) {
        try {
            Symbol symbol = symbolDAO.getWithLatestRevision(projectId, id);
            return Response.ok(symbol).build();
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.get", Status.NOT_FOUND, null);
        }
    }

    /**
     * Get a Symbol by its ID.
     * This returns all revisions of a symbol
     *
     * @param projectId
     *            The ID of the project.
     * @param id
     *            The ID of the symbol.
     * @return A Symbol matching the projectID & ID or a not found response.
     * @responseType    java.util.List<de.learnlib.alex.core.entities.Symbol>
     * @successResponse 200 OK
     * @errorResponse   404 not found `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @GET
    @Path("/{id}/complete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComplete(@PathParam("project_id") Long projectId, @PathParam("id") Long id) {
        try {
            List<Symbol> symbols = symbolDAO.getWithAllRevisions(projectId, id);
            String json = createSymbolsJSON(symbols);
            return Response.status(Status.OK).header("X-Total-Count", symbols.size()).entity(json).build();
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.getComplete", Status.NOT_FOUND, null);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not write the symbols from the DB into proper JSON!", e);
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.getComplete",
                                                               Status.INTERNAL_SERVER_ERROR,
                                                               null);
        }
    }

    /**
     * Get a Symbol by its ID & revision.
     * 
     * @param projectId
     *            The ID of the project.
     * @param id
     *            The ID of the symbol.
     * @param revision
     *            The revision of the symbol.
     * @return A Symbol matching the projectID, ID & revision or a not found response.
     * @responseType de.learnlib.alex.core.entities.Symbol
     * @successResponse 200 OK
     * @errorResponse   404 not found `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @GET
    @Path("/{id}:{revision}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWithRevision(@PathParam("project_id") Long projectId, @PathParam("id") Long id,
            @PathParam("revision") long revision) {
        try {
            Symbol symbol = symbolDAO.get(projectId, id, revision);
            return Response.ok(symbol).build();
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.getWithRevision",
                                                               Status.NOT_FOUND,
                                                               null);
        }
    }

    /**
     * Update a Symbol.
     * 
     * @param projectId
     *            The ID of the project.
     * @param id
     *            The ID of the symbol.
     * @param symbol
     *            The new symbol data.
     * @return On success the updated symbol (maybe enhanced with information from the DB); An error message on failure.
     * @responseType de.learnlib.alex.core.entities.Symbol
     * @successResponse 200 OK
     * @errorResponse   400 bad request `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     * @errorResponse   404 not found `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("project_id") Long projectId, @PathParam("id") Long id, Symbol symbol) {
        if (!Objects.equals(id, symbol.getId()) || !Objects.equals(projectId, symbol.getProjectId())) {
            return  Response.status(Status.BAD_REQUEST).build();
        }

        try {
            symbolDAO.update(symbol);
            return Response.ok(symbol).build();
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.update", Status.NOT_FOUND, e);
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.update", Status.BAD_REQUEST, e);
        }
    }

    /**
     * Update a bunch of Symbols.
     *
     * @param projectId
     *            The ID of the project.
     * @param ids
     *            The IDs of the symbols.
     * @param symbols
     *            The new symbol data.
     * @return On success the updated symbol (maybe enhanced with information from the DB); An error message on failure.
     * @responseType de.learnlib.alex.core.entities.Symbol
     * @successResponse 200 OK
     * @errorResponse   400 bad request `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     * @errorResponse   404 not found `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @PUT
    @Path("/batch/{ids}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response batchUpdate(@PathParam("project_id") Long projectId,
                                @PathParam("ids") IdsList ids,
                                List<Symbol> symbols) {
        Set idsSet = new HashSet<>(ids);
        for (Symbol symbol : symbols) {
            if (!idsSet.contains(symbol.getId()) || !Objects.equals(projectId, symbol.getProjectId())) {
                return Response.status(Status.BAD_REQUEST).build();
            }
        }
        try {
            symbolDAO.update(symbols);
            return Response.ok(symbols).header("X-Total-Count", symbols.size()).build();
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.batchUpdate", Status.NOT_FOUND, e);
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.batchUpdate", Status.BAD_REQUEST, e);
        }
    }

    /**
     * Move a Symbol to a new group.
     *
     * @param projectId
     *         The ID of the project.
     * @param symbolId
     *         The ID of the symbol.
     * @param groupId
     *         The ID of the new group.
     * @return On success the moved symbol (enhanced with information from the DB); An error message on failure.
     */
    @PUT
    @Path("/{symbol_id}/moveTo/{group_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response moveSymbolToAnotherGroup(@PathParam("project_id") Long projectId,
                                             @PathParam("symbol_id") Long symbolId,
                                             @PathParam("group_id") Long groupId) {
        try {
            Symbol symbol = symbolDAO.getWithLatestRevision(projectId, symbolId);
            symbolDAO.move(symbol, groupId);
            return Response.ok(symbol).build();
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.moveSymbolToAnotherGroup",
                                                               Status.NOT_FOUND, e);
        }
    }

    /**
     * Move a bunch of Symbols to a new group.
     *
     * @param projectId
     *         The ID of the project.
     * @param symbolIds
     *         The ID of the symbols.
     * @param groupId
     *         The ID of the new group.
     * @return On success the moved symbols (enhanced with information from the DB); An error message on failure.
     */
    @PUT
    @Path("/batch/{symbol_ids}/moveTo/{group_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response moveSymbolToAnotherGroup(@PathParam("project_id") Long projectId,
                                             @PathParam("symbol_ids") IdsList symbolIds,
                                             @PathParam("group_id") Long groupId) {
        try {
            List<Symbol> symbols = symbolDAO.getByIdsWithLatestRevision(projectId,
                                                                        symbolIds.toArray(new Long[symbolIds.size()]));
            symbolDAO.move(symbols, groupId);

            return Response.ok(symbols).build();
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.moveSymbolToAnotherGroup",
                                                               Status.NOT_FOUND, e);
        }
    }

    /**
     * Mark one symbol as hidden.
     * 
     * @param projectId
     *            The ID of the project.
     * @param id
     *            The ID of the symbol to hide.
     * @return On success no content will be returned; an error message on failure.
     * @successResponse 204 OK & no content
     * @errorResponse   404 not found `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @POST
    @Path("/{id}/hide")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hide(@PathParam("project_id") Long projectId, @PathParam("id") Long id) {
        try {
            symbolDAO.hide(projectId, id);
            Symbol symbol = symbolDAO.getWithLatestRevision(projectId, id);
            return Response.status(Status.OK).entity(symbol).build();

        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.hide", Status.NOT_FOUND, e);
        }
    }

    /**
     * Mark a bunch of symbols as hidden.
     *
     * @param projectId
     *            The ID of the project.
     * @param ids
     *            The IDs of the symbols to hide.
     * @return On success no content will be returned; an error message on failure.
     * @successResponse 204 OK & no content
     * @errorResponse   404 not found `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @POST
    @Path("/batch/{ids}/hide")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hide(@PathParam("project_id") long projectId, @PathParam("ids") IdsList ids) {
        try {
            Long[] idsArray = ids.toArray(new Long[ids.size()]);
            symbolDAO.hide(projectId, idsArray);
            List<Symbol> symbols = symbolDAO.getByIdsWithLatestRevision(projectId, idsArray);

            String json = createSymbolsJSON(symbols);
            return Response.status(Status.OK).header("X-Total-Count", symbols.size()).entity(json).build();
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.hide", Status.NOT_FOUND, e);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could write the symbols from the DB into proper JSON!", e);
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.hide", Status.INTERNAL_SERVER_ERROR,
                                                               null);
        }
    }

    /**
     * Remove the hidden flag from a symbol.
     *
     * @param projectId
     *            The ID of the project.
     * @param id
     *            The ID of the symbol to show.
     * @return On success no content will be returned; an error message on failure.
     * @successResponse 204 OK & no content
     * @errorResponse   404 not found `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @POST
    @Path("/{id}/show")
    @Produces(MediaType.APPLICATION_JSON)
    public Response show(@PathParam("project_id") long projectId, @PathParam("id") Long id) {
        try {
            symbolDAO.show(projectId, id);
            Symbol symbol = symbolDAO.getWithLatestRevision(projectId, id);
            return Response.status(Status.OK).entity(symbol).build();
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.show", Status.NOT_FOUND, e);
        }
    }

    /**
     * Remove the hidden flag from a bunch of symbols.
     *
     * @param projectId
     *            The ID of the project.
     * @param ids
     *            The IDs of the symbols to show.
     * @return On success no content will be returned; an error message on failure.
     * @successResponse 204 OK & no content
     * @errorResponse   404 not found `de.learnlib.alex.utils.ResourceErrorHandler.RESTError
     */
    @POST
    @Path("/batch/{ids}/show")
    @Produces(MediaType.APPLICATION_JSON)
    public Response show(@PathParam("project_id") long projectId, @PathParam("ids") IdsList ids) {
        try {
            Long[] idsArray = ids.toArray(new Long[ids.size()]);
            symbolDAO.show(projectId, idsArray);
            List<Symbol> symbols = symbolDAO.getByIdsWithLatestRevision(projectId, idsArray);

            String json = createSymbolsJSON(symbols);
            return Response.status(Status.OK).header("X-Total-Count", symbols.size()).entity(json).build();
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.show", Status.NOT_FOUND, e);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could write the symbols from the DB into proper JSON!", e);
            return ResourceErrorHandler.createRESTErrorMessage("SymbolResource.show", Status.INTERNAL_SERVER_ERROR,
                                                               null);
        }
    }

    /**
     * Create the JSON for a list of Symbols with the 'type' property. Workaround of a Jackson thing.
     * 
     * @param symbols
     *            The List of Symbols to convert into JSON.
     * @return The Symbols in JSON.
     * @throws JsonProcessingException
     *             If something went wrong while converting to JSON.
     */
    private String createSymbolsJSON(List<Symbol> symbols) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithType(new TypeReference<List<Symbol>>() { })
                .writeValueAsString(symbols);
    }

}