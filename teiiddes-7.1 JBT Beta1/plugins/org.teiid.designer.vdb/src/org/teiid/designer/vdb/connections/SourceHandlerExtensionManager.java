package org.teiid.designer.vdb.connections;

import java.util.Properties;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbPlugin;

/**
 * 
 * Class to provide extension management for the org.teiid.designer.vdb.connectionFinder extension point.
 * 
 * In this particular case, there isn't expected to be more than ONE extension point so we need to log an error if
 * we find more than the expected.
 *
 */
public class SourceHandlerExtensionManager {
	private static boolean extensionsLoaded;
	private static SourceHandler vdbSourceHandler;

	/**
	 * @param obj the target object
	 * @return the array of actions in object array form
	 */
	public static Object[] findApplicableActions(Object obj) {
		if( !extensionsLoaded ) {
			loadExtensions();
		}
		
		Object[] actions = null;
		
		if( vdbSourceHandler != null ) {
			try {
				actions = vdbSourceHandler.getApplicableActions(obj);
			} catch (Exception e) {
                String message = VdbPlugin.UTIL.getString("errorFindingApplicableActionsFor", obj);    //$NON-NLS-1$
				VdbPlugin.UTIL.log(IStatus.ERROR, e, message);
			}
		}
		
		return actions;		
		
	}
	
	/**
	 * 
	 * @param sourceModelName
	 * @param properties
	 * @return the vdb source connection object
	 */
	public static VdbSourceConnection findVdbSourceConnection(String sourceModelName, Properties properties) {
		if( !extensionsLoaded ) {
			loadExtensions();
		}
		
		VdbSourceConnection sourceConnection = null;
		
		if( vdbSourceHandler != null ) {
			try {
				sourceConnection = vdbSourceHandler.ensureVdbSourceConnection(sourceModelName, properties);
			} catch (Exception e) {
                String message = VdbPlugin.UTIL.getString("errorFindingVdbSourceConnection", sourceModelName);//$NON-NLS-1$     
				VdbPlugin.UTIL.log(IStatus.ERROR, e, message);
			}
		}
		
		return sourceConnection;

	}
	
	/**
	 * Returns the instance of a ConnectionFinder for use with a VDB to assist in finding a matching Connection
	 * 
	 * @return the ConnectionFinder
	 */
	public static SourceHandler getVdbConnectionFinder() {
		if( !extensionsLoaded ) {
			loadExtensions();
		}
		
		return vdbSourceHandler;
	}
	
    private static void loadExtensions() {
        extensionsLoaded = true;

        String id = VdbConstants.SourceHandlerExtension.ID;
        String classTag = VdbConstants.SourceHandlerExtension.CLASS;
        String className = VdbConstants.SourceHandlerExtension.CLASSNAME;
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(VdbConstants.PLUGIN_ID, id);
        
        // get the all extensions to the NewChildAction extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        // walk through the extensions and find all INewChildAction implementations
        if( extensions != null & extensions.length > 0 ) {
	        IConfigurationElement[] elements = extensions[0].getConfigurationElements();
            try {
                // Find the first one
                for (int j = 0; j < elements.length; ++j) {
                    if (elements[j].getName().equals(classTag)) {
                        vdbSourceHandler = (SourceHandler)elements[j].createExecutableExtension(className);
                    }
                }

            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = VdbPlugin.UTIL.getString("errorloadingExtensionsErrorMessage", extensions[0].getUniqueIdentifier());//$NON-NLS-1$     
                VdbPlugin.UTIL.log(IStatus.ERROR, e, message);
            }
            if( extensions.length > 1 ) {
            	for( int i=1; i<extensions.length; i++ ) {
            		String message = VdbPlugin.UTIL.getString("unexpectedExtensionErrorMessage", extensions[i].getUniqueIdentifier());//$NON-NLS-1$
            		VdbPlugin.UTIL.log(IStatus.ERROR, message);
            	}
            }
        }

    }
}
