/*
 * Copyright 2016 TU Dortmund
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

package de.learnlib.alex.data.dao;

import de.learnlib.alex.auth.entities.User;
import de.learnlib.alex.common.exceptions.NotFoundException;
import de.learnlib.alex.data.entities.Project;
import de.learnlib.alex.data.entities.SymbolGroup;
import de.learnlib.alex.data.repositories.ProjectRepository;
import de.learnlib.alex.data.repositories.SymbolActionRepository;
import de.learnlib.alex.data.repositories.SymbolGroupRepository;
import de.learnlib.alex.data.repositories.SymbolRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SymbolGroupDAOImplTest {

    private static final long USER_ID    = 21L;
    private static final long PROJECT_ID = 42L;
    private static final long GROUP_ID   = 84L;
    private static final int  TEST_GROUP_COUNT = 3;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectDAO projectDAO;

    @Mock
    private SymbolGroupRepository symbolGroupRepository;

    @Mock
    private SymbolRepository symbolRepository;

    @Mock
    private SymbolActionRepository symbolActionRepository;

    private SymbolGroupDAO symbolGroupDAO;

    @Before
    public void setUp() {
        symbolGroupDAO = new SymbolGroupDAOImpl(projectRepository, projectDAO, symbolGroupRepository, symbolRepository,
                                                symbolActionRepository);
    }

    @Test
    public void shouldCreateAValidGroup() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        SymbolGroup group = new SymbolGroup();
        group.setProject(project);
        //
        given(projectDAO.getByID(USER_ID, PROJECT_ID, ProjectDAO.EmbeddableFields.ALL)).willReturn(project);
        given(symbolGroupRepository.save(group)).willReturn(group);

        symbolGroupDAO.create(user, group);

        verify(symbolGroupRepository).save(group);
    }

    @Test(expected = ValidationException.class)
    public void shouldHandleDataIntegrityViolationExceptionOnGroupCreationGracefully() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        SymbolGroup group = new SymbolGroup();
        group.setProject(project);
        //
        given(projectDAO.getByID(USER_ID, PROJECT_ID, ProjectDAO.EmbeddableFields.ALL)).willReturn(project);
        given(symbolGroupRepository.save(group)).willThrow(DataIntegrityViolationException.class);

        symbolGroupDAO.create(user, group); // should fail
    }

    @Test(expected = ValidationException.class)
    public void shouldHandleTransactionSystemExceptionOnGroupCreationGracefully() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        SymbolGroup group = new SymbolGroup();
        group.setProject(project);
        //
        ConstraintViolationException constraintViolationException;
        constraintViolationException = new ConstraintViolationException("Project is not valid!", new HashSet<>());
        RollbackException rollbackException = new RollbackException("RollbackException", constraintViolationException);
        TransactionSystemException transactionSystemException;
        transactionSystemException = new TransactionSystemException("Spring TransactionSystemException",
                                                                    rollbackException);
        //
        given(projectDAO.getByID(USER_ID, PROJECT_ID, ProjectDAO.EmbeddableFields.ALL)).willReturn(project);
        given(symbolGroupRepository.save(group)).willThrow(transactionSystemException);

        symbolGroupDAO.create(user, group); // should fail
    }

    @Test
    public void shouldGetAllGroupsOfAProject() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        List<SymbolGroup> groups = createGroupsList();
        //
        given(projectDAO.getByID(USER_ID, PROJECT_ID)).willReturn(project);
        given(symbolGroupRepository.findAllByProject_Id(PROJECT_ID)).willReturn(groups);

        List<SymbolGroup> allGroups = symbolGroupDAO.getAll(user, PROJECT_ID);

        assertThat(allGroups.size(), is(equalTo(groups.size())));
        for (SymbolGroup g : allGroups) {
            assertTrue(groups.contains(g));
        }
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowAnExceptionIfYouWantToGetAllGroupsOfANonExistingProject() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);

        given(projectDAO.getByID(USER_ID, -1L)).willThrow(new NotFoundException());

        symbolGroupDAO.getAll(user, -1L);
    }

    @Test
    public void shouldGetAGroupByItsID() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        SymbolGroup group = new SymbolGroup();
        group.setId(GROUP_ID);
        group.setProject(project);
        //
        given(symbolGroupRepository.findOneByProject_IdAndId(PROJECT_ID, GROUP_ID))
                                                                                                     .willReturn(group);

        SymbolGroup g = symbolGroupDAO.get(user, PROJECT_ID, group.getId());

        assertThat(g, is(equalTo(group)));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowAnExceptionIfTheGroupCanNotBeFound() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);

        symbolGroupDAO.get(user, -1L, -1L); // should fail
    }

    @Test
    public void shouldUpdateAGroup() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        SymbolGroup group = new SymbolGroup();
        group.setId(GROUP_ID);
        group.setProject(project);
        //
        given(symbolGroupRepository.findOneByProject_IdAndId(PROJECT_ID, GROUP_ID))
                                                                                                     .willReturn(group);

        symbolGroupDAO.update(user, group);

        verify(symbolGroupRepository).save(group);
    }

    @Test(expected = ValidationException.class)
    public void shouldHandleDataIntegrityViolationExceptionOnGroupUpdateGracefully() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        SymbolGroup group = new SymbolGroup();
        group.setId(GROUP_ID);
        group.setProject(project);
        //
        given(symbolGroupRepository.findOneByProject_IdAndId(PROJECT_ID, GROUP_ID))
                                                                                                     .willReturn(group);
        given(symbolGroupRepository.save(group)).willThrow(DataIntegrityViolationException.class);

        symbolGroupDAO.update(user, group); // should fail
    }

    @Test(expected = ValidationException.class)
    public void shouldHandleTransactionSystemExceptionOnGroupUpdateGracefully() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        SymbolGroup group = new SymbolGroup();
        group.setId(GROUP_ID);
        group.setProject(project);
        //
        ConstraintViolationException constraintViolationException;
        constraintViolationException = new ConstraintViolationException("Project is not valid!", new HashSet<>());
        RollbackException rollbackException = new RollbackException("RollbackException", constraintViolationException);
        TransactionSystemException transactionSystemException;
        transactionSystemException = new TransactionSystemException("Spring TransactionSystemException",
                                                                    rollbackException);
        //
        given(symbolGroupRepository.findOneByProject_IdAndId(PROJECT_ID, GROUP_ID))
                                                                                                     .willReturn(group);
        given(symbolGroupRepository.save(group)).willThrow(transactionSystemException);

        symbolGroupDAO.update(user, group); // should fail
    }

    @Test
    public void shouldDeleteAGroup() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        SymbolGroup group = new SymbolGroup();
        group.setId(GROUP_ID);
        group.setProject(project);
        //
        given(projectDAO.getByID(USER_ID, PROJECT_ID, ProjectDAO.EmbeddableFields.DEFAULT_GROUP)).willReturn(project);
        given(symbolGroupRepository.findOneByProject_IdAndId(PROJECT_ID, GROUP_ID))
                                                                                                     .willReturn(group);

        symbolGroupDAO.delete(user, PROJECT_ID, GROUP_ID);

        verify(symbolGroupRepository).delete(group);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotDeleteTheDefaultGroupOfAProject() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        SymbolGroup group = new SymbolGroup();
        group.setId(GROUP_ID);
        group.setProject(project);
        project.setDefaultGroup(group);
        //
        given(projectDAO.getByID(USER_ID, PROJECT_ID, ProjectDAO.EmbeddableFields.DEFAULT_GROUP)).willReturn(project);
        given(symbolGroupRepository.findOneByProject_IdAndId(PROJECT_ID, GROUP_ID)).willReturn(group);

        symbolGroupDAO.delete(user, PROJECT_ID, GROUP_ID); // should fail
    }

    @Test(expected = NotFoundException.class)
    public void shouldFailToDeleteAProjectThatDoesNotExist() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);

        symbolGroupDAO.delete(user, PROJECT_ID, -1L);
    }


    private List<SymbolGroup> createGroupsList() {
        List<SymbolGroup> groups = new LinkedList<>();
        for (int i = 0; i  < TEST_GROUP_COUNT; i++) {
            SymbolGroup g = new SymbolGroup();
            groups.add(g);
        }
        return groups;
    }

}
