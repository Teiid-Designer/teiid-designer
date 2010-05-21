/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.namedobject;

public interface BaseObject extends Comparable, Cloneable {

    /**
     * Return the identifier for this object.  The returned type will be an instance of the BaseID subclass
     * which corresponds to the class of this node.
     * @return the specialized BaseID instance for this node.
     */
    BaseID getID();


    /**
     * Returns the name for this instance of the object.  If you are using
     * the dot notation for a naming conventions, this will return the last
     * node in name.
     * @return the name
     *
     * @see getFullName
     */
    String getName();

    /**
     * Returns the full name for this instance of the object.
     * @return the name
     */
    String getFullName();

    /**
     * Return a deep cloned instance of this object.  Subclasses must override
     * this method.
     * @return the object that is the clone of this instance.
     * {@link com.metamatrix.metadata.api.Defaults Defaults} cannot be cloned).
     */
    Object clone();

  
}


