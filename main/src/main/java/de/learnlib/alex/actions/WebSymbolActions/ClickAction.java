package de.learnlib.alex.actions.WebSymbolActions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.learnlib.alex.core.entities.ExecuteResult;
import de.learnlib.alex.core.learner.connectors.WebSiteConnector;
import org.hibernate.validator.constraints.NotBlank;
import org.openqa.selenium.NoSuchElementException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Action to click on a specific element.
 */
@Entity
@DiscriminatorValue("web_click")
@JsonTypeName("web_click")
public class ClickAction extends WebSymbolAction {

    /** to be serializable. */
    private static final long serialVersionUID = -9158530821188611940L;

    /** The information to identify the element. */
    @NotBlank
    @Column(columnDefinition = "CLOB")
    private String node;

    /**
     * Get the information to identify the element.
     * 
     * @return The element identifier.
     */
    public String getNode() {
        return node;
    }

    /**
     * Get the node to look for.
     * All variables and counters will be replaced with their values.
     *
     * @return The node to look for.
     */
    @JsonIgnore
    public String getNodeWithVariableValues() {
        return insertVariableValues(node);
    }

    /**
     * Set the information to identify the element.
     * 
     * @param node
     *            The new element identifier.
     */
    public void setNode(String node) {
        this.node = node;
    }

    @Override
    public ExecuteResult execute(WebSiteConnector connector) {
        try {
            connector.getElement(getNodeWithVariableValues()).click();
            return getSuccessOutput();
        } catch (NoSuchElementException e) {
            return getFailedOutput();
        }
    }

}
