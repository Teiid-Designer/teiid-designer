/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;


/**
 * The Teiid VDB entry business object.
 */
public class Entry extends VdbAdminObject {

    /**
     * @return the path (which is the identifier)
     * @see #getId()
     */
    public String getPath() {
        return getId();
    }

    /**
     * Generates a property change event of type {@link VdbObject.PropertyName#ID} if the path is changed.
     * 
     * @param newPath the new path (which is the identifier)
     * @see Entry#setId(String)
     */
    public void setPath(String newPath) {
        setId(newPath);
    }

}
