package de.learnlib.weblearner.actions.RESTSymbolActions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.learnlib.weblearner.core.entities.ExecuteResult;
import de.learnlib.weblearner.core.entities.SymbolAction;
import de.learnlib.weblearner.core.learner.connectors.ConnectorManager;
import de.learnlib.weblearner.core.learner.connectors.WebServiceConnector;
import de.learnlib.weblearner.utils.SearchHelper;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Base class for all the REST specific actions.
 */
@Entity
@DiscriminatorValue("rest")
@JsonTypeName("rest")
public abstract class RESTSymbolAction extends SymbolAction {

    /** to be serializable. */
    private static final long serialVersionUID = -897337751104947135L;

    @Transient
    private ConnectorManager connectorManager;

    @Override
    public ExecuteResult execute(ConnectorManager connector) {
        this.connectorManager = connector;
        return execute(connector.getConnector(WebServiceConnector.class));
    }

    protected abstract ExecuteResult execute(WebServiceConnector connector);

    protected String insertVariableValues(String text) {
        return SearchHelper.insertVariableValues(connectorManager, project.getId(), text);
    }
}
