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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;


/** The NewModelObjectHelperManager collects all extensions of this type and provides the hooks to do the work.
 *  
 *  The <code>INewModelObjectHelper</code> interface provides plugins the capability to contribute to the creation of new objects
 *  The primary use case lies in creating virtual tables. Defect 18433 pointed out that the user could
 *  not UNDO the creation with one undo.  There were other symptoms, but the reality was, some additional work
 *  was being performed (Creating transformation roots and helpers) by notification listeners, etc.  This interface 
 *  allows the work to be done up-front and creates a more concrete set of work.
 *  To generically contribute an implementation of this interface, use the com.metamatrix.modeler.ui.newModelObjectHelper 
 *  extension point ID.
 *  
 * @since 4.3
 */
public abstract class NewModelObjectHelperManager {
    private static final String ID              = "com.metamatrix.modeler.core.newModelObjectHelper"; //$NON-NLS-1$
    private static final String CLASS           = "class"; //$NON-NLS-1$
    private static final String CLASSNAME       = "name"; //$NON-NLS-1$

    private static List helpersCache;
    private static boolean helpersLoaded = false;
    

    public static INewModelObjectHelper[] getHelpers(Object newObject) {
        ArgCheck.isNotNull(newObject);
            
        HashSet helpers = new HashSet();
        
        if( !helpersLoaded )
            loadHelperExtensions();
            
        if( helpersCache != null ) {
            // walk through the helpers and ask 
            INewModelObjectHelper nextHelper = null;
            for( Iterator iter = helpersCache.iterator(); iter.hasNext(); ) {
                nextHelper = (INewModelObjectHelper)iter.next();
                if( nextHelper.canHelpCreate(newObject) ) {
                    helpers.add(nextHelper);
                }
            }
        }
        return (INewModelObjectHelper[])helpers.toArray(new INewModelObjectHelper[helpers.size()]);
        
    }
    
    private static void loadHelperExtensions() {
        helpersCache = new ArrayList();
        helpersLoaded = true;
        
        // get the NewChildAction extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(ID);
        
        // get the all extensions to the NewChildAction extension point
        IExtension[] extensions = extensionPoint.getExtensions();
        
        // walk through the extensions and find all INewChildAction implementations
        for ( int i=0 ; i<extensions.length ; ++i ) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            try {

                // first, find the content provider instance and add it to the instance list
                for ( int j=0 ; j<elements.length ; ++j ) {
                    if ( elements[j].getName().equals(CLASS)) {
                        Object helper = elements[j].createExecutableExtension(CLASSNAME);
                        if ( helper instanceof INewModelObjectHelper ) {
                            helpersCache.add(helper);
                        }
                    }
                }
            
            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = ModelerCore.Util.getString("NewModelObjectHelperManager.loadHelperExtensionsErrorMessage", //$NON-NLS-1$
                            extensions[i].getUniqueIdentifier()); 
                ModelerCore.Util.log(IStatus.ERROR, e, message);
            }
        }
    }
    
    public static boolean helpCreate(Object newObject, Map properties) throws ModelerCoreException  {
        ArgCheck.isNotNull(newObject);
        
        // Set default behavior to undoable. This is the behavior of the UnitOfWorkImpl.
        boolean canUndo = true;
            
        // Defect 18433 - BML 8/31/05 - Added this manager and INewModelObjectHelper interface to give arbitary
        // plugins the change to contribute more work following the creation of a new object
        // In the case of a Virtual Group, we needed to create the transformation for that table so it didn't 
        // get lazily created as another "Undo" event. (i.e. TransformationNotificationListener, EditAction,, etc.)
        // If Helper exists, ask for helpCreate(newObj)
        // The helper also has the opportunity to change/override the "Undo" state of the transaction
        
        // Get all applicable helpers
        INewModelObjectHelper[] helpers = getHelpers(newObject);
        boolean undoHelp = true;
        
        // Walk through each and ask to help create additional objects
        for( int i=0; i<helpers.length; i++ ) {
            undoHelp = helpers[i].helpCreate(newObject, properties);
            // if any helper says it can't undo the work, then we set the canUndo for the entire operation
            if( !undoHelp && canUndo )
                canUndo = false;
        }
        
        return canUndo;
    }

}
