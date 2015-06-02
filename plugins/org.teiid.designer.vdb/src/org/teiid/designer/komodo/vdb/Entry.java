/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

/**
 * Represents a VDB entry.
 */
public interface Entry {

    /**
     * The type identifier.
     */
    int TYPE_ID = Entry.class.hashCode();

    /**
     * Identifier of this object
     */
    TeiidType IDENTIFIER = TeiidType.VDB_ENTRY;

    /**
     * An empty array of VDB entries.
     */
    Entry[] NO_ENTRIES = new Entry[0];

    /**
     * @return the value of the <code>description</code> property (can be empty)
     */
    String getDescription();

    /**
     * @return the value of the <code>file path</code> property (never empty)
     */
    String getPath();

    /**
     * @param newDescription
     *        the new value of the <code>description</code> property
     */
    void setDescription( final String newDescription );

    /**
     * @param newPath
     *        the new value of the <code>file path</code> property (cannot be empty)
     */
    void setPath( final String newPath );

}
