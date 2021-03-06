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

package de.learnlib.alex.data.rest;

import de.learnlib.alex.ALEXTestApplication;
import de.learnlib.alex.auth.entities.User;
import de.learnlib.alex.common.exceptions.NotFoundException;
import de.learnlib.alex.data.dao.ProjectDAO;
import de.learnlib.alex.data.entities.Project;
import de.learnlib.alex.data.entities.ProjectUrl;
import de.learnlib.alex.data.entities.Symbol;
import de.learnlib.alex.webhooks.services.WebhookService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ValidationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ProjectResourceTest extends JerseyTest {

    private static final Long USER_TEST_ID = 1L;
    private static final Long PROJECT_TEST_ID = 1L;

    @Mock
    private ProjectDAO projectDAO;

    private User admin;
    private String adminToken;

    private Project project;

    @Override
    protected Application configure() {
        MockitoAnnotations.initMocks(this);

        ALEXTestApplication testApplication = new ALEXTestApplication(ProjectResource.class);

        admin = testApplication.getAdmin();
        adminToken = testApplication.getAdminToken();

        testApplication.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(projectDAO).to(ProjectDAO.class);
                bind(mock(WebhookService.class)).to(WebhookService.class);
            }
        });

        Symbol symbol = new Symbol();
        symbol.setName("Project Resource Test Symbol");

        ProjectUrl projectUrl = new ProjectUrl();
        projectUrl.setDefault(true);
        projectUrl.setUrl("http://www.example.com");

        project = new Project();
        project.setUser(admin);
        project.setId(PROJECT_TEST_ID);
        project.setName("Test Project");
        project.addSymbol(symbol);

        try {
            given(projectDAO.getByID(USER_TEST_ID, PROJECT_TEST_ID)).willReturn(project);
        } catch (NotFoundException e) {
            e.printStackTrace();
            fail();
        }

        return testApplication;
    }

    @Test
    public void shouldCreateAProject() {
        String json = "{\"id\": " + project.getId() + ","
                        + "\"name\": \"" + project.getName() + "\","
                        + "\"urls\": [{\"name\": null, \"url\": \"" + project.getDefaultUrl() + "\", \"default\": true}],"
                        + "\"description\": \"" + project.getDescription() + "\"}";
        Response response = target("/projects").request().header("Authorization", adminToken).post(Entity.json(json));

        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        verify(projectDAO).create(project);
    }

    @Test
    public void shouldReturn400IfProjectCouldNotBeCreated() {
        willThrow(new ValidationException("Test Message")).given(projectDAO).create(project);

        Response response = target("/projects").request().header("Authorization", adminToken)
                                .post(Entity.json(project));

        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void shouldReturn400IfProjectNameAlreadyExistsForAUser() {
        Project p = new Project();
        p.setUser(admin);
        p.setName("Test Project");

        willThrow(new ValidationException("Test Message")).given(projectDAO).create(p);

        Response response = target("/projects").request().header("Authorization", adminToken)
                                .post(Entity.json(p));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void shouldReturnAllProjectsWithoutEmbedded() {
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        given(projectDAO.getAll(admin)).willReturn(projects);

        Response response = target("/projects").request().header("Authorization", adminToken).get();

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals("1", response.getHeaderString("X-Total-Count"));
        verify(projectDAO).getAll(admin);
    }

    @Test
    public void shouldReturnAllProjectsWithEmbedded() {
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        given(projectDAO.getAll(admin, ProjectDAO.EmbeddableFields.SYMBOLS))
                .willReturn(projects);

        Response response = target("/projects").queryParam("embed", "symbols").request()
                                .header("Authorization", adminToken).get();

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals("1", response.getHeaderString("X-Total-Count"));
        verify(projectDAO).getAll(admin, ProjectDAO.EmbeddableFields.SYMBOLS);
    }

    @Test
    public void shouldGetTheRightProjectWithoutEmbeddedFields() throws NotFoundException {
        Response response = target("/projects/" + PROJECT_TEST_ID).request().header("Authorization", adminToken).get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        verify(projectDAO).getByID(USER_TEST_ID, PROJECT_TEST_ID);
    }

    @Test
    public void shouldGetTheRightProjectWithEmbedded() throws NotFoundException {
        given(projectDAO.getByID(USER_TEST_ID,
                                 PROJECT_TEST_ID,
                                 ProjectDAO.EmbeddableFields.SYMBOLS))
                .willReturn(project);
        Response response = target("/projects/" + PROJECT_TEST_ID).queryParam("embed", "symbols")
                                .request().header("Authorization", adminToken).get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        verify(projectDAO).getByID(USER_TEST_ID,
                                   PROJECT_TEST_ID,
                                   ProjectDAO.EmbeddableFields.SYMBOLS);
    }

    @Test
    public void shouldReturn404WhenProjectNotFound() throws NotFoundException {
        given(projectDAO.getByID(eq(USER_TEST_ID), eq(PROJECT_TEST_ID), any())).willThrow(NotFoundException.class);

        Response response = target("/projects/" + PROJECT_TEST_ID).request().header("Authorization", adminToken).get();

        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(projectDAO).getByID(USER_TEST_ID, PROJECT_TEST_ID);
    }

    @Test
    public void shouldUpdateTheRightProject() throws NotFoundException {
        String json = "{\"id\": " + project.getId() + ","
                        + "\"name\": \"" + project.getName() + "\","
                        + "\"urls\": [{\"name\": null, \"url\": \"" + project.getDefaultUrl() + "\", \"default\": true}],"
                        + "\"description\": \"" + project.getDescription() + "\"}";

        target("/project").request().header("Authorization", adminToken).post(Entity.json(project));
        Response response = target("/projects/" + project.getId()).request().header("Authorization", adminToken)
                                .put(Entity.json(json));

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        verify(projectDAO).update(admin, project);
    }

    @Test
    public void shouldFailIfIdInUrlAndObjectAreDifferent() throws NotFoundException {
        project.setName("Test Project - Update Diff");
        target("/project").request().post(Entity.json(project));
        Response response = target("/projects/" + (project.getId() + 1)).request().header("Authorization", adminToken)
                                .put(Entity.json(project));

        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(projectDAO, never()).update(admin, project);
    }

    @Test
    public void shouldReturn400OnUpdateWhenProjectIsInvalid() throws NotFoundException {
        project.setName("Test Project - Invalid Test");

        willThrow(new ValidationException()).given(projectDAO).update(admin, project);
        Response response = target("/projects/" + project.getId()).request().header("Authorization", adminToken)
                                .put(Entity.json(project));

        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void shouldDeleteTheRightProject() throws NotFoundException {
        target("/project").request().post(Entity.json(project));

        Response response = target("/projects/" + project.getId()).request().header("Authorization", adminToken)
                                .delete();
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        verify(projectDAO).delete(admin, project.getId());
    }

    @Test
    public void shouldReturn404OnDeleteWhenProjectNotFound() throws NotFoundException {
        willThrow(NotFoundException.class).given(projectDAO).delete(admin, PROJECT_TEST_ID);
        Response response = target("/projects/" + PROJECT_TEST_ID).request().header("Authorization", adminToken)
                                .delete();

        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

}
