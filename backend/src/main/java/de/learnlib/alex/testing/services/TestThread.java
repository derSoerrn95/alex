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

package de.learnlib.alex.testing.services;

import de.learnlib.alex.auth.entities.User;
import de.learnlib.alex.common.exceptions.NotFoundException;
import de.learnlib.alex.data.entities.Project;
import de.learnlib.alex.testing.dao.TestDAO;
import de.learnlib.alex.testing.dao.TestReportDAO;
import de.learnlib.alex.testing.entities.Test;
import de.learnlib.alex.testing.entities.TestExecutionConfig;
import de.learnlib.alex.testing.entities.TestReport;
import de.learnlib.alex.testing.entities.TestResult;
import de.learnlib.alex.testing.events.TestEvent;
import de.learnlib.alex.testing.events.TestExecutionStartedEventData;
import de.learnlib.alex.webhooks.services.WebhookService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The thread that executes tests.
 * There should ever only be one test per project.
 */
public class TestThread extends Thread {

    /** Listener for when the test process finished. */
    public interface FinishedListener {

        /** What to do when the test process finished. */
        void handleFinished();
    }

    /** The user that executes the tests. */
    private final User user;

    /** The project that is used. */
    private final Project project;

    /** The configuration that is used for testing. */
    private final TestExecutionConfig config;

    /** The {@link WebhookService} to use. */
    private final WebhookService webhookService;

    /** The {@link TestDAO} to use. */
    private final TestDAO testDAO;

    /** The {@link TestReportDAO} to use. */
    private final TestReportDAO testReportDAO;

    /** The finished listener. */
    private final FinishedListener finishedListener;

    /** The test service. */
    private final TestService testService;

    /** The map where intermediate results are stored. */
    private final Map<Long, TestResult> results;

    /**
     * Constructor.
     *
     * @param user             {@link #user}.
     * @param project          {@link #project}.
     * @param config           {@link #config}.
     * @param webhookService   {@link #webhookService}.
     * @param testDAO          {@link #testDAO}.
     * @param testReportDAO    {@link #testReportDAO}.
     * @param testService      {@link #testService}.
     * @param finishedListener {@link #finishedListener}.
     */
    public TestThread(User user, Project project, TestExecutionConfig config,
                      WebhookService webhookService, TestDAO testDAO, TestReportDAO testReportDAO,
                      TestService testService, FinishedListener finishedListener) {
        this.user = user;
        this.project = project;
        this.config = config;
        this.webhookService = webhookService;
        this.testDAO = testDAO;
        this.testReportDAO = testReportDAO;
        this.testService = testService;
        this.finishedListener = finishedListener;
        this.results = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            final List<Test> tests = testDAO.get(user, project.getId(), config.getTestIds());

            // do not fire the event if the test is only called for testing purposes.
            if (config.isCreateReport()) {
                final TestExecutionStartedEventData data = new TestExecutionStartedEventData(project.getId(), config);
                webhookService.fireEvent(user, new TestEvent.ExecutionStarted(data));
            }

            testService.executeTests(user, tests, config, results);
            final TestReport report = getReport();

            if (config.isCreateReport()) {
                testReportDAO.create(user, project.getId(), report);
                webhookService.fireEvent(user, new TestEvent.ExecutionFinished(report));
            }

        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            finishedListener.handleFinished();
        }
    }

    /**
     * Get the report for the intermediate results.
     *
     * @return The report.
     */
    public TestReport getReport() {
        final TestReport testReport = new TestReport();
        testReport.setTestResults(new ArrayList<>(results.values()));
        return testReport;
    }
}
