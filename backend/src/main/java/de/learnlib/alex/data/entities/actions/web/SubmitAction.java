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

package de.learnlib.alex.data.entities.actions.web;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.learnlib.alex.data.entities.ExecuteResult;
import de.learnlib.alex.data.entities.WebElementLocator;
import de.learnlib.alex.learning.services.connectors.WebSiteConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.openqa.selenium.NoSuchElementException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Action to submit a specific element.
 */
@Entity
@DiscriminatorValue("web_submit")
@JsonTypeName("web_submit")
public class SubmitAction extends WebSymbolAction {

    private static final long serialVersionUID = 3054489976413991003L;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Marker LEARNER_MARKER = MarkerManager.getMarker("LEARNER");

    /** The information to identify the element. */
    @NotNull
    @Embedded
    private WebElementLocator node;

    /**
     * Get the node to look for.
     *
     * @return The node to look for.
     */
    public WebElementLocator getNode() {
        return node;
    }

    /**
     * Set the node to check for.
     *
     * @param node
     *         The new node to check for.
     */
    public void setNode(WebElementLocator node) {
        this.node = node;
    }

    @Override
    public ExecuteResult execute(WebSiteConnector connector) {
        final WebElementLocator nodeWithVariables =
                new WebElementLocator(insertVariableValues(node.getSelector()), node.getType());

        try {
            connector.getElement(nodeWithVariables).submit();

            LOGGER.info(LEARNER_MARKER, "Submitted '{}' (ignoreFailure: {}, negated: {}).",
                    nodeWithVariables, ignoreFailure, negated);
            return getSuccessOutput();
        } catch (NoSuchElementException e) {
            LOGGER.info(LEARNER_MARKER, "Could not submit '{}' (ignoreFailure: {}, negated: {}).",
                    nodeWithVariables, ignoreFailure, negated, e);
            return getFailedOutput();
        }
    }

}
