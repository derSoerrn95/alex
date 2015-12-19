package de.learnlib.alex.core.entities.learnlibproxies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.learnlib.oracles.DefaultQuery;
import net.automatalib.words.Word;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Proxy around a DefaultQuery.
 * The Proxy is needed to make it easier to (de-)serialize the Transition into/ from JSON.
 *
 * @see de.learnlib.oracles.DefaultQuery
 */
@Embeddable
public class DefaultQueryProxy implements Serializable {

    /** to be serializable. */
    private static final long serialVersionUID = 4759682392322006213L;

    /** Create one static ObjectMapper to (de-)serialize the Proxy for the DB. */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** The Prefix of the query as Strings. */
    private List<String> prefix;

    /** The Suffix of the query as Strings. */
    private List<String> suffix;

    /** The Output of the query as Strings. */
    private List<String> output;

    public DefaultQueryProxy() {
        this.prefix = new LinkedList<>();
        this.suffix = new LinkedList<>();
        this.output = new LinkedList<>();
    }

    /**
     * Create a new Proxy based on the provided DefaultQuery.
     *
     * @param query
     *         The base Query of the new proxy. Can be null.
     * @return The new Proxy around the DefaultQuery..
     */
    public static DefaultQueryProxy createFrom(DefaultQuery<String, Word<String>> query) {
        DefaultQueryProxy newProxy = new DefaultQueryProxy();
        if (query != null) {
            newProxy.prefix = query.getPrefix().asList();
            newProxy.suffix = query.getSuffix().asList();
            if (query.getOutput() != null) {
                newProxy.output = query.getOutput().asList();
            }
        }

        return newProxy;
    }

    /**
     * @return The Prefix of the Proxy.
     */
    @Transient
    @JsonProperty
    public List<String> getPrefix() {
        return prefix;
    }

    /**
     * @param prefix The new prefix of the Proxy.
     */
    public void setPrefix(List<String> prefix) {
        this.prefix = prefix;
    }

    /**
     * @return The Suffix of the Proxy.
     */
    @Transient
    @JsonProperty
    public List<String> getSuffix() {
        return suffix;
    }

    /**
     * @param suffix The new suffix of the Proxy.
     */
    public void setSuffix(List<String> suffix) {
        this.suffix = suffix;
    }

    /**
     * @return The Output of the Proxy.
     */
    @Transient
    @JsonProperty
    public List<String> getOutput() {
        return output;
    }

    /**
     * @param output The new output of the Proxy.
     */
    public void setOutput(List<String> output) {
        this.output = output;
    }

    /**
     * Getter method to interact with the DB, because the Java standard serialization doesn't work.
     *
     * @return The Proxy as JSON string.
     */
    @Column(name = "counterExample", columnDefinition = "CLOB")
    @JsonIgnore
    public String getProxyDB() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Setter method to interact with the DB, because the Java standard deserialization doesn't work.
     *
     * @param proxyAsString
     *         The Proxy as JSON string.
     */
    @JsonIgnore
    public void setProxyDB(String proxyAsString) {
        try {
            DefaultQueryProxy that = OBJECT_MAPPER.readValue(proxyAsString, DefaultQueryProxy.class);
            this.prefix = that.prefix;
            this.suffix = that.suffix;
            this.output = that.output;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a DefaultQuery based on this proxy.
     *
     * @return The new DefaultQuery.
     */
    public DefaultQuery<String, Word<String>> createDefaultProxy() {
        Word<String> prefixAsWord = Word.fromList(prefix);
        Word<String> suffixAsWord = Word.fromList(suffix);
        Word<String> outputAsWord = Word.fromList(output);
        return new DefaultQuery<>(prefixAsWord, suffixAsWord, outputAsWord);
    }

    //CHECKSTYLE.OFF: AvoidInlineConditionals|LineLength|MagicNumber|NeedBraces - auto generated by IntelliJ IDEA
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultQueryProxy that = (DefaultQueryProxy) o;
        return Objects.equals(prefix, that.prefix) &&
                Objects.equals(suffix, that.suffix) &&
                Objects.equals(output, that.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, suffix, output);
    }
    // CHECKSTYLE.OFF: AvoidInlineConditionals|LineLength|MagicNumber|NeedBraces

}