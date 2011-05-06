/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.internal.resources.Marker;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.metadata.FunctionMethod;
import org.teiid.metadata.FunctionParameter;
import org.teiid.metadata.FunctionMethod.Determinism;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.function.FunctionTree;
import org.teiid.query.function.SystemFunctionManager;
import org.teiid.query.function.UDFSource;

import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.ModelType;
import com.metamatrix.metamodels.function.ScalarFunction;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

public final class UdfManager implements IResourceChangeListener {
    
    public static final UdfManager INSTANCE = new UdfManager();
    
    public static final SystemFunctionManager SYSTEM_FUNCTION_MANAGER = new SystemFunctionManager();
    
    private FunctionLibrary functionLibrary;
    
    private FunctionLibrary systemFunctionLibrary;
   
    /**
     * A set of function models.
     * 
     * @since 6.0.0
     */
    private Set<ModelResource> functionModels = new HashSet<ModelResource>();

    private volatile boolean initialized;

    /**
     * Don't allow public construction.
     * 
     * @since 6.0.0
     */
    private UdfManager() {
    }
    
    /**
     * Ensures the UDF model is located in the workspace and the FunctionLibraryManager has been initialized using the default UDF
     * model. <strong>This must be called before any other method can be used.</strong>
     * 
     * @throws Exception if there was a problem with the UDF model or library manager
     * @since 6.0.0
     */
    public synchronized void initialize() throws Exception {
        if (initialized) {
            return;
        }

        // register to receive resource events so that we can notify listeners when a function model changes
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        
        Collection<IResource> allResources = WorkspaceResourceFinderUtil.getAllWorkspaceResources();
        try {
            for( IResource next : allResources ) {
                if(! ModelUtil.isModelFile(next, true)) {
                    continue;
                }
                ModelResource mr = ModelUtil.getModelResource((IFile)next, true);

                if( mr.getModelType().getValue() == ModelType.FUNCTION) {
                    registerFunctionModel(mr, false);
                } 
            }
        } catch (Exception err) {
            UdfPlugin.UTIL.log(err);
        }
        
        initialized = true;
    }

    /**
     * @param udfModel the UDF model being checked
     * @return <code>true</code> if the workspace function model does not have validation errors
     * @throws CoreException if there is a problem obtaining the EMF resource of the workspace function model
     * @since 6.0.0
     */
    private boolean isFunctionObjectErrorFree( EObject functionEObject, IMarker[] markers, ModelResource udfModelResource)  {

        if(markers != null && markers.length > 0) {
            for (int ndx = markers.length; --ndx >= 0;) {
                IMarker iMarker = markers[ndx];

                EObject targetEObject = null;
                
                try {
					targetEObject = ModelWorkspaceManager.getModelWorkspaceManager().getMarkerManager().getMarkedEObject(udfModelResource.getCorrespondingResource(),
					                                                                                                             iMarker);
				} catch (ModelWorkspaceException ex) {
					UdfPlugin.UTIL.log(ex);
				}

                if( targetEObject == functionEObject ) {
	                Object attribute = null;
	                if( iMarker != null ) {
	                    try {
	                        attribute = iMarker.getAttribute(IMarker.SEVERITY);
	                    } catch (CoreException e) {
	                        // ResourceException is caught here because some calls to getAttribute() may be on an IMarker who's resource
	                        // does not exist in the workspace any more.  (Defect 15552)
	                        if (e instanceof ModelerCoreException ) {
	                            UdfPlugin.UTIL.log(e);
	                        }
	                    }
	                }
	                if (attribute == null) {
	                    return true;
	                }
	                // Asserting attr is an Integer...
	                final int severity = ((Integer)attribute).intValue();
	                if (severity == IMarker.SEVERITY_ERROR) {
	                    return false;
	                }
                }
            }
        }
        
        return true;
    }
    
    private IMarker[] getMarkers(ModelResource udfModelResource ) {

    	IResource resrc = null;
    	
        if (udfModelResource != null && udfModelResource.exists() ) {
            resrc = udfModelResource.getResource();
            
        }
        if( resrc != null ) {
        	IMarker[] markers = null;
            
            try {
                markers = resrc.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
            } catch (CoreException ex) {
                UdfPlugin.UTIL.log(ex);
                return new Marker[0];
            }
           
            return markers;
        }
        
        return new Marker[0];
    }

    private synchronized boolean makeModification(ModelResource modelResource, boolean deleted ) throws Exception {
        // let query engine know of the change so that functions can be available for modeling
        boolean result = false;
        if (deleted) {
        	//System.out.println(" UDF model " + modelResource.getItemName() + " was REMOVED from the FunctionLibrary");
            result = this.functionModels.remove(modelResource);
        } else {
            //System.out.println(" UDF model " + modelResource.getItemName() + " was ADDED to the FunctionLibrary");
            result = this.functionModels.add(modelResource); //url, new UdfInfo(modelName, url, modelResource));
        }
        
        functionLibrary = null;
        
        return result;
    }

    /**
     * Informs the Query Engine's Function Library that the specified function model should be loaded. This method should be only
     * once for a model. When the model is changed and saved the changes are broadcast via {@link IResourceChangeEvent}s.
     * 
     * @param udfModel the model being registered
     * @throws Exception if there is a problem loading the file
     * @see #addFunctionModel(File)
     */
    public void registerFunctionModel( ModelResource modelResource, boolean delete ) throws Exception {
        makeModification(modelResource, delete);
    }

    @Override
    public void resourceChanged( IResourceChangeEvent event ) {
        if (!initialized) {
            return;
        }
        
        if (ResourceChangeUtilities.isPreClose(event)) {
            IProject project = (IProject)event.getResource();

            if (ModelerCore.hasModelNature(project)) {
                modelProjectClosed(project);
            }
        } else if (ResourceChangeUtilities.isPreDelete(event)) {
            IProject project = (IProject)event.getResource();
            if (ModelerCore.hasModelNature(project)) {
            	
            	modelProjectDeleted(project);
            }
        }
    }
    
    private void modelProjectClosed(IProject project) {
    	modelProjectDeleted(project);
    }
    
    private void modelProjectDeleted(IProject project) {
    	//System.out.println(" Model Project " + project.getName() + " is being DELETED");
    	try {
			for( IResource res : project.members()) {
				if( res instanceof IFolder ) {
					folderDeleted((IFolder)res);
				} else if(res instanceof IFile) {
					fileDeleted((IFile)res);
				}
			}
		} catch (CoreException ex) {
			UdfPlugin.UTIL.log(ex);
		} catch (Exception ex) {
			UdfPlugin.UTIL.log(ex);
		}
    }
    
    private void folderDeleted(IFolder folder) throws CoreException, Exception {
    	for( IResource res : folder.members()) {
    		if( res instanceof IFolder ) {
    			folderDeleted((IFolder)res);
			} else if(res instanceof IFile) {
				fileDeleted((IFile)res);
			}
    	}
    }
    
    private void fileDeleted(IFile file) throws ModelWorkspaceException, Exception {
    	// Do the check for UDF file and un-register the model
    	ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(file);
    	if( modelResource != null ) {
    		UdfManager.INSTANCE.registerFunctionModel(modelResource, true);
    	}
    }

    /**
     * Ensures the UDF model framework is shutdown correctly. <strong>This must be called at the time the plugin is
     * stopped.</strong>
     * 
     * @since 6.0.0
     */
    public void shutdown() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }

    public FunctionLibrary getSystemFunctionLibrary() {
    	if( this.systemFunctionLibrary == null ) {
    		this.systemFunctionLibrary = new FunctionLibrary(SYSTEM_FUNCTION_MANAGER.getSystemFunctions(), new FunctionTree[0]);
    	}
    	return this.systemFunctionLibrary;
    }
    
    public synchronized FunctionLibrary getFunctionLibrary() {
        if (functionLibrary == null) {
            FunctionTree[] trees = new FunctionTree[functionModels.size()];
            int i = 0;
            
            for( ModelResource functionModelResource : functionModels ) {
            	ScalarFunction[] functions = getScalarFunctions(functionModelResource);
            	if( functions.length > 0 ) {
            		IMarker[] markers = getMarkers(functionModelResource);
            		
            		String schema = FileUtils.getFilenameWithoutExtension(functionModelResource.getItemName());
            		FunctionTree tree = new FunctionTree(schema, new UDFSource(Collections.EMPTY_LIST), false);
            		
            		for( ScalarFunction function : functions ) {
            			// Function's must have a return parameter and a Scalar function may not yet have one after
            			// it's initially created (intermediate state)
            			// Also the Function AND it's return parameter (if non-null) need to be error free
            			if( !isFunctionObjectErrorFree(function.getReturnParameter(), markers, functionModelResource) ||
            				function.getReturnParameter() == null || 
            				!isFunctionObjectErrorFree(function.getReturnParameter(), markers, functionModelResource)) {
            				continue;
            			}
            			String description = null;
            			
            			try {
							description = ModelerCore.getModelEditor().getDescription(function);
						} catch (ModelerCoreException ex) {
							UdfPlugin.UTIL.log(ex);
						}
            			
						boolean functionPamameterHasError = false;
						
            			Collection<FunctionParameter> fParams = new ArrayList<FunctionParameter>();
            			
            			for( Object inputParam : function.getInputParameters() ) {
            				if( inputParam instanceof com.metamatrix.metamodels.function.FunctionParameter) {
            					com.metamatrix.metamodels.function.FunctionParameter param = (com.metamatrix.metamodels.function.FunctionParameter)inputParam;
            					fParams.add(new FunctionParameter(param.getName(), param.getType()));
            					// If any function parameter has an error don't add this
            					if( !functionPamameterHasError && !isFunctionObjectErrorFree(param, markers, functionModelResource)){
            						functionPamameterHasError = true;
            					}
            				}
            			}
            			
            			if( functionPamameterHasError ) {
            				continue;
            			}
            			
            			String returnParamName = ModelerCore.getModelEditor().getName(function.getReturnParameter());
            			FunctionParameter outputParam = new FunctionParameter(returnParamName, function.getReturnParameter().getType()); 
            			
            			FunctionMethod fMethod = 
            				new FunctionMethod(
            						function.getName(), 
            						description, 
            						function.getCategory(), 
            						null, 
            						function.getInvocationClass(), 
            						function.getInvocationMethod(),
            						fParams.toArray(new FunctionParameter[0]),
            						outputParam,
            					    false,
            						null
            						);
            			fMethod.setPushDown(function.getPushDown().getLiteral());
            			if( function.isDeterministic() ) {
            				fMethod.setDeterminism(Determinism.DETERMINISTIC);
            			} else {
            				fMethod.setDeterminism(Determinism.NONDETERMINISTIC);
            			}
            			
            			FunctionDescriptor fd = tree.addFunction(schema, null, fMethod);
            			fd.setMetadataID(function);
            		}
            		trees[i++] = tree;
            	}
            }
            
            functionLibrary = new FunctionLibrary(SYSTEM_FUNCTION_MANAGER.getSystemFunctions(), trees);
        }
        return functionLibrary;
    }
    
    private ScalarFunction[] getScalarFunctions(ModelResource mr) {
        	ScalarFunctionFinder visitor = new ScalarFunctionFinder();
        	final int mode = ModelVisitorProcessor.MODE_VISIBLE_CONTAINMENTS;   // show only those objects visible to user
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor,mode);
            
            try {
				processor.walk(mr, ModelVisitorProcessor.DEPTH_INFINITE);
			} catch (ModelerCoreException ex) {
				UdfPlugin.UTIL.log(ex);
			}
            
            return visitor.getFunctions();
            
    }
    
    class ScalarFunctionFinder implements ModelVisitor {
    	
    	Collection<ScalarFunction> functions;

		@Override
		public boolean visit(EObject object) throws ModelerCoreException {
			// Tables are contained by Catalogs, Schemas and Resources
	        if (object instanceof ScalarFunction) {
	        	if( functions == null ) {
	        		functions = new ArrayList<ScalarFunction>();
	        	}
	        	functions.add((ScalarFunction)object);
	            return true;
	        }

	        return false;
		}

		@Override
		public boolean visit(Resource resource) throws ModelerCoreException {
			return true;
		}
		
		public ScalarFunction[] getFunctions() {
			return functions.toArray(new ScalarFunction[0]);
		}
    	
    }
}
