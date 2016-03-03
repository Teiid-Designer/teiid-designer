/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.core.designer.id;

import java.io.Serializable;
import org.teiid.core.designer.util.CoreArgCheck;


/**
 * @since 8.0
 */
public class LongID implements ObjectID, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String PROTOCOL = "mmlong"; //$NON-NLS-1$

    private long id;

    protected LongID(long id) {
	    this.id = id;
    }
    /**
     * Get hash code for object
     * @return Hash code
     */
    @Override
	public int hashCode() {
        return (int) this.id;
    }

    /**
    /**
     * Returns true if the specified object is semantically equal to this instance.
     * Note:  this method is consistent with <code>compareTo()</code>.
     * <p>
     * @param obj the object that this instance is to be compared to.
     * @return whether the object is equal to this object.
     */
    @Override
	public boolean equals(Object obj) {
        // Check if instances are identical ...
        if ( this == obj ) {
            return true;
        }

        // Check if object can be compared to this one
        // (this includes checking for null ) ...
        //if ( this.getClass().isInstance(obj) ) {
        if ( obj instanceof LongID ) {
            LongID that = (LongID) obj;
            return ( this.id == that.id );
		}

        // Otherwise not comparable ...
        return false;
    }
    /**
     * Compares this object to another. If the specified object is not an instance of
     * the LongID class, then this method throws a
     * ClassCastException (as instances are comparable only to instances of the same
     * class).
     * Note:  this method <i>is</i> consistent with <code>equals()</code>, meaning
     * that <code>(compare(x, y)==0) == (x.equals(y))</code>.
     * <p>
     * @param obj the object that this instance is to be compared to; may not be null.
     * @return a negative integer, zero, or a positive integer as this object
     *      is less than, equal to, or greater than the specified object, respectively.
     * @throws ClassCastException if the specified object's type prevents it
     *      from being compared to this instance.
     */
    @Override
	public int compareTo(Object obj) {
        LongID that = (LongID) obj;     // May throw ClassCastException
        CoreArgCheck.isNotNull(obj);

        long diff = this.id - that.id;
        if ( diff < 0 ) {
            return -1;
        }
        if ( diff > 0 ) {
            return 1;
        }
        return 0;
    }

    /**
     * Returns a string representing the current state of the object.
     * @return the string representation of this instance.
     */
    @Override
	public String toString(){
        return PROTOCOL + ObjectID.DELIMITER + this.id;
    }
    
    /**
     * @see org.teiid.core.designer.id.ObjectID#toString(char)
     */
    @Override
	public String toString(char delim) {
        return PROTOCOL + delim + this.id;
    }

    /**
     * Return the name of the protocol that this factory uses.
     * @return the protocol name
     */
    @Override
	public String getProtocol() {
	    return PROTOCOL;
    }

    public long getValue() {
        return this.id;
    }
}

