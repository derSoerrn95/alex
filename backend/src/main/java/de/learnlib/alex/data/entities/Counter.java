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

package de.learnlib.alex.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.UUID;

/**
 * A simple counter class.
 */
@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "name"})
)
@JsonPropertyOrder(alphabetic = true)
public class Counter implements Serializable {

    private static final long serialVersionUID = 5495935413098569457L;

    /** The ID of the counter in the DB. */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonIgnore
    private UUID uuid;

    /** The project the counter belongs to. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JsonIgnore
    private Project project;

    /** The name of the counter. */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]+[a-zA-Z0-9\\-_]*")
    private String name;

    /** The value of the counter. */
    @NotNull
    private Integer value;

    /**
     * Create a new counter with a default value of 0.
     */
    public Counter() {
        value = 0;
    }

    /**
     * @return The internal ID used by the database.
     */
    public UUID getUUID() {
        return uuid;
    }

    /** @param uuid The counter id. */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the project in which the counter is used in..
     * @return The related project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set a new project for the counter.
     *
     * @param project
     *         The new related project.
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Get the ID of {@link Project} the Symbol belongs to.
     *
     * @return The parent Project.
     * @requiredField
     */
    @JsonProperty("project")
    public Long getProjectId() {
        if (project == null) {
            return 0L;
        } else {
            return this.project.getId();
        }
    }

    /**
     * Set the ID of the {@link Project} the Symbol belongs to.
     *
     * @param projectId
     *            The new parent Project.
     */
    @JsonProperty("project")
    public void setProjectId(Long projectId) {
        this.project = new Project(projectId);
    }

    /**
     * Get the name of the counter.
     *
     * @return The counter name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set a new name for the counter.
     *
     * @param name
     *         The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the value of the counter.
     *
     * @return The current value.
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Set a new value for the counter.
     *
     * @param value
     *         The new value.
     */
    public void setValue(Integer value) {
        this.value = value < 0 ? 0 : value;
    }

    @Override
    public Counter clone() {
        Counter counter = new Counter();
        counter.setUUID(uuid);
        counter.setName(name);
        counter.setProject(project);
        counter.setValue(value);
        return counter;
    }

    //CHECKSTYLE.OFF: AvoidInlineConditionals|MagicNumber|NeedBraces - auto generated by IntelliJ IDEA
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Counter)) return false;

        Counter counter = (Counter) o;

        if (project != null ? !project.equals(counter.project) : counter.project != null) return false;
        return !(name != null ? !name.equals(counter.name) : counter.name != null);
    }

    @Override
    public int hashCode() {
        int result = project != null ? project.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.OFF: AvoidInlineConditionals|MagicNumber|NeedBraces
}
