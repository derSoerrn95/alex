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

package de.learnlib.alex.testsuites.entities;

import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.persistence.DiscriminatorValue;
import java.util.HashMap;
import java.util.Map;

/**
 * The result of the execution of a test suite.
 */
@DiscriminatorValue("suite")
@JsonTypeName("suite")
public class TestSuiteResult extends TestResult {

    /** How many test cases passed. */
    private long testCasesPassed;

    /** How many test cases failed. */
    private long testCasesFailed;

    /** The results of the tests of this suite. */
    private Map<Long, TestResult> results;

    /**
     * Constructor.
     */
    public TestSuiteResult() {
    }

    /**
     * Constructor.
     *
     * @param testSuite       The test suite that has been executed.
     * @param testCasesPassed The number of test cases that passed.
     * @param testCasesFailed The number of test cases that failed.
     */
    public TestSuiteResult(TestSuite testSuite, long testCasesPassed, long testCasesFailed) {
        super(testSuite);
        this.results = new HashMap<>();
        this.testCasesPassed = testCasesPassed;
        this.testCasesFailed = testCasesFailed;
    }

    /**
     * Adds result from children test cases.
     *
     * @param result The test case in this suite.
     */
    public void add(TestCaseResult result) {
        testCasesPassed += result.isPassed() ? 1 : 0;
        testCasesFailed += !result.isPassed() ? 1 : 0;
        time += result.getTime();
    }

    /**
     * Adds result from children test suites.
     *
     * @param result The test suite in this suite.
     */
    public void add(TestSuiteResult result) {
        testCasesPassed += result.getTestCasesPassed();
        testCasesFailed += result.getTestCasesFailed();
        time += result.getTime();
    }

    public long getTestCasesRun() {
        return testCasesPassed + testCasesFailed;
    }

    public void setTestCasesRun(long num) {
    }

    public long getTestCasesPassed() {
        return testCasesPassed;
    }

    public void setTestCasesPassed(long testCasesPassed) {
        this.testCasesPassed = testCasesPassed;
    }

    public void addTestCasesPassed(long amount) {
        this.testCasesPassed += amount;
    }

    public long getTestCasesFailed() {
        return testCasesFailed;
    }

    public void setTestCasesFailed(long testCasesFailed) {
        this.testCasesFailed = testCasesFailed;
    }

    public void addTestCasesFailed(long amount) {
        this.testCasesFailed += amount;
    }

    public Map<Long, TestResult> getResults() {
        return results;
    }

    public void setResults(Map<Long, TestResult> results) {
        this.results = results;
    }

    public boolean isPassed() {
        return testCasesFailed == 0;
    }

    public void setPassed(boolean passed) {
    }
}
