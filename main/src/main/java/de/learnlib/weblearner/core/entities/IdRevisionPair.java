package de.learnlib.weblearner.core.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Class to put an ID and a Revision together.
 */
@Embeddable
public class IdRevisionPair implements Serializable {

    /** to be serializable. */
    private static final long serialVersionUID = -6906978252141016538L;

    /** The ID of a {@link Symbol}. */
    protected long id;

    /** The revision of a {@link Symbol}. */
    protected long revision;

    /**
     * Default constructor.
     */
    public IdRevisionPair() {
        // Nothing to do
    }

    /**
     * Advanced constructor which sets all the fields.
     *
     * @param id
     *         The ID.
     * @param revision
     *         The revision.
     */
    public IdRevisionPair(long id, long revision) {
        this.id = id;
        this.revision = revision;
    }

    /**
     * Advanced constructor which sets all the fields according to a Symbol.
     *
     * @param symbol
     *         The symbol to copy the ID and Revision from..
     */
    public IdRevisionPair(Symbol symbol) {
        this.id = symbol.getId();
        this.revision = symbol.getRevision();
    }

    /**
     * Get the ID.
     * 
     * @return The ID
     */
    public long getId() {
        return id;
    }

    /**
     * Set a new ID.
     * 
     * @param id
     *            The new ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the revision.
     * 
     * @return The revision.
     */
    public long getRevision() {
        return revision;
    }

    /**
     * Set a new revision.
     * 
     * @param revision
     *            The new revision.
     */
    public void setRevision(long revision) {
        this.revision = revision;
    }

    //CHECKSTYLE.OFF: MagicNumber - auto generated by Eclipse

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (revision ^ (revision >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IdRevisionPair other = (IdRevisionPair) obj;
        if (id != other.id) {
            return false;
        }
        if (revision != other.revision) {
            return false;
        }
        return true;
    }

    //CHECKSTYLE.ON: MagicNumber

}
