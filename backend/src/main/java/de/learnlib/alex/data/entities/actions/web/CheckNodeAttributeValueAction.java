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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.learnlib.alex.data.entities.ExecuteResult;
import de.learnlib.alex.data.entities.WebElementLocator;
import de.learnlib.alex.learning.services.connectors.WebSiteConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.hibernate.validator.constraints.NotBlank;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Action to check the value of a nodes attribute.
 */
@Entity
@DiscriminatorValue("web_checkNodeAttributeValue")
@JsonTypeName("web_checkNodeAttributeValue")
public class CheckNodeAttributeValueAction extends WebSymbolAction {

    private static final long serialVersionUID = -1195203568320940744L;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Marker LEARNER_MARKER = MarkerManager.getMarker("LEARNER");

    /**
     * Enumeration to specify the check method.
     */
    public enum CheckMethod {

        /**
         * If the attribute contains the value.
         */
        CONTAINS,

        /**
         * If the attribute exists.
         */
        EXISTS,

        /**
         * If the attribute equals the value.
         */
        IS,

        /**
         * If the attribute matches the value.
         */
        MATCHES;

        /**
         * Parser function to handle the enum names case insensitive.
         *
         * @param name The enum name.
         * @return The corresponding CheckMethod.
         * @throws IllegalArgumentException If the name could not be parsed.
         */
        @JsonCreator
        public static CheckMethod fromString(String name) throws IllegalArgumentException {
            return CheckMethod.valueOf(name.toUpperCase());
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * The selector of the element.
     */
    @NotNull
    @Embedded
    private WebElementLocator node;

    /**
     * The attribute name of the element to check.
     */
    @NotBlank
    private String attribute;

    /**
     * The attribute value to check for.
     */
    @NotNull
    private String value;

    /**
     * The method that is used to check the attribute value.
     */
    @NotNull
    private CheckMethod checkMethod;

    @Override
    protected ExecuteResult execute(WebSiteConnector connector) {
        final WebElementLocator nodeWithVariables =
                new WebElementLocator(insertVariableValues(node.getSelector()), node.getType());

        final String valueWithVariables = insertVariableValues(value);

        try {
            final WebElement element = connector.getElement(nodeWithVariables);
            final String attributeValue = element.getAttribute(attribute);

            if (attributeValue == null) {
                LOGGER.info(LEARNER_MARKER, "Attribute '{}' not found on element '{}'",
                        attribute, nodeWithVariables);
                return getFailedOutput();
            }

            boolean isValid = false;
            switch (checkMethod) {
                case IS:
                    isValid = attributeValue.equals(valueWithVariables);
                    break;
                case EXISTS:
                    // since the case that the attribute does not exist is checked above, we can set this to true.
                    isValid = true;
                    break;
                case CONTAINS:
                    isValid = attributeValue.contains(valueWithVariables);
                    break;
                case MATCHES:
                    isValid = attributeValue.matches(valueWithVariables);
                    break;
                default:
                    break;
            }

            if (isValid) {
                LOGGER.info(LEARNER_MARKER, "The value of the attribute '{}' of the node '{}'"
                                + " '{}' the searched value '{}' (ignoreFailure: {}, negated: {}).",
                        attribute, nodeWithVariables, checkMethod, valueWithVariables, ignoreFailure, negated);
                return getSuccessOutput();
            } else {
                LOGGER.info(LEARNER_MARKER, "The value of the attribute '{}' of the node '{}'"
                                + " does not '{}' the searched value '{}' (ignoreFailure: {}, negated: {}).",
                        attribute, nodeWithVariables, checkMethod, valueWithVariables, ignoreFailure, negated);
                return getFailedOutput();
            }
        } catch (NoSuchElementException e) {
            LOGGER.info(LEARNER_MARKER, "Could not find the node '{}' (ignoreFailure: {}, negated: {}).",
                    nodeWithVariables, ignoreFailure, negated, e);
            return getFailedOutput();
        }
    }

    /**
     * @return The selector of the element.
     */
    public WebElementLocator getNode() {
        return node;
    }

    /**
     * @param node The selector of the element.
     */
    public void setNode(WebElementLocator node) {
        this.node = node;
    }

    /**
     * @return The attribute of the element.
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @param attribute The attribute of the element.
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    /**
     * @return The value that is checked for.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value that is checked for.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return The method that is used to check the attribute value.
     */
    public CheckMethod getCheckMethod() {
        return checkMethod;
    }

    /**
     * @param checkMethod The method that is used to check the attribute value.
     */
    public void setCheckMethod(CheckMethod checkMethod) {
        this.checkMethod = checkMethod;
    }
}
