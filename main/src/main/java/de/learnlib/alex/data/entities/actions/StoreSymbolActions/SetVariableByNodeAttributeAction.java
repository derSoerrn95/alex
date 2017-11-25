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

package de.learnlib.alex.data.entities.actions.StoreSymbolActions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.learnlib.alex.data.entities.ExecuteResult;
import de.learnlib.alex.data.entities.SymbolAction;
import de.learnlib.alex.data.entities.WebElementLocator;
import de.learnlib.alex.learning.services.connectors.ConnectorManager;
import de.learnlib.alex.learning.services.connectors.VariableStoreConnector;
import de.learnlib.alex.learning.services.connectors.WebSiteConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.hibernate.validator.constraints.NotBlank;
import org.openqa.selenium.NoSuchElementException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Action to set a variable to the value of an attribute of an HTML node.
 */
@Entity
@DiscriminatorValue("setVariableByNodeAttribute")
@JsonTypeName("setVariableByNodeAttribute")
public class SetVariableByNodeAttributeAction extends SymbolAction {

    private static final long serialVersionUID = 8998187003156355834L;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Marker LEARNER_MARKER = MarkerManager.getMarker("LEARNER");

    /** The name of the variable. */
    @NotBlank
    protected String name;

    /** The node to look for. */
    @NotNull
    @Embedded
    protected WebElementLocator node;

    /** The attribute name of the node to look for. */
    @NotNull
    protected String attribute;

    /**
     * @return The name of the variable to set.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name of the new variable to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The node to get the attribute from.
     */
    public WebElementLocator getNode() {
        return node;
    }

    /**
     * @param node The new identifier for the node to get the attribute from.
     */
    public void setNode(WebElementLocator node) {
        this.node = node;
    }

    /**
     * @return The identifier of the attribute to get the value from.
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @param attribute The identifier of the new attribute to get the value from.
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    protected ExecuteResult execute(ConnectorManager connector) {
        VariableStoreConnector storeConnector = connector.getConnector(VariableStoreConnector.class);
        WebSiteConnector webSiteConnector = connector.getConnector(WebSiteConnector.class);

        try {
            String value = webSiteConnector.getElement(node).getAttribute(attribute);
            storeConnector.set(name, value);

            return getSuccessOutput();
        } catch (NoSuchElementException e) {
            LOGGER.info(LEARNER_MARKER, "Could not set the variable '{}' to the value of the attribute "
                                            + "of the HTML node '{}'.",
                        name, node);
            return getFailedOutput();
        }
    }

}
