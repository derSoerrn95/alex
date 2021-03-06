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

package de.learnlib.alex.data.entities.actions.misc;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.learnlib.alex.data.entities.ExecuteResult;
import de.learnlib.alex.data.entities.SymbolAction;
import de.learnlib.alex.learning.services.connectors.ConnectorManager;
import de.learnlib.alex.learning.services.connectors.VariableStoreConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Action to assert the equality of the content of a variable with a given string.
 */
@Entity
@DiscriminatorValue("assertVariable")
@JsonTypeName("assertVariable")
public class AssertVariableAction extends SymbolAction {

    private static final long serialVersionUID = 6363724455992504221L;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Marker LEARNER_MARKER  = MarkerManager.getMarker("LEARNER");

    /**
     * The name of the variable to assert.
     */
    @NotBlank
    private String name;

    /**
     * The value to assert the variable content with.
     */
    @NotBlank
    private String value;

    /**
     * Whether the value of the variable is matched against a regular expression.
     */
    @NotNull
    private boolean regexp;

    /** Constructor. */
    public AssertVariableAction() {
        this.regexp = false;
    }

    /**
     * @return The name of the variable to assert.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The new name of the variable to assert.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The value to check the variable against.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The new vlue to check the variable against.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return Treat the value as regexp?
     */
    public boolean isRegexp() {
        return regexp;
    }

    /**
     * @param regexp True, if the value is a regular expression; false otherwise.
     */
    public void setRegexp(boolean regexp) {
        this.regexp = regexp;
    }

    @Override
    protected ExecuteResult execute(ConnectorManager connector) {
        final VariableStoreConnector variableStore = connector.getConnector(VariableStoreConnector.class);
        final String variableValue = variableStore.get(name);
        final String valueWithVariables = insertVariableValues(value);

        final boolean result;
        if (regexp) {
            result = variableValue.matches(valueWithVariables);
        } else {
            result = variableValue.equals(valueWithVariables);
        }

        LOGGER.info(LEARNER_MARKER, "Asserting variable '{}' with value '{}' against '{}' => {} "
                                        + "(regex: {}, ignoreFailure: {}, negated: {}).",
                    name, variableValue, valueWithVariables, result, regexp, ignoreFailure, negated);

        if (result) {
            return getSuccessOutput();
        } else {
            return getFailedOutput();
        }
    }

}
