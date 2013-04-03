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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.builder.ModelBuildUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;


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
	 * Method which delegates to all handlers the ability to update models that are dependent on the refactored models
	 * 
	 * @param type the type of the refactor operations (see <code>IRefactorModelHandler</code>
	 * @param modelResource the dependent model
	 * @param refactoredPaths a Map containing original and new model paths
	 * @param monitor the ProgressMonitor
	 */
	public static void helpUpdateDependentModelContents(int type, ModelResource modelResource,
			Collection<PathPair> refactoredPaths, IProgressMonitor monitor) {
		if( !handlersLoaded ) {
			loadExtensions();
		}
		
		for( IRefactorModelHandler handler : handlers) {
			handler.helpUpdateDependentModelContents(type, modelResource, refactoredPaths, monitor);
		}
	}

    /**
	 * @param type the type of the refactor operations (see <code>IRefactorModelHandler</code>
	 * @param refactoredResource the dependent model
	 * @param refactoredPaths a Map containing original and new model paths
	 * @param monitor the ProgressMonitor
     * @see org.teiid.designer.core.refactor.IRefactorNonModelResourceHandler#processNonModel(int, org.eclipse.core.resources.IResource, java.util.Map, 
     * 	org.eclipse.core.runtime.IProgressMonitor)
     */
    public static void helpUpdateNonModelResource( int type,
                                                   IResource refactoredResource,
                                                   Collection<PathPair> refactoredPaths,
                                                   IProgressMonitor monitor ) {
        if (!handlersLoaded) {
            loadExtensions();
        }

        try {
            for (IRefactorModelHandler handler : handlers) {
                if (handler instanceof IRefactorNonModelResourceHandler) {
                    ((IRefactorNonModelResourceHandler)handler).processNonModel(type, refactoredResource, refactoredPaths, monitor);
                }
            }
        } catch (Exception e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }
	
	/**
	 * Method which delegates to all handlers the ability to update or perform internal refactoring for the refactored models
	 * 
	 * @param type the type of the refactor operations (see <code>IRefactorModelHandler</code>
	 * @param refactoredModelResource the dependent model
	 * @param refactoredPaths a Map containing original and new model paths
	 * @param monitor the ProgressMonitor
     * @see org.teiid.designer.core.refactor.IRefactorModelHandler#helpUpdateModelContents(int, org.teiid.designer.core.workspace.ModelResource, java.util.Map, 
     *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public static void helpUpdateModelContents(int type, ModelResource refactoredModelResource, Collection<PathPair> refactoredPaths, IProgressMonitor monitor) {
		if( !handlersLoaded ) {
			loadExtensions();
		}
		
		try {
			if( !ModelUtil.isXsdFile(refactoredModelResource.getCorrespondingResource()) ) {
				for( IRefactorModelHandler handler : handlers) {
					handler.helpUpdateModelContents(type, refactoredModelResource, refactoredPaths, monitor);
				}
			}
		} catch (ModelWorkspaceException theException) {
			ModelerCore.Util.log(IStatus.ERROR, theException, theException.getMessage());
		}
	}
	
	/**
	 * 
	 * @param deletedResourcePaths a collection of deleted resource paths
	 * @param directDependentResources collection of direct dependent resources
	 * @param monitor the process monitor
	 * @see org.teiid.designer.core.refactor.IRefactorModelHandler#helpUpdateModelContentsForDelete(
	 * 		java.util.Collection, java.util.Collection, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public static void helpUpdateModelContentsForDelete(Collection<Object> deletedResourcePaths, Collection<Object> directDependentResources, IProgressMonitor monitor) {
		if( !handlersLoaded ) {
			loadExtensions();
		}
		
		
		for( IRefactorModelHandler handler : handlers) {
			handler.helpUpdateModelContentsForDelete(deletedResourcePaths, directDependentResources, monitor);
		}
		
		// Need to call update imports for all direct dependent resources
		
		for( Object nextObj : directDependentResources ) {
			IResource iRes = (IResource)nextObj;
			
            try {
            	ModelResource mr = ModelUtil.getModelResource((IFile)iRes, true);
            	if( !ModelUtil.isXsdFile(mr.getCorrespondingResource()) ) {
	                ModelBuildUtil.rebuildImports(mr.getEmfResource(), true);
	                mr.save(new NullProgressMonitor(), true);
				}
            } catch (final ModelWorkspaceException theException) {
            	ModelerCore.Util.log(IStatus.ERROR, theException, theException.getMessage());
            }
		}
	}
	
    /**
     * 
	 * @param refactorType the refactor type
     * @param refactoredResource the refactored resource
     * @param monitor the progress monitor
     * @return if all handlers approve of refactoring
     * @see org.teiid.designer.core.refactor.IRefactorModelHandler#preProcess(int, org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public static boolean preProcess(final int refactorType, final IResource refactoredResource, IProgressMonitor monitor) {
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
     * 
	 * @param refactorType the refactor type
     * @param refactoredResource the refactored resource
     * @param monitor the progress monitor
     * @see org.teiid.designer.core.refactor.IRefactorModelHandler#postProcess(int, org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public static void postProcess(final int refactorType, final IResource refactoredResource, IProgressMonitor monitor) {
		if( !handlersLoaded ) {
			loadExtensions();
		}
		
		for( IRefactorModelHandler handler : handlers) {
			handler.postProcess(refactorType, refactoredResource, monitor);
		}
	}
}