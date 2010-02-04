/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.runtime.IProgressMonitor;
import com.metamatrix.core.modeler.util.ArgCheck;

/**
 * FakeOpenable
 */
public class FakeOpenable implements Openable {

    private final String key;
    private boolean closed;
    private boolean hasUnsavedChanges;

    /**
     * Construct an instance of FakeOpenable.
     */
    public FakeOpenable( final String key ) {
        super();
        ArgCheck.isNotNull(key);
        ArgCheck.isNotZeroLength(key);
        this.key = key;
        this.closed = true;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#close()
     */
    public void close() {
        this.closed = true;
        this.hasUnsavedChanges = false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#hasUnsavedChanges()
     */
    public boolean hasUnsavedChanges() {
        return this.hasUnsavedChanges;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#isOpen()
     */
    public boolean isOpen() {
        return !this.closed;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#open(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void open( IProgressMonitor progress ) {
        this.closed = false;
        this.hasUnsavedChanges = false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
     */
    public void save( IProgressMonitor progress,
                      boolean force ) {
        this.hasUnsavedChanges = false;
    }

    public void setChanged() {
        this.hasUnsavedChanges = true;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        // Check if instances are identical ...
        if (this == obj) {
            return true;
        }

        // Check if object can be compared to this one
        // (this includes checking for null ) ...
        // if ( this.getClass().isInstance(obj) ) {
        if (obj instanceof FakeOpenable) {
            FakeOpenable that = (FakeOpenable)obj;
            if (that.key == null && this.key == null) {
                return true;
            }
            if (this.key != null) {
                return this.key.equals(that.key);
            }
            return that.key.equals(this.key);
        }

        // Otherwise not comparable ...
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString();
    }

}
