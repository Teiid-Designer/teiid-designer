/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import com.metamatrix.modeler.core.ModelerCore;



/** 
 * @since 5.0
 */
public class RefactorResourceUtil {


    private static List<IRefactorResourceListener> listeners = Collections.EMPTY_LIST;
    private static boolean listenersLoaded = false;
    
    /** Constants for the RefactorResourceListener extension point */
    private static    String EXTENSION_ID                    = "refactorResourceListener";   //$NON-NLS-1$
    private static    String EXTENSION_CLASS                 = "class"; //$NON-NLS-1$
    private static    String EXTENSION_CLASSNAME             = "name"; //$NON-NLS-1$

    public static void notifyRefactored(RefactorResourceEvent event) {
        loadProviderList();
        
        for( IRefactorResourceListener listener: listeners) {
            listener.notifyRefactored(event);
        }
    }
    
    private static void loadProviderList() {
        // if already loaded, return. We don't want to load them for all instances of this class.
        if( listenersLoaded ) {
            return;
        }
        
        listenersLoaded = true;
        
        
        //  -------------------------------------------------------------------------------------------------------
        // build a list of all IRefactorResourceListeners
        // -------------------------------------------------------------------------------------------------------
        
        // get the IRefactorResourceListener extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(ModelerCore.PLUGIN_ID, EXTENSION_ID);
        // get the all extensions to the extension point
        IExtension[] extensions = extensionPoint.getExtensions();
        
        listeners = new ArrayList<IRefactorResourceListener>(extensions.length);
        
        // walk through the extensions and find all IRefactorResourceListeners
        for ( int i=0 ; i<extensions.length ; ++i ) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();

            try {
    
                // find the content provider instance and add it to the instance list
                for ( int j=0 ; j<elements.length ; ++j ) {
                    if ( elements[j].getName().equals(EXTENSION_CLASS)) {
                        Object listener = elements[j].createExecutableExtension(EXTENSION_CLASSNAME);
                        if ( listener instanceof IRefactorResourceListener ) {
                            listeners.add((IRefactorResourceListener)listener);
                            break;
                        }
                    }
                }
                
            } catch (Exception e) {
                ModelerCore.Util.log(e);
            }
        }
        // -------------------------------------------------------------------------------------------------------
        
    }

}
