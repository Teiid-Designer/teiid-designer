/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.selection;

/**
 * TreeSelection
 */
public interface TreeSelection {

    /**
     * Selection constant (value 0) indicating a model object is <i>not</i> selected, nor are any model objects (directly or
     * indirectly) below it.
     */
    int UNSELECTED = 0; // NO_UCD (Indicates this is ignored by unused code detection tool)

    /**
     * Selection constant (value 1) indicating this model object <i>is</i> selected as are all of the model objects (directly and
     * indirectly) below it.
     */
    int SELECTED = 1;

    /**
     * Selection constant (value 2) indicating this model object is <i>not</i> selected, while some of the model objects (directly
     * or indirectly) below it are selected and some are not selected.
     */
    int PARTIALLY_SELECTED = 2;

    /**
     * Return the selection mode on this the specified object.
     * 
     * @param modelObject the object for which the selection state is to be returned; may not be null
     * @return the current selection; one of {@link #SELECTED}, {@link #PARTIALLY_SELECTED} or {@link #UNSELECTED}
     */
    int getSelectionMode( Object node );

}
