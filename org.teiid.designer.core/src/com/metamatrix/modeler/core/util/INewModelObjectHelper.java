/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
