/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.core.selection;

/**
 * TreeSelection
 */
public interface TreeSelection {

    /*====================================================================
     * Constants defining the selection modes:
     *====================================================================*/
    
    /**
     * Selection constant (value 0) indicating a model object is <i>not</i> selected, nor are any
     * model objects (directly or indirectly) below it.
     */
    public static final int UNSELECTED = 0;

    /**
     * Selection constant (value 1) indicating this model object <i>is</i> selected as are all of the 
     * model objects (directly and indirectly) below it.
     */
    public static final int SELECTED = 1;

    /**
     * Selection constant (value 2) indicating this model object is <i>not</i> selected, while some 
     * of the model objects (directly or indirectly) below it are selected and some are not selected.
     */
    public static final int PARTIALLY_SELECTED = 2;

    /**
     * Return the selection mode on this the specified object.
     * @param modelObject the object for which the selection state is to be returned; may not be null
     * @return the current selection; one of {@link #SELECTED}, {@link #PARTIALLY_SELECTED}
     * or {@link #UNSELECTED} 
     */
    public int getSelectionMode(Object node);

}
