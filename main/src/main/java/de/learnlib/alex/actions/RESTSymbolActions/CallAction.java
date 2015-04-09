package de.learnlib.alex.actions.RESTSymbolActions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.learnlib.alex.core.entities.ExecuteResult;
import de.learnlib.alex.core.learner.connectors.WebServiceConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * RESTSymbolAction to make a request to the API.
 */
@Entity
@DiscriminatorValue("rest_call")
@JsonTypeName("rest_call")
public class CallAction extends RESTSymbolAction {

    /** to be serializable. */
    private static final long serialVersionUID = 7971257988991996022L;

    /** Use the logger for the server part. */
    private static final Logger LOGGER = LogManager.getLogger("server");

    /**
     * Enumeration to specify the HTTP method.
     */
    public enum Method {
        /** Refers to the GET method. */
        GET,

        /** Refers to the POST method. */
        POST,

        /** Refers to the PUT method. */
        PUT,

        /** Refers to the DELETE method. */
        DELETE
    }

    /** The method to use for the call. */
    @NotNull
    private Method method;

    /** The url to call. This is just the suffix which will be appended to the base url. */
    @NotBlank
    private String url;

    /** Optional data to sent with a POST or PUT request. */
    private String data;

    /**
     * Get the method to use for the next request.
     *
     * @return The selected method.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Select a method to use for the request.
     *
     * @param method
     *         The new method to use.
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Get the URL the request will go to.
     *
     * @return The URL which will be called.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the URL the request will go to.
     * In the URL all the variables and counters will be replace with their values.
     *
     * @return The URL which will be called.
     */
    @JsonIgnore
    public String getUrlWithVariableValues() {
        return insertVariableValues(url);
    }

    /**
     * Set the URL to send the request to.
     *
     * @param url
     *         The URL to call.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the optional data which will be send together with a POST or PUT request.
     *
     * @return The data to include in the next POST/ PUT request.
     */
    public String getData() {
        return data;
    }

    /**
     * Get the optional data which will be send together with a POST or PUT request.
     * All variables and counters will be replaced with their values.
     *
     * @return The data to include in the next POST/ PUT request.
     */
    @JsonIgnore
    public String getDataWithVariableValues() {
        return insertVariableValues(data);
    }

    /**
     * Set the optional data which will be send together with a POST or PUT request.
     *
     * @param data
     *         The data to include in the next POST/ PUT request.
     */
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public ExecuteResult execute(WebServiceConnector target) {
        try {
            doRequest(target);
            return getSuccessOutput();
        } catch (Exception e) {
            LOGGER.info("Could not call " + getUrlWithVariableValues(), e);
            return getFailedOutput();
        }
    }

    private void doRequest(WebServiceConnector target) {
        switch (method) {
            case GET:
                target.get(getUrlWithVariableValues());
                break;
            case POST:
                target.post(getUrlWithVariableValues(), getDataWithVariableValues());
                break;
            case PUT:
                target.put(getUrlWithVariableValues(), getDataWithVariableValues());
                break;
            case DELETE:
                target.delete(getUrlWithVariableValues());
                break;
            default:
                LOGGER.info("tried to make a call to a REST API with an unknown method '" + method.name() + "'.");
        }
    }

}
