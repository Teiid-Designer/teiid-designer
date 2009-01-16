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

package com.metamatrix.modeler.core.util;

import java.util.Map;

import com.metamatrix.modeler.core.ModelerCoreException;


/** This interface provides plugins the capability to contribute to the creation of new objects
 *  The primary use case lies in creating virtual tables. Defect 18433 pointed out that the user could
 *  not UNDO the creation with one undo.  There were other symptoms, but the reality was, some additional work
 *  was being performed (Creating transformation roots and helpers) by notification listeners, etc.  This interface 
 *  allows the work to be done up-front and creates a more concrete set of work.
 *  To generically contribute an implementation of this interface, use the com.metamatrix.modeler.ui.newModelObjectHelper 
 *  extension point ID.
 *  The NewModelObjectHelperManager collects all extensions of this type and provides the hooks to do the work.
 * @since 4.3
 */
public interface INewModelObjectHelper {

    /**
     * Method to determine if a helper can take over creation of specific EObject based on model resource, parent object
     * and new object descriptor (i.e. Command) 
     * @param newObject
     * @return
     * @since 4.3
     */
    boolean canHelpCreate(Object newObject);
    
    /**
     * Method that actually does creation work. 
     * @param newObject
     * @param properties extra properties to specify to tweak creation.  For example, a property
     *   can be set to prevent virtual base tables from having supportsUpdate set to false.  This
     *   value may be null or Collections.EMPTY_MAP if there are no such properties.
     * @return true, if the actions performed were undoable, false otherwise.
     * @since 4.3
     */
    boolean helpCreate(Object newObject, Map properties) throws ModelerCoreException;

}
