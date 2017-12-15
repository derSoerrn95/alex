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

package de.learnlib.alex.testsuites.services.reporters;

import de.learnlib.alex.testsuites.entities.TestCaseResult;
import de.learnlib.alex.testsuites.entities.TestResult;
import de.learnlib.alex.testsuites.entities.TestSuiteResult;
import de.learnlib.alex.testsuites.entities.TestReportConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

/**
 * Creates a JUnit report of a test result.
 *
 * @see <a href="https://www.ibm.com/support/knowledgecenter/en/SSQ2R2_9.5.0/com.ibm.rsar.analysis.codereview.cobol.doc/topics/cac_useresults_junit.html">JUnit XML format</a>
 */
public class JUnitTestResultReporter extends TestResultReporter<String> {

    /**
     * Creates a report
     *
     * @param reportConfig The config to create a report from.
     * @return The serialized and formatted xml report as string.
     */
    @Override
    public String createReport(final TestReportConfig reportConfig) {
        try {
            final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            final Document doc = docBuilder.newDocument();

            final Element rootElement = doc.createElement("testsuites");
            doc.appendChild(rootElement);

            final TestSuiteResult overallResult = new TestSuiteResult(reportConfig.getParent(), 0L, 0L);
            overallResult.setResults(reportConfig.getResults());

            reportConfig.getResults().forEach((id, result) -> {
                if (result instanceof TestCaseResult) {
                    overallResult.add((TestCaseResult) result);
                } else {
                    overallResult.add((TestSuiteResult) result);
                }
            });

            final Queue<TestSuiteResult> queue = new ArrayDeque<>();
            queue.offer(overallResult);

            while (!queue.isEmpty()) {
                final Optional<Element> testSuiteEl = createTestSuiteTag(doc, queue.poll(), queue);
                testSuiteEl.ifPresent(rootElement::appendChild);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter stringWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(stringWriter);
            transformer.transform(source, streamResult);

            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Optional<Element> createTestSuiteTag(final Document doc,
                                                 final TestSuiteResult suiteResult,
                                                 final Queue<TestSuiteResult> queue) {

        int numTestCasesFailed = 0;
        long time = 0L;

        final List<Element> testCaseEls = new ArrayList<>();

        for (Map.Entry<Long, TestResult> result : suiteResult.getResults().entrySet()) {
            if (result.getValue() instanceof TestCaseResult) {
                final TestCaseResult caseResult = (TestCaseResult) result.getValue();

                numTestCasesFailed += caseResult.isPassed() ? 0 : 1;
                time += caseResult.getTime();

                testCaseEls.add(createTestCaseTag(doc, caseResult));
            } else {
                queue.offer((TestSuiteResult) result.getValue());
            }
        }

        if (!testCaseEls.isEmpty()) {
            final Element testSuiteEl = doc.createElement("testsuite");
            testSuiteEl.setAttribute("id", String.valueOf(suiteResult.getTest().getId()));
            testSuiteEl.setAttribute("name", suiteResult.getTest().getName());
            testSuiteEl.setAttribute("tests", String.valueOf(testCaseEls.size()));
            testSuiteEl.setAttribute("failures", String.valueOf(numTestCasesFailed));
            testSuiteEl.setAttribute("time", String.valueOf(time));

            testCaseEls.forEach(testSuiteEl::appendChild);

            return Optional.of(testSuiteEl);
        } else {
            return Optional.empty();
        }
    }

    private Element createTestCaseTag(final Document doc, final TestCaseResult result) {
        final Element testCaseEl = doc.createElement("testcase");
        testCaseEl.setAttribute("name", result.getTest().getName());
        testCaseEl.setAttribute("id", String.valueOf(result.getTest().getId()));
        testCaseEl.setAttribute("time", String.valueOf(result.getTime()));

        if (!result.isPassed()) {
            final Element failureEl = doc.createElement("failure");
            failureEl.setAttribute("message", result.getFailureMessage());
            testCaseEl.appendChild(failureEl);
        }

        return testCaseEl;
    }

}
