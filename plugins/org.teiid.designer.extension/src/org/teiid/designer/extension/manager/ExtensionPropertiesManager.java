/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.Messages;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCoreException;
/**
 * This class is the backbone for the new Model Extensions framework.
 * 
 * The mechanism for persisting Model Extension properties will not change from old framework. Properties are still
 * defined by adding key/value pair entries defined as "<tags ...../> objects within an <code>Annotation</code> referenced
 * to each model object.
 * 
 * The responsibility for adding/removing/editing the tags values has been moved from the EMF framework (via a ModelExtensions metamodel)
 * to the {@link IExtensionPropertiesHandler} implementations.
 * 
 * In old framework, default values were NOT persisted in the xmi file. At indexing time, the contributed model extension property
 * feature was access for it's default value and property indexed.
 * 
 * In the new framework, the JCR Compact Node Definition (CND) representation will be used to define extended model properties
 * in the form of key, value, type and default values. During indexing time, the {@link IExtensionPropertiesHandler} will use
 * this info to provide the missing (i.e. default) values. 
 * 
 *
 */

public class ExtensionPropertiesManager {
	public final static 	String ID = "extensionPropertiesHandler"; //$NON-NLS-1$
    public final static     String REFACTOR_HANDLER_TAG = "propertiesHandler"; //$NON-NLS-1$
    public final static     String CLASSNAME = "name"; //$NON-NLS-1$

	private static Collection<IExtensionPropertiesHandler> handlers;
	private static boolean handlersLoaded = false;

	private static Set<String> MODEL_EXTENSION_IDS = new HashSet<String>();
	
	private static void loadExtensions() {
		HashMap extList = new HashMap();
		handlersLoaded = true;


		// get the NewChildAction extension point from the plugin class
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(ExtensionPlugin.PLUGIN_ID, ID);
		
		// get the all extensions to the NewChildAction extension point
		IExtension[] extensions = extensionPoint.getExtensions();
		
		// walk through the extensions and find all INewChildAction implementations
		for ( int i=0 ; i<extensions.length ; ++i ) {
			IConfigurationElement[] elements = extensions[i].getConfigurationElements();
			try {

				// first, find the content provider instance and add it to the instance list
				for ( int j=0 ; j<elements.length ; ++j ) {
					if ( elements[j].getName().equals(REFACTOR_HANDLER_TAG)) {
						IExtensionPropertiesHandler helper = (IExtensionPropertiesHandler)elements[j].createExecutableExtension(CLASSNAME);
						// Set the text label

						extList.put(elements[j].getAttribute(CLASSNAME), helper);
						if( MODEL_EXTENSION_IDS.contains(helper.getID()) ) {
							// TODO: Throw exception
						} else {
							MODEL_EXTENSION_IDS.add(helper.getID());
						}
					}
				}
            
			} catch (Exception e) {
				// catch any Exception that occurred obtaining the configuration and log it
				String message = NLS.bind(Messages.ExtensionPropertiesManager_loadExtensionsErrorMessage,
							extensions[i].getUniqueIdentifier()); 
				ExtensionPlugin.Util.log(IStatus.ERROR, e, message);
			}
		}
		
		
		handlers = extList.values();
	}
	
	/**
	 * Method returns a list of extended model {@link Properties} for a given {@link EObject} target model object.
	 * 
	 * @param target the {@link EObject} target of the annotation containing the extended model properties.
	 * @return the extended model properties
	 */
	public static Properties getExtendedProperties(final EObject target) {
		// Walk through the handlers and gather up all extension properties
		
		if( !handlersLoaded ) {
			loadExtensions();
		}
		Properties props = new Properties();

		try {
			for( IExtensionPropertiesHandler handler : handlers) {
				Properties tempProps = handler.getExtendedProperties(target);
				if( tempProps != null && !tempProps.isEmpty() ) {
					props.putAll(tempProps);
				}
			}
		} catch (ModelerCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return props;
	}
	
	/**
	 * Method determines if a target {@link EObject} has extended model {@link Properties}.
	 * 
	 * @param target the {@link EObject} target of the annotation containing the extended model properties.
	 * @return true if properties exists for the target {@link EObject}
	 */
	public static boolean hasExtendedProperties(final EObject target) {
		// Walk through the handlers and gather up all extension properties
		
		if( !handlersLoaded ) {
			loadExtensions();
		}

		try {
			for( IExtensionPropertiesHandler handler : handlers) {
				boolean hasProps = handler.hasExtendedProperties(target);
				if( hasProps ) {
					return true;
				}
			}
		} catch (ModelerCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Method determines if a target {@link EObject} has extended model {@link Properties}.
	 * 
	 * @param target the {@link EObject} target of the annotation containing the extended model properties.
	 * @return true if properties exists for the target {@link EObject}
	 */
	public static ExtendedModelObject getExtendedModelObject(final EObject target) {
		// Walk through the handlers and gather up all extension properties
		
		if( !handlersLoaded ) {
			loadExtensions();
		}

		try {
			for( IExtensionPropertiesHandler handler : handlers) {
				ExtendedModelObject extendedMO = handler.getExtendedModelObject(target);
				if( extendedMO != null ) {
					return extendedMO;
				}
			}
		} catch (ModelerCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Method determines if a target {@link EObject} is a model with extended properties.
	 * 
	 * @param target the {@link EObject} target of the annotation containing the extended model properties.
	 * @return true if properties exists for the target {@link EObject}
	 */
	public static boolean isApplicable(final EObject target) {
		// Walk through the handlers and gather up all extension properties
		
		if( !handlersLoaded ) {
			loadExtensions();
		}
		boolean result = false;
		for( IExtensionPropertiesHandler handler : handlers) {
			if( handler.isApplicable(target) ) {
				result = true;
			}
		}

		
		return result;
	}
	
	/**
	 * Method determines if a target {@link EObject} is a model with extended properties.
	 * 
	 * @param target the {@link EObject} target of the annotation containing the extended model properties.
	 * @return true if properties exists for the target {@link EObject}
	 */
	public static boolean isApplicable(final IResource resource) {
		// Walk through the handlers and gather up all extension properties
		
		if( !handlersLoaded ) {
			loadExtensions();
		}
		boolean result = false;
		
		for( IExtensionPropertiesHandler handler : handlers) {
			if( handler.isApplicable(resource)) {
				result = true;
			}
		}

		
		return result;
	}
	
	/**
	 * Provide means to determine if a tag's key is a new Model Extension property. Used by {@link RuntimeAdapter}.
	 * 
	 * @param rawKey
	 * @returno
	 */
	public static boolean isExtendedKey(final Object rawKey) {
		CoreArgCheck.isNotNull(rawKey, "rawKey"); //$NON-NLS-1$
		
		if( !handlersLoaded ) {
			loadExtensions();
		}
		
		if( rawKey instanceof String ) {
			for( IExtensionPropertiesHandler handler : handlers) {
				if( handler.isExtendedKey((String)rawKey) ) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Simple method to provide {@link RuntimeAdapter} the ability to filter out certain custom tags from being indexed.
	 * 
	 * @param rawKey
	 * @return
	 */
	public static boolean isNonIndexedKey(final Object rawKey) {
		CoreArgCheck.isNotNull(rawKey, "rawKey"); //$NON-NLS-1$
		
		// Tag keys that need to get weeded out to prevent indexing
		// This method should become obsolete when we remove old Extension Models as supported metamodels.
		
		String theKey = (String)rawKey;
		if( theKey.startsWith("connection") || theKey.startsWith("translator") ||  //$NON-NLS-1$ //$NON-NLS-2$
			theKey.startsWith(IExtensionPropertiesHandler.EXTENSION_ID_PREFIX) ||
			theKey.startsWith(IExtensionPropertiesHandler.EXTENSION_NAMEPSACE_PREFIX) ||
			theKey.startsWith(IExtensionPropertiesHandler.EXTENSION_CND_PREFIX ) ) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns a list of missing extended model {@link Properties} for a given {@link EObject} target model object
	 * and the current list of properties. Since "Default" properties are not persisted, but defined by each contribution, 
	 * the indexer, {@link RuntimeAdapter}, needs to index all properties. This method fills in the missing properties.
	 * 
	 * @param target the {@link EObject} target of the annotation containing the extended model properties.
	 * @param currentProps
	 * @return
	 */
	public static Properties getMissingDefaultProperties(EObject target, Properties currentProps) {
		if( !handlersLoaded ) {
			loadExtensions();
		}
		
		Properties resultProps = new Properties();
		
		for( IExtensionPropertiesHandler handler : handlers) {
			resultProps.putAll(handler.getMissingDefaultProperties(target, currentProps));
		}
		
		return resultProps;
	}
	
	public static String createExtendedModelNamespace(final String id) {
		return IExtensionPropertiesHandler.PROPERTY_KEY_NAMESPACE_PREFIX + id + ':';
	}
	
	public static String getDisplayName(EObject target) {
		if( !handlersLoaded ) {
			loadExtensions();
		}
		
		Collection<String> displayNames = new ArrayList<String>();
		
		for( IExtensionPropertiesHandler handler : handlers) {
			if( handler.isApplicable(target) ) {
				displayNames.add(handler.getDisplayName());
			}
		}
		
		if( displayNames.size() == 1 ) {
			return displayNames.iterator().next();
		}
		
		return  null;
	}
}
