/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model;

/**
 * Indicates the Teiid object has a description.
 */
public interface Describable {

    /**
     * The name of the description property.
     */
    String DESCRIPTION = Describable.class.getSimpleName() + ".description"; //$NON-NLS-1$

    /**
     * @return the name (can be <code>null</code> or empty)
     */
    String getDescription();

    /**
     * Generates a property change event if the description is changed.
     * 
     * @param newDescription the new descripition (can be <code>null</code> or empty)
     */
    void setDescription(String newDescription);

}
