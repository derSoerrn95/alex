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

package de.learnlib.alex.testing.rest;

import de.learnlib.alex.auth.entities.User;
import de.learnlib.alex.auth.security.UserPrincipal;
import de.learnlib.alex.common.exceptions.NotFoundException;
import de.learnlib.alex.common.utils.IdsList;
import de.learnlib.alex.common.utils.ResourceErrorHandler;
import de.learnlib.alex.data.dao.ProjectDAO;
import de.learnlib.alex.data.entities.Project;
import de.learnlib.alex.testing.dao.TestDAO;
import de.learnlib.alex.testing.entities.Test;
import de.learnlib.alex.testing.entities.TestCase;
import de.learnlib.alex.testing.entities.TestExecutionConfig;
import de.learnlib.alex.testing.entities.TestReport;
import de.learnlib.alex.testing.entities.TestResult;
import de.learnlib.alex.testing.entities.TestStatus;
import de.learnlib.alex.testing.events.TestEvent;
import de.learnlib.alex.testing.services.TestService;
import de.learnlib.alex.webhooks.services.WebhookService;
import org.apache.shiro.authz.UnauthorizedException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST endpoints for working with tests.
 *
 * @resourcePath tests
 * @resourceDescription Endpoints for working with tests.
 */
@Path("/projects/{project_id}/tests")
@RolesAllowed({"REGISTERED"})
public class TestResource {

    /** The security context containing the user of the request. */
    @Context
    private SecurityContext securityContext;

    /** The {@link TestDAO} to use. */
    @Inject
    private TestDAO testDAO;

    /** The test service. */
    @Inject
    private TestService testService;

    /** The {@link WebhookService} to use. */
    @Inject
    private WebhookService webhookService;

    /** The {@link ProjectDAO} to use. */
    @Inject
    private ProjectDAO projectDAO;

    /**
     * Create a test.
     *
     * @param projectId
     *         The id of the project to create the test in.
     * @param test
     *         The test to create.
     * @return The created test.
     * @throws NotFoundException
     *         If the project with the given id could not be found.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTest(@PathParam("project_id") Long projectId, Test test) throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        projectDAO.getByID(user.getId(), projectId);

        test.setProjectId(projectId);

        try {
            testDAO.create(user, test);
            webhookService.fireEvent(user, new TestEvent.Created(test));
            return Response.ok(test).status(Response.Status.CREATED).build();
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("TestCase.create", Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Create multiple tests at once.
     *
     * @param projectId
     *         The id of the project to create the tests in.
     * @param tests
     *         The tests to create.
     * @return The created tests.
     * @throws NotFoundException
     *         If the project with the given id could not be found.
     */
    @POST
    @Path("/batch")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTests(@PathParam("project_id") Long projectId, List<Test> tests) throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        projectDAO.getByID(user.getId(), projectId);

        tests.forEach(test -> test.setProjectId(projectId));

        try {
            testDAO.create(user, tests);

            final List<Test> createdTests = new ArrayList<>();
            for (final Test t : tests) {
                createdTests.add(testDAO.get(user, projectId, t.getId()));
            }

            webhookService.fireEvent(user, new TestEvent.CreatedMany(createdTests));
            return Response.ok(createdTests).status(Response.Status.CREATED).build();
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("TestCase.create", Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Get a single test.
     *
     * @param projectId
     *         The id of the project.
     * @param id
     *         The id of the tests.
     * @return The test.
     * @throws NotFoundException
     *         If the project or the test could not be found.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("project_id") Long projectId, @PathParam("id") Long id) throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();

        try {
            final Test test = testDAO.get(user, projectId, id);
            return Response.ok(test).build();
        } catch (UnauthorizedException e) {
            return ResourceErrorHandler.createRESTErrorMessage("TestCase.get", Response.Status.UNAUTHORIZED, e);
        }
    }

    /**
     * Get all tests in a project.
     *
     * @param projectId
     *         The if of the project.
     * @param type
     *         The type of the test.
     * @return The list of projects.
     * @throws NotFoundException
     *         If an entity was not found.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@PathParam("project_id") Long projectId, @QueryParam("type") String type)
            throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();

        try {
            final List<Test> tests = testDAO.getByType(user, projectId, type);
            return Response.ok(tests).build();
        } catch (UnauthorizedException e) {
            return ResourceErrorHandler.createRESTErrorMessage("Tests.get", Response.Status.UNAUTHORIZED, e);
        } catch (IllegalArgumentException e) {
            return ResourceErrorHandler.createRESTErrorMessage("Tests.get", Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Get a single test.
     *
     * @param projectId
     *         The id of the project.
     * @return The test.
     * @throws NotFoundException
     *         If the project or the test could not be found.
     */
    @GET
    @Path("/root")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("project_id") Long projectId) throws NotFoundException {
        final User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        final Test root = testDAO.getRoot(user, projectId);
        return Response.ok(root).build();
    }

    /**
     * Execute a test.
     *
     * @param projectId
     *         The id of the project.
     * @param id
     *         The id of the test.
     * @param testConfig
     *         The configuration to run the test with.
     * @return The result of the test execution.
     * @throws NotFoundException
     *         If the project or the test could not be found.
     */
    @POST
    @Path("/{id}/execute")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(@PathParam("project_id") Long projectId, @PathParam("id") Long id,
            TestExecutionConfig testConfig) throws NotFoundException {
        final User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        final Test test = testDAO.get(user, projectId, id);

        if (!(test instanceof TestCase)) {
            final Exception e = new Exception("The test is not a test case.");
            return ResourceErrorHandler.createRESTErrorMessage("TestResource.execute", Response.Status.BAD_REQUEST, e);
        }

        final Map<Long, TestResult> results = new HashMap<>();
        testService.executeTestCase(user, (TestCase) test, testConfig, results);

        final TestReport report = new TestReport();
        report.setTestResults(new ArrayList<>(results.values()));

        return Response.ok(report).build();
    }

    /**
     * Executes a test run that can contains multiple tests.
     *
     * @param projectId
     *         The id of the project
     * @param testConfig
     *         The configuration for the test
     * @return A {@link TestReport}.
     * @throws NotFoundException
     *         If the project or a test could not be found.
     */
    @POST
    @Path("/execute")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(@PathParam("project_id") Long projectId,
            TestExecutionConfig testConfig)
            throws NotFoundException {
        final User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        final Project project = projectDAO.getByID(user.getId(), projectId);

        if (testService.isActive(user, projectId)) {
            return ResourceErrorHandler.createRESTErrorMessage("TestResource.execute",
                    Response.Status.BAD_REQUEST, "There is already a testing process running for this project.");
        } else {
            final TestStatus status = testService.start(user, project, testConfig);
            return Response.ok(status).build();
        }
    }

    /**
     * Get the status of the current test process.
     *
     * @param projectId
     *         The id of the project.
     * @return Status 200 with a {@link TestStatus}.
     * @throws NotFoundException
     *         If the project could not be found.
     */
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response status(@PathParam("project_id") Long projectId) throws NotFoundException {
        final User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
        final Project project = projectDAO.getByID(user.getId(), projectId);
        final TestStatus status = testService.getStatus(user, project);
        return Response.ok(status).build();
    }

    /**
     * Update a test.
     *
     * @param projectId
     *         The id of the project.
     * @param id
     *         The id of the test.
     * @param test
     *         The updated test.
     * @return The updated test.
     * @throws NotFoundException
     *         If the project or the test could not be found.
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("project_id") Long projectId,
            @PathParam("id") Long id,
            Test test) throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();

        if (test.getId() == null) {
            test.setId(id);
        }

        if (!test.getId().equals(id)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        test.setProjectId(projectId);

        try {
            testDAO.update(user, test);

            webhookService.fireEvent(user, new TestEvent.Updated(test));
            return Response.ok(test).build();
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("TestCase.update", Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Move tests to another test suite.
     *
     * @param projectId
     *         The id of the project.
     * @param testIds
     *         The ids of the tests to move.
     * @param targetId
     *         The id of the target test suite.
     * @return 200 If the tests have been moved successfully.
     * @throws NotFoundException
     *         If one of the entities could not be found.
     */
    @PUT
    @Path("/batch/{testIds}/moveTo/{targetId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response move(@PathParam("project_id") Long projectId, @PathParam("testIds") IdsList testIds,
            @PathParam("targetId") Long targetId) throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();

        try {
            final List<Test> movedTests = testDAO.move(user, projectId, testIds, targetId);
            webhookService.fireEvent(user, new TestEvent.MovedMany(movedTests));
            return Response.ok(movedTests).build();
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("TestResource.move", Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Delete a test.
     *
     * @param projectId
     *         The id of the project.
     * @param id
     *         The id of the test.
     * @return An empty body if the test has been deleted.
     * @throws NotFoundException
     *         If the project or the test could not be found.
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("project_id") Long projectId, @PathParam("id") Long id) throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();

        try {
            testDAO.delete(user, projectId, id);
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("TestCase.delete", Response.Status.NOT_FOUND, e);
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("TestCase.delete", Response.Status.BAD_REQUEST, e);
        }

        webhookService.fireEvent(user, new TestEvent.Deleted(id));
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Deletes multiple tests.
     *
     * @param projectId
     *         The id of the project.
     * @param ids
     *         The ids of the tests to delete.
     * @return An empty body if the project has been deleted.
     * @throws NotFoundException
     *         If the project or a test could not be found.
     */
    @DELETE
    @Path("/batch/{ids}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("project_id") Long projectId,
            @PathParam("ids") IdsList ids) throws NotFoundException {
        User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();

        try {
            testDAO.delete(user, projectId, ids);
        } catch (NotFoundException e) {
            return ResourceErrorHandler.createRESTErrorMessage("TestCase.delete", Response.Status.NOT_FOUND, e);
        } catch (ValidationException e) {
            return ResourceErrorHandler.createRESTErrorMessage("TestCase.delete", Response.Status.BAD_REQUEST, e);
        }

        webhookService.fireEvent(user, new TestEvent.DeletedMany(ids));
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
