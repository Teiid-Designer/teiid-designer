/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.refactor;

import java.util.Collection;
import java.util.HashMap;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.refactor.IRefactorModelHandler.RefactorType;


/**
 * This class manages the loading of Refactor Model Handler extension point contributions.
 * 
 * The primary methods:
 * 
 *     helpUpdateDependentModelContents()
 *     helpUpdateModelContents()
 *     
 * allow refactor commands to perform "clean-up" or additional work.
 * 
 * Two examples are renaming SQL Model-names for RENAME operations and renaming Model names in Choice criteria for 
 * XML document models.
 * 
 *
 *
 * @since 8.0
 */
public class RefactorModelExtensionManager {
    @SuppressWarnings("javadoc")
	public final static String ID = "refactorModelHandler"; //$NON-NLS-1$
    @SuppressWarnings("javadoc")
    public final static String REFACTOR_HANDLER_TAG = "refactorHandler"; //$NON-NLS-1$
    @SuppressWarnings("javadoc")
    public final static String CLASSNAME = "name"; //$NON-NLS-1$

	private static Collection<IRefactorModelHandler> handlers;
	private static boolean handlersLoaded = false;

	
	private static void loadExtensions() {
		HashMap extList = new HashMap();
		handlersLoaded = true;


		// get the NewChildAction extension point from the plugin class
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(ModelerCore.PLUGIN_ID, ID);
		
		// get the all extensions to the NewChildAction extension point
		IExtension[] extensions = extensionPoint.getExtensions();
		
		// walk through the extensions and find all INewChildAction implementations
		for ( int i=0 ; i<extensions.length ; ++i ) {
			IConfigurationElement[] elements = extensions[i].getConfigurationElements();
			try {

				// first, find the content provider instance and add it to the instance list
				for ( int j=0 ; j<elements.length ; ++j ) {
					if ( elements[j].getName().equals(REFACTOR_HANDLER_TAG)) {
						Object helper = elements[j].createExecutableExtension(CLASSNAME);
						// Set the text label

						extList.put(elements[j].getAttribute(CLASSNAME), helper);
					}
				}
            
			} catch (Exception e) {
				// catch any Exception that occurred obtaining the configuration and log it
				String message = ModelerCore.Util.getString("RefactorModelExtensionManager.loadingExtensionsErrorMessage", //$NON-NLS-1$
							extensions[i].getUniqueIdentifier()); 
				ModelerCore.Util.log(IStatus.ERROR, e, message);
			}
		}
		
		
		handlers = extList.values();
	}

    /**
     * 
	 * @param refactorType the refactor type
     * @param refactoredResource the refactored resource
     * @param monitor the progress monitor
     * @return if all handlers approve of refactoring
     * @see org.teiid.designer.core.refactor.IRefactorModelHandler#preProcess(RefactorType, IResource, IProgressMonitor)
	 */
	public static boolean preProcess(RefactorType refactorType, final IResource refactoredResource, IProgressMonitor monitor) {
		if( !handlersLoaded ) {
			loadExtensions();
		}
		
		for( IRefactorModelHandler handler : handlers) {
			if( ! handler.preProcess(refactorType, refactoredResource, monitor) ) {
				return false;
			}
		}
		
		return true;
	}
	
    /**
     * @param refactorType type of refactoring
     * @param refactoredResource the refactored resource
     * @throws Exception
	 */
	public static void postProcess(RefactorType refactorType, final IResource refactoredResource) throws Exception {
		if( !handlersLoaded ) {
			loadExtensions();
		}
		
		for( IRefactorModelHandler handler : handlers) {
			handler.postProcess(refactorType, refactoredResource);
		}
	}
}