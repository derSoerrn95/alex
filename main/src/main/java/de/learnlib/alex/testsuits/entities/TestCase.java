package de.learnlib.alex.testsuits.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.learnlib.alex.data.entities.Project;
import de.learnlib.alex.data.entities.Symbol;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Representation of a Test Case.
 */
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"projectId", "name"},
                        name = "Unique Test Case Name per Project"
                )
        }
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCase implements Serializable {

    public class TestCaseSymbolRepresentation {
        private Long id;
        private String name;

        public TestCaseSymbolRepresentation(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /** The ID of the Test Case in the DB. */
    private UUID uuid;

    /** The id of the Test Case in the Project. */
    private Long id;

    /** The Project the Test Case belongs to. */
    private Project project;

    /** The ID of the Project to be used in the JSON. */
    private Long projectId;

    /** The name of the Test Case. */
    private String name;

    /** Link to the Symbols that are used during the Test Case. */
    private List<Symbol> symbols;
    private List<Long>   symbolsAsIds;

    /**
     * Default Constructor.
     */
    public TestCase() {
        super();

        this.symbols = new LinkedList<>();
    }

    /**
     * Get the ID of Test Case used in the DB.
     *
     * @return The internal ID.
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonIgnore
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Set the ID the Test Case has in the DB new.
     *
     * @param uuid The new internal ID.
     */
    @JsonIgnore
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the project the Test Case belongs to.
     *
     * @return The (parent) project.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "projectId")
    @JsonIgnore
    public Project getProject() {
        return project;
    }

    /**
     * Set the project the Test Case belongs to.
     *
     * @param project The new project.
     */
    @JsonIgnore
    public void setProject(Project project) {
        this.project = project;
        if (project == null) {
            this.projectId = null;
        } else {
            this.projectId = project.getId();
        }
    }

    /**
     * Get the {@link Project} the Test Case belongs to.
     *
     * @return The parent Project.
     * @requiredField
     */
    @Transient
    @JsonProperty("project")
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Set the {@link Project} the Test Case belongs to.
     *
     * @param projectId The new parent Project.
     */
    @JsonProperty("project")
    public void setProjectId(Long projectId) {
        this.project   = null;
        this.projectId = projectId;
    }

    /**
     * Get the ID of the Test Case (within the project).
     *
     * @return The ID.
     * @requiredField
     */
    @JsonProperty
    public Long getId() {
        return this.id;
    }

    /**
     * Set the ID of this Test Case (within the project).
     *
     * @param id The new ID.
     */
    @JsonProperty
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name of the Test Case.
     *
     * @return The name.
     */
    @NotBlank
    @JsonProperty
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Test Case.
     *
     * @param name The new name.
     */
    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the Symbols of the Test Case.
     *
     * @return The Symbols of this Test Case
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Symbol> getSymbols() {
        return symbols;
    }

    /**
     * Get the Symbol IDs of the Test Case.
     *
     * @return A list of Symbol ID to execute during the Test Case (in order).
     */
    @Transient
    @JsonIgnore
    public List<Long> getSymbolsAsIds() {
        if (symbolsAsIds == null || symbolsAsIds.isEmpty()) {
            symbolsAsIds = new ArrayList<>();
        }
        return symbolsAsIds;
    }

    @Transient
    @JsonProperty("symbols")
    public List<TestCaseSymbolRepresentation> getSymbolRepresentation() {
        if (symbols == null || symbols.isEmpty()) {
            return null;
        }
        return symbols.stream().map(s -> new TestCaseSymbolRepresentation(s.getId(), s.getName())).collect(Collectors.toList());
    }


    /**
     * Set a new List of Symbols of the Test Case.
     *
     * @param symbols The new list of Symbols.
     */
    @JsonIgnore
    public void setSymbols(List<Symbol> symbols) {
        if (symbols == null) {
            this.symbols = new LinkedList<>();
        } else {
            this.symbols = symbols;
            this.symbolsAsIds = symbols.stream().map(Symbol::getId).collect(Collectors.toList());
        }
    }

    /**
     * Set the Symbols of the Test Case.
     *
     * @param symbolsAsIds A list of Symbol ID to execute during the Test Case (in order).
     */
    @Transient
    @JsonProperty("symbols")
    public void setSymbolsAsIds(List<Long> symbolsAsIds) {
        this.symbolsAsIds = symbolsAsIds;
    }


    /**
     * Add one action to the end of the Action List.
     *
     * @param action The SymbolAction to add.
     */
    public void addSymbol(Symbol action) {
        if (action == null) {
            throw new IllegalArgumentException("Can not add Symbol 'null'");
        }

        symbols.add(action);
    }

}
