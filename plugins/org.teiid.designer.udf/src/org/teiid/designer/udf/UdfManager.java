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
import java.util.HashSet;
import java.util.List;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.ModelObjectCollector;
import org.teiid.designer.core.util.ModelVisitor;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.core.workspace.ModelObjectAnnotationHelper;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.core.workspace.ResourceChangeUtilities;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.function.FunctionPlugin;
import org.teiid.designer.metamodels.function.ScalarFunction;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.util.PushdownFunctionData;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerVersionListener;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;


/**
 * @since 8.0
 */
public final class UdfManager implements IResourceChangeListener {
    
    public static final String RELATIONAL_EXT_PROP_PREFIX = "relational:"; //$NON-NLS-1$
    public static final String FUNCTION_CATEGORY_PROP = "relational:function-category"; //$NON-NLS-1$
    public static final String JAVA_CLASS_PROP = "relational:java-class"; //$NON-NLS-1$
    public static final String JAVA_METHOD_PROP = "relational:java-method"; //$NON-NLS-1$

    private static UdfManager INSTANCE;
    
    private ModelObjectAnnotationHelper ANNOTATION_HELPER = new ModelObjectAnnotationHelper();
    
    private IFunctionLibrary systemFunctionLibrary;
    
    private IFunctionLibrary cachedFunctionLibrary;
   
    /**
     * A set of function models.
     * 
     * @since 6.0.0
     */
    private Set<ModelResource> functionModels = new HashSet<ModelResource>();

    private volatile boolean initialized;
    
    private boolean changed = false;

    private ITeiidServerVersionListener teiidServerVersionListener = new ITeiidServerVersionListener() {
        
        @Override
        public void serverChanged(ITeiidServer server) {
            // Nothing to do
        }

        @Override
        public void versionChanged(ITeiidServerVersion version) {
            systemFunctionLibrary = null;
            cachedFunctionLibrary = null;
        }
    };

    /**
     * Get the singleton instance of the manager
     * 
     * @return single instance of {@link UdfManager}
     */
    public static UdfManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UdfManager();
            try {
                INSTANCE.initialize();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        
        return INSTANCE;
    }
    
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
        ModelerCore.getWorkspace().addResourceChangeListener(this);
        
        // register as a listener for when the default teiid instance version is modified
        ModelerCore.addTeiidServerVersionListener(teiidServerVersionListener);

        // Register ModelResources that 1) are type function model or 2) are type relational, containing a procedure with function=true
        Collection<IFile> allResources = WorkspaceResourceFinderUtil.getProjectFileResources();
        try {
            for( IResource next : allResources ) {
                if(! ModelUtil.isModelFile(next, true)) {
                    continue;
                }
                ModelResource mr = ModelUtil.getModelResource((IFile)next, true);

                int theModelType = mr.getModelType().getValue();
                if( theModelType == ModelType.FUNCTION) {
                    registerFunctionModel(mr, false);
                } else if(theModelType == ModelType.PHYSICAL || theModelType==ModelType.VIRTUAL) {
                    Collection<Procedure> functionProcs = getFunctions(mr);
                    if(!functionProcs.isEmpty()) {
                        registerFunctionModel(mr,false);
                    }
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
     * @throws Exception if error occurs
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
    	unregisterProject(project);
    }
    
    private void modelProjectDeleted(IProject project) {
    	//System.out.println(" Model Project " + project.getName() + " is being DELETED");
        if (! project.isOpen()) {
            /*
             * project should already have been unregistered and we cannot
             * analysis project members due to the project being closed
             */
            return;
        }

    	unregisterProject(project);
    }

    /**
     * @param project
     */
    private void unregisterProject(IProject project) {
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
        ModelerCore.getWorkspace().removeResourceChangeListener(this);
        ModelerCore.removeTeiidServerVersionListener(teiidServerVersionListener);
    }

    /**
     * Get the default system function library
     * 
     * @return implementation of {@link IFunctionLibrary}
     */
    public IFunctionLibrary<IFunctionForm, IFunctionDescriptor> getSystemFunctionLibrary() {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        
    	if( this.systemFunctionLibrary == null ) {
    		this.systemFunctionLibrary = queryService.createFunctionLibrary(); 
    	}
    	
    	return this.systemFunctionLibrary;
    }
    
    /**
     * Get the FunctionLibrary
     * @return the FunctionLibrary
     */
    public synchronized IFunctionLibrary<IFunctionForm, IFunctionDescriptor> getFunctionLibrary() {
    	//System.out.println("UdfManger.getFunctionLibrary()");
    	if( !changed && this.cachedFunctionLibrary != null ) {
    		return this.cachedFunctionLibrary;
    	}
    	
    	List<FunctionMethodDescriptor> functionMethodDescriptors = new ArrayList<FunctionMethodDescriptor>();
    	
        for( ModelResource functionModelResource : functionModels ) {
        	ScalarFunction[] functions = getScalarFunctions(functionModelResource);
        	if( functions.length == 0 )
        	    continue;
        	    
        	IMarker[] markers = getMarkers(functionModelResource);
        		
        	String schema = FileUtils.getFilenameWithoutExtension(functionModelResource.getItemName());
        		
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
					
        	    Collection<FunctionParameterDescriptor> fParams = new ArrayList<FunctionParameterDescriptor>();
        			
        	    for( Object inputParam : function.getInputParameters() ) {
        	        if( inputParam instanceof org.teiid.designer.metamodels.function.FunctionParameter) {
        	            org.teiid.designer.metamodels.function.FunctionParameter param = (org.teiid.designer.metamodels.function.FunctionParameter)inputParam;
        	            fParams.add(new FunctionParameterDescriptor(param.getName(), param.getType()));
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
        	    FunctionParameterDescriptor outputParam = new FunctionParameterDescriptor(returnParamName, function.getReturnParameter().getType()); 
        			
        	    FunctionMethodDescriptor fMethodDescriptor = new FunctionMethodDescriptor(function,
        	                                                                                    function.getName(), 
        	                                                                                    description, 
        	                                                                                    function.getCategory(), 
        	                                                                                    function.getInvocationClass(), 
        	                                                                                    function.getInvocationMethod(),
        	                                                                                    fParams.toArray(new FunctionParameterDescriptor[0]),
        	                                                                                    outputParam,
        	                                                                                    schema);
        	    
        	    fMethodDescriptor.setPushDown(function.getPushDown().getLiteral());
        	    fMethodDescriptor.setDeterministic(function.isDeterministic());
        	    
        	    boolean varArgs = false;
        	    String propValue = FunctionPlugin.getExtensionProperty(function, "function:varargs"); //$NON-NLS-1$
        	    if( propValue != null && propValue.length() > 0 ) {
        	    	varArgs = Boolean.parseBoolean(propValue);
        	    }
        	    fMethodDescriptor.setVariableArgs(varArgs);
        	   
        	    functionMethodDescriptors.add(fMethodDescriptor);
        	}
        }
        
        // Now walk "Relational models" to search for new Procedures that have FUNCTION = true set
        // -- Source procedures will be the pushdown functions
        // -- View procedures will be the user-defined functions
        
        for( ModelResource sourceModel : getRelationalModels() ) {
        	
    		IMarker[] markers = getMarkers(sourceModel);
    		
    		String schema = FileUtils.getFilenameWithoutExtension(sourceModel.getItemName());
    		
    		for( Procedure procedure : getFunctions(sourceModel) ) {
    		    
    		    // Determine if working with a Source or View Model
    		    boolean isPhysical = ModelUtil.isPhysical(procedure);
    		    
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
				
    			Collection<FunctionParameterDescriptor> fParams = new ArrayList<FunctionParameterDescriptor>();
    			
    			for( ProcedureParameter inputParam : wrappedProcedure.getInputParameters() ) {
    				String dTypeName = ModelerCore.getModelEditor().getName(inputParam.getType());
					fParams.add(new FunctionParameterDescriptor(inputParam.getName(), dTypeName));
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
    			FunctionParameterDescriptor outputParam = new FunctionParameterDescriptor(returnParamName, dTypeName); 
    			
    			String category = wrappedProcedure.getCategory();
                String javaClass = wrappedProcedure.getJavaClass();
                String javaMethod = wrappedProcedure.getJavaMethod();

                boolean javaClassAndMethodEmpty = (javaClass==null && javaMethod==null) ? true : false;
                
    			// For source pushdown function, set the category name as the Model Name
                boolean isPushdown = false;
                if(isPhysical && javaClassAndMethodEmpty) {
                	isPushdown = true;
                    category = sourceModel.getItemName();
                    if( category.endsWith(StringConstants.DOT_XMI)) {
                        category = category.replaceAll(StringConstants.DOT_XMI, StringConstants.EMPTY_STRING);
                    }
                }
    			
    			FunctionMethodDescriptor fMethodDescriptor = new FunctionMethodDescriptor(procedure,
    			                                                                            wrappedProcedure.getName(), 
    			                                                                            description, 
    			                                                                            category, 
    			                                                                            javaClass, 
    			                                                                            javaMethod,
    			                                                                            fParams.toArray(new FunctionParameterDescriptor[0]),
    			                                                                            outputParam,
    			                                                                            schema);
    			
    			if( isPushdown) {
    				fMethodDescriptor.setPushDown(Boolean.toString(true));
    			}
    			fMethodDescriptor.setDeterministic(wrappedProcedure.isDeterministic());
    			
        	    boolean varArgs = false;
        	    String propValue = RelationalPlugin.getExtensionProperty(procedure, "relational:varargs"); //$NON-NLS-1$
        	    if( propValue != null && propValue.length() > 0 ) {
        	    	varArgs = Boolean.parseBoolean(propValue);
        	    }
        	    fMethodDescriptor.setVariableArgs(varArgs);
    			
    			functionMethodDescriptors.add(fMethodDescriptor);
    		}
        }
        
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        
        this.cachedFunctionLibrary = queryService.createFunctionLibrary(functionMethodDescriptors);

        this.changed = false;
        return this.cachedFunctionLibrary;
    }
    
    /*
     * Get all procedures from the supplied model, where FUNCTION=true
     */
    private Collection<Procedure> getFunctions(ModelResource relationalModel) {
    	Collection<Procedure> functions = new ArrayList<Procedure>();
    	
    	try {
        	
        	final ModelObjectCollector moc = new ModelObjectCollector(relationalModel.getEmfResource());
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
    
    private Collection<ModelResource> getRelationalModels() {
    	Collection<ModelResource> relationalMdls = new ArrayList<ModelResource>();
    	
    	try {
			ModelResource[] allModels = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace().getModelResources();
			
			for( ModelResource model : allModels ) {
				String uri = model.getPrimaryMetamodelUri();
				if( RelationalPackage.eNS_URI.equalsIgnoreCase(uri) ) {
				    relationalMdls.add(model);
				}
			}
			
		} catch (CoreException ex) {
            UdfPlugin.UTIL.log(ex);
		}
    	
    	
    	return relationalMdls;
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

		@SuppressWarnings("unused")
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

		@SuppressWarnings("unused")
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
    	String category;
    	String javaClass;
    	String javaMethod;
    	
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
    		    Properties relationalProps = ANNOTATION_HELPER.getProperties(procedure, UdfManager.RELATIONAL_EXT_PROP_PREFIX);
				Properties extProps = ANNOTATION_HELPER.getExtendedProperties(procedure);
				
				if( extProps != null && extProps.size() > 0 ) {
					Object determValue = extProps.get(PushdownFunctionData.DETERMINISTIC_PROPERTY_KEY);
					if( determValue != null ) {
						deterministic = Boolean.valueOf((String)determValue);
					}
				}
				if(relationalProps!=null && relationalProps.size()>0) {
					Object categoryValue = relationalProps.get(UdfManager.FUNCTION_CATEGORY_PROP);
					if(categoryValue!=null && !((String)categoryValue).isEmpty()) {
					    category = (String)categoryValue;
					}
                    Object javaClassValue = relationalProps.get(UdfManager.JAVA_CLASS_PROP);
                    if(javaClassValue!=null && !((String)javaClassValue).isEmpty()) {
                        javaClass = (String)javaClassValue;
                    }
                    Object javaMethodValue = relationalProps.get(UdfManager.JAVA_METHOD_PROP);
                    if(javaMethodValue!=null && !((String)javaMethodValue).isEmpty()) {
                        javaMethod = (String)javaMethodValue;
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
    	
    	/*
    	 * Get the category.  Will only be set for view function - will be null for source
    	 */
    	public String getCategory() {
    	    return category;
    	}
    	
        /*
         * Get the java class.  Will only be set for view function - will be null for source
         */
        public String getJavaClass() {
            return javaClass;
        }
        
        /*
         * Get the java method.  Will only be set for view function - will be null for source
         */
        public String getJavaMethod() {
            return javaMethod;
        }
    }
}
