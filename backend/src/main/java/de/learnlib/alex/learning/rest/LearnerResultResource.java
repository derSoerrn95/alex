/*
 * Copyright 2018 TU Dortmund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.learnlib.alex.learning.rest;

import de.learnlib.alex.auth.entities.User;
import de.learnlib.alex.auth.security.UserPrincipal;
import de.learnlib.alex.common.exceptions.NotFoundException;
import de.learnlib.alex.common.utils.IdsList;
import de.learnlib.alex.common.utils.ResourceErrorHandler;
import de.learnlib.alex.common.utils.ResponseHelper;
import de.learnlib.alex.learning.dao.LearnerResultDAO;
import de.learnlib.alex.learning.entities.LearnerResult;
import de.learnlib.alex.learning.services.Learner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * REST API to fetch the test results.
 *
 * @resourcePath results
 * @resourceDescription Operations around the test results / hypotheses
 */
@Path("/projects/{project_id}/results")
public class LearnerResultResource {

    private static final Logger LOGGER = LogManager.getLogger();

    /** The {@link LearnerResultDAO} to use. */
    @Inject
    private LearnerResultDAO learnerResultDAO;

    /** The Learner to check if a result is not active before deletion. */
    @Inject
    private Learner learner;

    /** The security context containing the user of the request. */
    @Context
    private SecurityContext securityContext;

    /**
     * Get all learn results one project.
     *
     * @param projectId
     *         The project of the learn results.
     * @param embed
     *         By default no steps are included in the response. However you can ask to include them with this parameter
     *         set to 'steps'.
     * @return A List of all learn results within one project.
     * @throws NotFoundException
     *         If the related Project could not be found.
     * @successResponse 200 OK
     * @responseType java.util.List<de.learnlib.alex.learning.entities.LearnerResult>
     * @errorResponse 400 bad request `de.learnlib.alex.common.utils.ResourceErrorHandler.RESTError
     * @errorResponse 404 not found   `de.learnlib.alex.common.utils.ResourceErrorHandler.RESTError
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@PathParam("project_id") long projectId, @QueryParam("embed") String embed)
            throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        LOGGER.trace("LearnerResultResource.getAllFinalResults(" + projectId + ") for user " + user + ".");

        try {
            boolean includeSteps = parseEmbeddableFields(embed);

            List<LearnerResult> results = learnerResultDAO.getAll(user, projectId, includeSteps);
            return ResponseHelper.renderList(results, Response.Status.OK);
        } catch (IllegalArgumentException e) {
            LOGGER.traceExit(e);
            return ResourceErrorHandler.createRESTErrorMessage("LearnerResultResource.getAllSteps",
                    Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Get the latest learner result.
     *
     * @param projectId
     *         The id of the project.
     * @return 200 with The latest learner result. 204 If there is not result.
     * @throws NotFoundException
     *         If the project could not be found.
     */
    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatest(@PathParam("project_id") long projectId)
            throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        LOGGER.trace("LearnerResultResource.getLatest(" + projectId + ") for user " + user + ".");

        try {
            final LearnerResult result = learnerResultDAO.getLatest(user, projectId);
            return result == null ? Response.noContent().build() : Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            LOGGER.traceExit(e);
            return ResourceErrorHandler.createRESTErrorMessage("LearnerResultResource.getLatest",
                    Response.Status.NOT_FOUND, e);
        }
    }

    /**
     * Get one / a list of learn result(s).
     *
     * @param projectId
     *         The project of the learn result(s).
     * @param testNos
     *         The number(s) of the learn result(s).
     * @param embed
     *         By default no steps are included in the response. However you can ask to include them with this parameter
     *         set to 'steps'.
     * @return A List of all step of possible multiple test runs.
     * @throws NotFoundException
     *         If the requested results or the related Project could not be found.
     * @successResponse 200 OK
     * @responseType java.util.List<de.learnlib.alex.learning.entities.LearnerResult>
     * @errorResponse 400 bad request `de.learnlib.alex.common.utils.ResourceErrorHandler.RESTError
     * @errorResponse 404 not found `de.learnlib.alex.common.utils.ResourceErrorHandler.RESTError
     */
    @GET
    @Path("{test_nos}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@PathParam("project_id") Long projectId,
            @PathParam("test_nos") IdsList testNos,
            @QueryParam("embed") String embed)
            throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        LOGGER.trace("LearnerResultResource.getAllSteps(" + projectId + ", " + testNos + ") for user " + user + ".");

        try {
            boolean includeSteps = parseEmbeddableFields(embed);

            if (testNos.size() == 1) {
                LearnerResult result = learnerResultDAO.get(user, projectId, testNos.get(0), includeSteps);
                return Response.ok(result).build();
            } else {
                List<LearnerResult> result = learnerResultDAO.getAll(
                        user, projectId,
                        testNos.toArray(new Long[testNos.size()]),
                        includeSteps);
                return ResponseHelper.renderList(result, Response.Status.OK);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.traceExit(e);
            return ResourceErrorHandler.createRESTErrorMessage("LearnerResultResource.getAllSteps",
                    Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Clone a learner result.
     *
     * @param projectId
     *         The ID of the project.
     * @param testNo
     *         The test no of the learner result to clone.
     * @return The cloned learner result.
     * @throws NotFoundException
     *         If one of the entities could not be found.
     */
    @POST
    @Path("{test_no}/clone")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clone(@PathParam("project_id") Long projectId, @PathParam("test_no") Long testNo)
            throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        LOGGER.traceEntry("LearnerResultResource.clone(" + projectId + ", " + testNo + ") for user " + user + ".");

        try {
            final LearnerResult clonedResult = learnerResultDAO.clone(user, projectId, testNo);
            return Response.status(Response.Status.CREATED).entity(clonedResult).build();
        } catch (IllegalArgumentException e) {
            LOGGER.traceExit(e);
            return ResourceErrorHandler.createRESTErrorMessage("LearnerResultResource.clone",
                    Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Delete one or more learn result(s).
     *
     * @param projectId
     *         The project of the learn results.
     * @param testNumbers
     *         The test numbers of the results to delete as a comma (',') separated list. E.g. 1,2,3
     * @return On success no content will be returned.
     * @throws NotFoundException
     *         If the given results or the related Project could not be found.
     * @successResponse 204 OK & no content
     * @errorResponse 400 bad request `de.learnlib.alex.common.utils.ResourceErrorHandler.RESTError
     * @errorResponse 404 not found   `de.learnlib.alex.common.utils.ResourceErrorHandler.RESTError
     */
    @DELETE
    @Path("{test_numbers}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteResultSet(@PathParam("project_id") Long projectId,
            @PathParam("test_numbers") IdsList testNumbers)
            throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        LOGGER.trace("LearnerResultResource.deleteResultSet(" + projectId + ", " + testNumbers + ") "
                + "for user " + user + ".");

        try {
            Long[] numbersLongArray = testNumbers.toArray(new Long[testNumbers.size()]);
            learnerResultDAO.delete(learner, projectId, numbersLongArray);
            return Response.status(Response.Status.NO_CONTENT).build();

        } catch (ValidationException e) {
            LOGGER.traceExit(e);
            return ResourceErrorHandler.createRESTErrorMessage("LearnerResultResource.deleteResultSet",
                    Response.Status.BAD_REQUEST, e);
        }
    }

    private boolean parseEmbeddableFields(String embed) throws IllegalArgumentException {
        if (embed == null
                || embed.isEmpty()) {
            return false;
        } else if (embed.toLowerCase().equals("steps")) {
            return true;
        } else {
            throw new IllegalArgumentException("Could not parse the embed value '" + embed + "'.");
        }
    }

}
