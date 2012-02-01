/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
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
import org.teiid.metadata.FunctionMethod.Determinism;
import org.teiid.metadata.FunctionParameter;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.function.FunctionTree;
import org.teiid.query.function.SystemFunctionManager;
import org.teiid.query.function.UDFSource;

import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.function.ScalarFunction;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.util.PushdownFunctionData;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelObjectCollector;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelObjectAnnotationHelper;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

public final class UdfManager implements IResourceChangeListener {
    
    public static final UdfManager INSTANCE = new UdfManager();
    
    public static final SystemFunctionManager SYSTEM_FUNCTION_MANAGER = new SystemFunctionManager();
    
    private static final ModelObjectAnnotationHelper ANNOTATION_HELPER = new ModelObjectAnnotationHelper();
    
    private FunctionLibrary systemFunctionLibrary;
    
    private FunctionLibrary cachedFunctionLibrary;
   
    /**
     * A set of function models.
     * 
     * @since 6.0.0
     */
    private Set<ModelResource> functionModels = new HashSet<ModelResource>();

    private volatile boolean initialized;
    
    private boolean changed = false;

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
            result = this.functionModels.add(modelResource); 
        }
        
        return result;
    }

    /**
     * Informs this manager that a function model has changed and that the cached function library needs to be reloaded.
     * 
     * @param modelResource the model resource that was changed
     * @param delete whether it was a delete or not
     */
    public void registerFunctionModel( ModelResource modelResource, boolean delete ) throws Exception {
        makeModification(modelResource, delete);
        changed = true;
    }
    
    /**
     * Informs this manager that a source model has changed and that the cached function library needs to be reloaded.
     * 
     * @param modelResource the model resource that was changed
     * @param delete whether it was a delete or not
     */
    public void notifySourceModelChanged( ModelResource modelResource, boolean delete ) {
        changed = true;
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
    	//System.out.println("UdfManger.getFunctionLibrary()");
    	if( !changed && this.cachedFunctionLibrary != null ) {
    		return this.cachedFunctionLibrary;
    	}
    	// Dynamically return a function library for each call rather than cache it here.
        Collection<FunctionTree> functionTrees = new ArrayList<FunctionTree>();
        
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
        		functionTrees.add(tree);
        	}
        }
        
        // Now walk "Source relational models" to search for new Procedures that have FUNCTION = true set
        
        for( ModelResource sourceModel : getSourceRelationalModels() ) {
        	
    		IMarker[] markers = getMarkers(sourceModel);
    		
    		String schema = FileUtils.getFilenameWithoutExtension(sourceModel.getItemName());
    		FunctionTree tree = new FunctionTree(schema, new UDFSource(Collections.EMPTY_LIST), false);
    		
    		for( Procedure procedure : getPushdownFunctions(sourceModel) ) {
    			// Also the Function's input parameters AND it's return parameter (if non-null) need to be error free
    			
    			ProcedureWrapper wrappedProcedure = new ProcedureWrapper(procedure);
    			
    			if( !isFunctionObjectErrorFree(wrappedProcedure.getReturnParameter(), markers, sourceModel) ||
    					wrappedProcedure.getReturnParameter() == null || 
    				!isFunctionObjectErrorFree(wrappedProcedure.getReturnParameter(), markers, sourceModel)) {
    				continue;
    			}
    			String description = null;
    			
    			try {
					description = ModelerCore.getModelEditor().getDescription(procedure);
				} catch (ModelerCoreException ex) {
					UdfPlugin.UTIL.log(ex);
				}
    			
				boolean functionPamameterHasError = false;
				
    			Collection<FunctionParameter> fParams = new ArrayList<FunctionParameter>();
    			
    			for( ProcedureParameter inputParam : wrappedProcedure.getInputParameters() ) {
    				String dTypeName = ModelerCore.getModelEditor().getName(inputParam.getType());
					fParams.add(new FunctionParameter(inputParam.getName(), dTypeName));
					// If any function parameter has an error don't add this
					if( !functionPamameterHasError && !isFunctionObjectErrorFree(inputParam, markers, sourceModel)){
						functionPamameterHasError = true;
					}
    			}
    			
    			if( functionPamameterHasError ) {
    				continue;
    			}
    			
    			String dTypeName = ModelerCore.getModelEditor().getName(wrappedProcedure.getReturnParameter().getType());
    			String returnParamName = ModelerCore.getModelEditor().getName(wrappedProcedure.getReturnParameter());
    			FunctionParameter outputParam = new FunctionParameter(returnParamName, dTypeName); 
    			
    			// Set the category name as the Model Name
    			String category = sourceModel.getItemName();
    			if( category.endsWith(".xmi")) { //$NON-NLS-1$
    				category = category.replaceAll(".xmi", StringUtilities.EMPTY_STRING); //$NON-NLS-1$
    			}
    			
    			FunctionMethod fMethod = 
    				new FunctionMethod(
    						wrappedProcedure.getName(), 
    						description, 
    						category, 
    						null, 
    						null, 
    						null,
    						fParams.toArray(new FunctionParameter[0]),
    						outputParam,
    					    false,
    						null
    						);
    			fMethod.setPushDown(Boolean.toString(true));
    			if( wrappedProcedure.isDeterministic() ) {
    				fMethod.setDeterminism(Determinism.DETERMINISTIC);
    			} else {
    				fMethod.setDeterminism(Determinism.NONDETERMINISTIC);
    			}
    			
    			FunctionDescriptor fd = tree.addFunction(schema, null, fMethod);
    			fd.setMetadataID(procedure);
    		}
    		functionTrees.add(tree);
        }
        //System.out.println("UdfManager.getFunctionLibrary() CREATED NEW FUNCTION LIBRARY");
        this.cachedFunctionLibrary = new FunctionLibrary(SYSTEM_FUNCTION_MANAGER.getSystemFunctions(), functionTrees.toArray(new FunctionTree[functionTrees.size()]));
        this.changed = false;
        return this.cachedFunctionLibrary;
    }
    
    private Collection<Procedure> getPushdownFunctions(ModelResource sourceModel) {
    	Collection<Procedure> functions = new ArrayList<Procedure>();
    	
    	try {
        	
        	final ModelObjectCollector moc = new ModelObjectCollector(sourceModel.getEmfResource());
            for( Object eObj : moc.getEObjects()){
            	if( eObj instanceof Procedure ) {
            		if( ((Procedure)eObj).isFunction()) {
            			functions.add((Procedure)eObj);
            		}
            	}
            }
		} catch (CoreException ex) {
            UdfPlugin.UTIL.log(ex);
		}
    	return functions;
    }
    
    private Collection<ModelResource> getSourceRelationalModels() {
    	Collection<ModelResource> sources = new ArrayList<ModelResource>();
    	
    	try {
			ModelResource[] allModels = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace().getModelResources();
			
			for( ModelResource model : allModels ) {
				String uri = model.getPrimaryMetamodelUri();
				if( RelationalPackage.eNS_URI.equalsIgnoreCase(uri) &&
					model.getModelType() == ModelType.PHYSICAL_LITERAL ) {
					sources.add(model);
				}
			}
			
		} catch (CoreException ex) {
            UdfPlugin.UTIL.log(ex);
		}
    	
    	
    	return sources;
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
			if( functions == null ) {
        		functions = new ArrayList<ScalarFunction>();
        	}
			return functions.toArray(new ScalarFunction[0]);
		}
    	
    }
    
    class ProcedureWrapper {
    	Procedure procedure;
    	ProcedureParameter returnParam;
    	Collection<ProcedureParameter> inputParams;
    	boolean deterministic = false;
    	
    	public ProcedureWrapper(Procedure procedure) {
    		super();
    		this.procedure = procedure;
    		init();
    	}
    	
    	private void init() {
    		inputParams = new ArrayList<ProcedureParameter>();
    		for( Object obj : procedure.getParameters() ) {
    			ProcedureParameter param = (ProcedureParameter)obj;
    			if( param.getDirection() == DirectionKind.IN_LITERAL ) {
    				inputParams.add(param);
    			} else if( param.getDirection() == DirectionKind.RETURN_LITERAL) {
    				returnParam = param;
    			}
    		}
    		try {
				Properties props = ANNOTATION_HELPER.getExtendedProperties(procedure);
				
				if( props != null && props.size() > 0 ) {
					Object determValue = props.get(PushdownFunctionData.DETERMINISTIC_PROPERTY_KEY);
					if( determValue != null ) {
						deterministic = Boolean.valueOf((String)determValue);
					}
				}
			} catch (ModelerCoreException ex) {
				UdfPlugin.UTIL.log(ex);
			}
    	}
    	
    	public ProcedureParameter getReturnParameter() {
    		return this.returnParam;
    	}
    	
    	public Collection<ProcedureParameter> getInputParameters() {
    		return this.inputParams;
    	}
    	
    	public String getName() {
    		return ModelerCore.getModelEditor().getName(procedure);
    	}
    	
    	public boolean isDeterministic() {
    		return this.deterministic;
    	}
    }
}
