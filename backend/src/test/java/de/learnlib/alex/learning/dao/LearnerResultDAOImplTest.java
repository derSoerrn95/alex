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

package de.learnlib.alex.learning.dao;

import de.learnlib.alex.auth.entities.User;
import de.learnlib.alex.common.exceptions.NotFoundException;
import de.learnlib.alex.data.dao.ProjectDAO;
import de.learnlib.alex.data.entities.Project;
import de.learnlib.alex.learning.entities.LearnerResult;
import de.learnlib.alex.learning.entities.LearnerResultStep;
import de.learnlib.alex.learning.entities.LearnerResumeConfiguration;
import de.learnlib.alex.learning.entities.LearnerStatus;
import de.learnlib.alex.learning.entities.Statistics;
import de.learnlib.alex.learning.entities.learnlibproxies.CompactMealyMachineProxy;
import de.learnlib.alex.learning.entities.learnlibproxies.eqproxies.MealyRandomWordsEQOracleProxy;
import de.learnlib.alex.learning.repositories.LearnerResultRepository;
import de.learnlib.alex.learning.repositories.LearnerResultStepRepository;
import de.learnlib.alex.learning.services.Learner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityManager;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LearnerResultDAOImplTest {

    private static final long USER_ID = 21L;
    private static final long PROJECT_ID = 42L;
    private static final int RESULTS_AMOUNT = 3;
    private static final MealyRandomWordsEQOracleProxy EXAMPLE_EQ_ORACLE =
            new MealyRandomWordsEQOracleProxy(1, 5, 10, 42);

    @Mock
    private ProjectDAO projectDAO;

    @Mock
    private LearnerResultRepository learnerResultRepository;

    @Mock
    private LearnerResultStepRepository learnerResultStepRepository;

    @Mock
    private Learner learner;

    @Mock
    private EntityManager entityManager;

    private LearnerResultDAO learnerResultDAO;


    @Before
    public void setUp() {
        learnerResultDAO = new LearnerResultDAOImpl(projectDAO, learnerResultRepository, learnerResultStepRepository,
                entityManager);
    }

    @Test
    public void shouldSaveAValidLearnerResult() {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        LearnerResult result = new LearnerResult();
        result.setProject(project);
        //
        given(learnerResultRepository.findHighestTestNo(PROJECT_ID)).willReturn(1L);
        given(learnerResultRepository.save(result)).willReturn(result);

        try {
            learnerResultDAO.create(user, result);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        verify(learnerResultRepository).save(result);
        assertThat(result.getTestNo(), is(equalTo(2L)));
    }

    @Test
    public void shouldSaveAValidLearnerResultWhenItIsTheFirstResult() {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        LearnerResult result = new LearnerResult();
        result.setProject(project);
        //
        given(learnerResultRepository.findHighestTestNo(PROJECT_ID)).willReturn(null);
        given(learnerResultRepository.save(result)).willReturn(result);

        try {
            learnerResultDAO.create(user, result);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        verify(learnerResultRepository).save(result);
        assertThat(result.getTestNo(), is(equalTo(0L)));
    }

    @Test(expected = ValidationException.class)
    public void shouldHandleDataIntegrityViolationExceptionOnLearnerResultCreationGracefully() {
        User user = new User();
        user.setId(USER_ID);
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        LearnerResult result = new LearnerResult();
        result.setProject(project);
        //
        given(learnerResultRepository.save(result)).willThrow(DataIntegrityViolationException.class);

        try {
            learnerResultDAO.create(user, result); // should fail
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveALearnResultWithoutAProject() {
        User user = new User();
        //
        LearnerResult result = new LearnerResult();

        try {
            learnerResultDAO.create(user, result); // should fail
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveALearnerResultWithATestNo() {
        User user = new User();
        //
        Project project = new Project();
        //
        LearnerResult result = new LearnerResult();
        result.setProject(project);
        result.setTestNo(0L);

        try {
            learnerResultDAO.create(user, result); // should fail
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldGetAllResultsOfOneProject() throws NotFoundException {
        User user = new User();
        //
        List<LearnerResult> results = createLearnerResultsList();
        //
        given(learnerResultRepository.findByProject_IdOrderByTestNoAsc(PROJECT_ID))
                .willReturn(results);

        List<LearnerResult> resultsFromDAO = learnerResultDAO.getAll(user, PROJECT_ID, true);

        assertThat(results.size(), is(equalTo(resultsFromDAO.size())));
        for (LearnerResult r : resultsFromDAO) {
            assertTrue(results.contains(r));
        }
    }

    @Test
    public void ensureThatGettingAllResultsReturnsAnEmptyListIfNoLearnerResultCouldBeFound() throws NotFoundException {
        User user = new User();

        given(learnerResultRepository.findByProject_IdOrderByTestNoAsc(PROJECT_ID))
                .willReturn(Collections.emptyList());

        List<LearnerResult> results = learnerResultDAO.getAll(user, PROJECT_ID, true);
        assertEquals(results.size(), 0);
    }


    @Test
    public void shouldGetMultipleResults() throws NotFoundException {
        User user = new User();
        //
        List<LearnerResult> results = createLearnerResultsList();
        Long[] testNos = new Long[]{0L, 1L, 2L};
        //
        given(learnerResultRepository.findByProject_IdAndTestNoIn(PROJECT_ID, testNos))
                .willReturn(results);

        List<LearnerResult> resultsFromDAO = learnerResultDAO.getAll(user, PROJECT_ID, testNos, true);

        assertThat(results.size(), is(equalTo(resultsFromDAO.size())));
        for (LearnerResult r : resultsFromDAO) {
            assertTrue(results.contains(r));
        }
    }

    @Test(expected = NotFoundException.class)
    public void ensureThatGettingMultipleResultThrowsAnExceptionIfOneResultIdIsInvalid() throws NotFoundException {
        User user = new User();
        //
        Long[] testNos = new Long[]{0L, 1L, 2L};
        //
        given(learnerResultRepository.findByProject_IdAndTestNoIn(PROJECT_ID, testNos))
                .willReturn(Collections.emptyList());

        learnerResultDAO.getAll(user, PROJECT_ID, testNos, true); // should fail
    }

    @Test
    public void shouldGetOneResult() throws NotFoundException {
        User user = new User();
        //
        LearnerResult result = new LearnerResult();
        Long[] testNos = new Long[]{0L};
        //
        given(learnerResultRepository.findByProject_IdAndTestNoIn(PROJECT_ID, testNos[0]))
                .willReturn(Collections.singletonList(result));

        LearnerResult resultFromDAO = learnerResultDAO.get(user, PROJECT_ID, 0L, true);

        assertThat(resultFromDAO, is(equalTo(result)));
    }

    @Test(expected = NotFoundException.class)
    public void ensureThatGettingANonExistingResultThrowsAnException() throws NotFoundException {
        User user = new User();
        //
        Long[] testNos = new Long[]{0L};
        //
        given(learnerResultRepository.findByProject_IdAndTestNoIn(PROJECT_ID, testNos[0]))
                .willReturn(Collections.emptyList());

        learnerResultDAO.get(user, PROJECT_ID, 0L, true); // should fail
    }

    @Test
    public void shouldCreateAStepFromValidConfiguration() throws NotFoundException {
        User user = new User();
        //
        Project project = new Project();
        //
        LearnerResult result = new LearnerResult();
        result.setProject(project);
        //
        LearnerResumeConfiguration configuration = new LearnerResumeConfiguration();
        configuration.setEqOracle(EXAMPLE_EQ_ORACLE);
        configuration.setMaxAmountOfStepsToLearn(-1);

        LearnerResultStep step = learnerResultDAO.createStep(result, configuration);

        assertThat(step.getStepNo(), is(equalTo(1L)));
        assertThat(step.getStepsToLearn(), is(equalTo(-1)));
    }

    @Test
    public void shouldCreateAStepFromAPreviousStep() throws NotFoundException {
        User user = new User();

        Project project = new Project();

        LearnerResult result = new LearnerResult();
        result.setProject(project);

        LearnerResumeConfiguration configuration = new LearnerResumeConfiguration();
        configuration.setEqOracle(EXAMPLE_EQ_ORACLE);
        configuration.setMaxAmountOfStepsToLearn(-1);
        LearnerResultStep step = learnerResultDAO.createStep(result, configuration);
        result.getSteps().add(step);

        LearnerResultStep newStep = learnerResultDAO.createStep(result);

        assertThat(newStep.getStepNo(), is(equalTo(2L)));
        assertThat(newStep.getStepsToLearn(), is(equalTo(-1)));
    }

    @Test
    public void shouldSaveAStep() throws NotFoundException {
        Statistics.DetailedStatistics detailedStatistics = new Statistics.DetailedStatistics(1L, 1L);
        //
        User user = new User();
        //
        Project project = new Project();
        //
        LearnerResult result = new LearnerResult();
        result.setProject(project);
        //
        LearnerResumeConfiguration configuration = new LearnerResumeConfiguration();
        configuration.setEqOracle(EXAMPLE_EQ_ORACLE);
        configuration.setMaxAmountOfStepsToLearn(-1);
        LearnerResultStep step = learnerResultDAO.createStep(result, configuration);
        //
        Statistics statistics = new Statistics();
        statistics.setEqsUsed(1L);
        statistics.setDuration(detailedStatistics);
        statistics.setMqsUsed(detailedStatistics);
        statistics.setSymbolsUsed(detailedStatistics);
        //
        CompactMealyMachineProxy hypothesis = mock(CompactMealyMachineProxy.class);
        step.setHypothesis(hypothesis);
        step.setStatistics(statistics);

        learnerResultDAO.saveStep(result, step);

        verify(learnerResultStepRepository, times(1)).save(step);
        assertThat(result.getHypothesis(), is(equalTo(hypothesis)));
        assertThat(result.getStatistics().getEqsUsed(), is(equalTo(1L)));
        assertThat(result.getStatistics().getDuration(), is(equalTo(detailedStatistics)));
        assertThat(result.getStatistics().getMqsUsed(), is(equalTo(detailedStatistics)));
        assertThat(result.getStatistics().getSymbolsUsed(), is(equalTo(detailedStatistics)));
    }

    @Test
    public void shouldDeleteMultipleResults() throws NotFoundException {
        User user = new User();
        //
        Long[] testNos = new Long[]{0L, 1L};
        //
        LearnerStatus status = new LearnerStatus();
        given(learner.getStatus(PROJECT_ID)).willReturn(status);
        //
        given(learnerResultRepository.deleteByProject_IdAndTestNoIn(PROJECT_ID, testNos)).willReturn(2L);

        learnerResultDAO.delete(learner, PROJECT_ID, testNos);

        verify(learnerResultRepository).deleteByProject_IdAndTestNoIn(PROJECT_ID, testNos);
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowAnExceptionIfTheTestResultToDeleteWasNotFound() throws NotFoundException {
        User user = new User();
        //
        Long[] testNos = new Long[]{0L, 1L};
        //
        LearnerStatus status = new LearnerStatus();
        given(learner.getStatus(PROJECT_ID)).willReturn(status);
        //
        given(learnerResultRepository.deleteByProject_IdAndTestNoIn(PROJECT_ID, testNos)).willReturn(1L);

        learnerResultDAO.delete(learner, PROJECT_ID, testNos);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowAnExceptionIfTheTestResultToDeleteIsActive() throws NotFoundException {
        User user = new User();
        //
        Project project = new Project();
        project.setId(PROJECT_ID);
        //
        LearnerResult result = new LearnerResult();
        result.setProject(project);
        result.setTestNo(0L);
        Long[] testNos = new Long[]{0L, 1L};
        //
        LearnerStatus status = new LearnerStatus(result, Learner.LearnerPhase.LEARNING, new ArrayList<>());
        given(learner.getStatus(PROJECT_ID)).willReturn(status);

        learnerResultDAO.delete(learner, PROJECT_ID, testNos); // should fail
    }

    private List<LearnerResult> createLearnerResultsList() throws NotFoundException {
        List<LearnerResult> results = new LinkedList<>();
        for (int i = 0; i < RESULTS_AMOUNT; i++) {
            LearnerResult r = new LearnerResult();
            results.add(r);
        }
        return results;
    }

}
