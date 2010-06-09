package org.teiid.designer.vdb.connections;

import java.util.Properties;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbPlugin;

import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * 
 * Class to provide extension management for the org.teiid.designer.vdb.connectionFinder extension point.
 * 
 * In this particular case, there isn't expected to be more than ONE extension point so we need to log an error if
 * we find more than the expected.
 *
 */
public class ConnectionFinderExtensionManager {
	private static boolean extensionsLoaded;
	private static ConnectionFinder vdbConnectionFinder;

	/**
	 * Method returns a connection name based on properties found (or not) within the <code>EmfResource</code> model and
	 * the provided model name.
	 * 
	 * @param model
	 * @param name
	 * @return the best-matched source connection name
	 */
	public static String findConnectionName(EmfResource model, String name) {
		if( !extensionsLoaded ) {
			loadExtensions();
		}
		
		String connectionName = name;
		
		if( vdbConnectionFinder != null ) {
			try {
				connectionName = vdbConnectionFinder.findConnectionName(name, getConnectionProperties(model, name));
			} catch (Exception e) {
                String message = VdbPlugin.UTIL.getString("errorFindingConnectionForSource", name);//$NON-NLS-1$     
				VdbPlugin.UTIL.log(IStatus.ERROR, e, message);
				return connectionName;
			}
		}
		
		return connectionName;
	}
	
	private static Properties getConnectionProperties(EmfResource model, String name) {
		Properties props = new Properties();
		props.setProperty("name", name);
		return props;
	}
	
	/**
	 * Returns the instance of a ConnectionFinder for use with a VDB to assist in finding a matching Connection
	 * 
	 * @return the ConnectionFinder
	 */
	public static ConnectionFinder getVdbConnectionFinder() {
		if( !extensionsLoaded ) {
			loadExtensions();
		}
		
		return vdbConnectionFinder;
	}
	
    private static void loadExtensions() {
        extensionsLoaded = true;

        String id = VdbConstants.ConnectionFinderExtension.ID;
        String classTag = VdbConstants.ConnectionFinderExtension.CLASS;
        String className = VdbConstants.ConnectionFinderExtension.CLASSNAME;
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
                        vdbConnectionFinder = (ConnectionFinder)elements[j].createExecutableExtension(className);
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
