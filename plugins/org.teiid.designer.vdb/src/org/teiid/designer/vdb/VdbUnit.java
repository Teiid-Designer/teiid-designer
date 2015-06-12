/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.vdb.Vdb.Event;


/**
 * Base vdb object class
 * 
 * @author blafond
 *
 */
public abstract class VdbUnit extends AbstractVdbObject {

    private Vdb vdb;

    /**
     * Default Constructor used for objects not yet being added to a vdb
     */
    public VdbUnit() {
    }

    /**
     * Constructor for objects already having a reference to a vdb
     *
     * @param vdb
     */
    public VdbUnit(Vdb vdb) {
        setVdb(vdb);
    }

	/**
     * @return the vdb
     */
    public Vdb getVdb() {
        return this.vdb;
    }

    /**
     * @param vdb
     */
    public void setVdb(Vdb vdb) {
        this.vdb = vdb;
    }

    /**
     * @param vdbUnit
     * @param event
     * @param oldValue
     * @param newValue
     */
    protected void setModified(VdbUnit vdbUnit, String event, Object oldValue, Object newValue) {
        if (vdb == null)
            return;

        vdb.setModified(vdbUnit, event, oldValue, newValue);
    }

    @Override
    public void setDescription(String newDescription) {
        String oldDescription = getDescription();
        super.setDescription(newDescription);
        if (isChanged() && vdb != null)
            setModified(this, Event.ENTRY_DESCRIPTION, oldDescription, newDescription);
    }

    @Override
    public final void setProperty(String key, String value) {
        String oldValue = getProperties().getProperty(key);
        if( oldValue != null ) {
            String valueString = getProperties().getProperty(key);
            if( StringUtilities.areDifferent(value, valueString)) {
                super.setProperty(key,  value);
                setModified(this, Event.GENERAL_PROPERTY, oldValue, value);
            }
        } else {
            super.setProperty(key,  value);
            setModified(this, Event.GENERAL_PROPERTY, oldValue, value);
        }
    }

    /**
     * @param key
     */
    public String removeProperty(String key) {
        String property = super.removeProperty(key);
        setModified(this, Event.GENERAL_PROPERTY, property, null);
        return property;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.vdb == null) ? 0 : this.vdb.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        VdbUnit other = (VdbUnit)obj;
        if (this.vdb == null) {
            if (other.vdb != null)
                return false;
        } else if (!this.vdb.equals(other.vdb))
            return false;
        return true;
    }
}
