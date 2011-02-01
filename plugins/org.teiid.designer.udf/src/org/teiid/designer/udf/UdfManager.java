/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.teiid.designer.udf.UdfModelEvent.Type;
import org.teiid.metadata.FunctionMethod;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.function.FunctionTree;
import org.teiid.query.function.SystemFunctionManager;
import org.teiid.query.function.UDFSource;
import org.teiid.query.function.metadata.FunctionMetadataReader;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.ModelType;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.validation.Validator;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

public final class UdfManager implements IResourceChangeListener {

    public static final UdfManager INSTANCE = new UdfManager();
    
    public static final SystemFunctionManager SYSTEM_FUNCTION_MANAGER = new SystemFunctionManager();

    private FunctionLibrary functionLibrary;

    private ListenerList changeListeners;
    /**
     * A collection of function models.
     * 
     * @since 6.0.0
     */
    private Map<URL, UDFSource> functionModels;

    private boolean initialized;

    /**
     * Don't allow public construction.
     * 
     * @since 6.0.0
     */
    private UdfManager() {
        this.changeListeners = new ListenerList(ListenerList.IDENTITY);
        this.functionModels = new HashMap<URL, UDFSource>();
    }

    public Set<URL> getUDFs() {
        return this.functionModels.keySet();
    }

    /**
     * @param listener the listener being registered to receive an event whenever a UDF model is changed
     * @since 6.0.0
     */
    public void addChangeListener( UdfModelListener listener ) {
        this.changeListeners.add(listener);

        // notify new listener of the registered UDF models
        for (URL url : this.functionModels.keySet()) {
            listener.processEvent(new UdfModelEvent(url, Type.NEW));
        }
    }

    private UDFSource findUdfSource( File functionModel ) {
        URL url;
        try {
            url = functionModel.toURI().toURL();
            return this.functionModels.get(url);
        } catch (MalformedURLException e) {
            UdfPlugin.UTIL.log(e);
        }

        return null;
    }

    /**
     * @param udfModel the UDF model whose model resource is being requested
     * @return the UDF model workspace resource
     * @throws CoreException if there is a problem obtaining the resource
     * @since 6.0.0
     */
    private EmfResource getFunctionModelResource( File udfModel ) throws CoreException {
        String path = udfModel.getAbsolutePath();
        URI uri = URI.createFileURI(path);
        return (EmfResource)ModelerCore.getModelContainer().getResource(uri, false);
    }
    
    private void loadWorkspaceFunctionModels() {
    	Collection<IResource> allResources = WorkspaceResourceFinderUtil.getAllWorkspaceResources();
		try {
			for( IResource next : allResources ) {
				if( ModelUtil.isModelFile(next, true)) {
					ModelResource mr = ModelUtil.getModelResource((IFile)next, true);

					if( mr.getModelType().getValue() == ModelType.FUNCTION) {
				        IPath path = mr.getUnderlyingResource().getLocation();
				        registerFunctionModel(new File(path.toOSString()));
					} 
			    }
			}
		} catch (Exception err) {
			UdfPlugin.UTIL.log(err);
		}
		
    }

    /**
     * Ensures the UDF model is located in the workspace and the FunctionLibraryManager has been initialized using the default UDF
     * model. <strong>This must be called before any other method can be used.</strong>
     * 
     * @throws Exception if there was a problem with the UDF model or library manager
     * @since 6.0.0
     */
    public void initialize() throws Exception {

        // register to receive resource events so that we can notify listeners when a function model changes
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        
        loadWorkspaceFunctionModels();
        
        initialized = true;
    }

    /**
     * @param udfModel the UDF model being checked
     * @return <code>true</code> if the workspace function model does not have validation errors
     * @throws CoreException if there is a problem obtaining the EMF resource of the workspace function model
     * @since 6.0.0
     */
    private boolean isFunctionModelValid( File udfModel ) throws CoreException {
        ValidationContext context = new ValidationContext();

        context.setResourceContainer(ModelerCore.getModelContainer());
        EmfResource rsrc = getFunctionModelResource(udfModel);

        if (rsrc == null) {
            String msg = UdfPlugin.UTIL.getString(I18nUtil.getPropertyPrefix(UdfManager.class) + "nullFunctionModelResource", udfModel.getAbsolutePath()); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, UdfPlugin.PLUGIN_ID, msg);
            throw new CoreException(status);
        }

        // run the model through the validator to see if there are any errors
        Validator.validate(null, rsrc, context);
        List<ValidationResult> results = context.getValidationResults();

        // if one of the results has an error then the model is not valid
        if (results != null) {
            for (ValidationResult result : results) {
                for (ValidationProblem problem : result.getProblems()) {
                    if (problem.getSeverity() == IStatus.ERROR) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void notifyListeners( File udfModel,
                                  Type type ) throws Exception {
        // don't notify if model is added or changed and is not valid
        if (type != Type.DELETED && !isFunctionModelValid(udfModel)) {
            return;
        }

        UdfModelEvent event = new UdfModelEvent(udfModel.toURI().toURL(), type);

        // let query engine know of the change so that functions can be available for modeling
        udfModelChanged(event);

        if (this.changeListeners.size() != 0) {
            Object[] listeners = this.changeListeners.getListeners();

            for (Object listener : listeners) {
                try {
                    ((UdfModelListener)listener).processEvent(event);
                } catch (Exception e) {
                    String msg = UdfPlugin.UTIL.getString(I18nUtil.getPropertyPrefix(UdfManager.class) + "exceptionInHander", listener.getClass()); //$NON-NLS-1$
                    UdfPlugin.UTIL.log(IStatus.ERROR, e, msg);
                    this.changeListeners.remove(listener);
                }
            }
        }
    }

    private void udfModelChanged( UdfModelEvent event ) {
        URL url = event.getUrl();
        UDFSource udfSource = this.functionModels.get(url);
        boolean remove = (udfSource != null);
        boolean add = !event.isDeleted();

        if (remove) {
            this.functionModels.remove(url);
        }

        if (add) {
            try {
                this.functionModels.put(url, null);
            } catch (Exception e) {
                UdfPlugin.UTIL.log(e);
            }
        }
        
        functionLibrary = null;
    }

    /**
     * Informs the Query Engine's Function Library that the specified function model should be loaded. This method should be only
     * once for a model. When the model is changed and saved the changes are broadcast via {@link IResourceChangeEvent}s.
     * 
     * @param udfModel the model being registered
     * @throws Exception if there is a problem loading the file
     */
    public void registerFunctionModel( File udfModel ) throws Exception {
        Type type = Type.CHANGED;

        if (findUdfSource(udfModel) == null) {
            type = Type.NEW;
        }

        notifyListeners(udfModel, type);
    }

    /**
     * Informs the Query Engine's Function Library that the specified function model should be loaded. This method should be only
     * once for a model. When the model is changed and saved the changes are broadcast via {@link IResourceChangeEvent}s.
     * 
     * @param udfModel the model being registered
     * @throws Exception if there is a problem loading the file
     * @see #addFunctionModel(File)
     */
    public void registerFunctionModel( ModelResource udfModel ) throws Exception {
        IPath path = udfModel.getUnderlyingResource().getLocation();
        registerFunctionModel(new File(path.toOSString()));
    }

    @Override
    public void resourceChanged( IResourceChangeEvent event ) {
        if (!initialized) {
            return;
        }

        if (ResourceChangeUtilities.isPostChange(event)) {
            if (ResourceChangeUtilities.isChanged(event.getDelta())) {
                ModelEditor modelEditor = ModelerCore.getModelEditor();
                IResourceDelta[] children = event.getDelta().getAffectedChildren();

                for (int i = 0; i < children.length; i++) {
                    IResource resource = children[i].getResource();

                    // Check to see if UDF model
                    
                    // make sure we are in the UDF project
                    if ((resource != null) && (resource instanceof IProject) && (resource.getParent() instanceof IWorkspaceRoot)) {
                        IResourceDelta[] udfFiles = children[i].getAffectedChildren();

                        for (int j = 0; j < udfFiles.length; j++) {
                            resource = udfFiles[j].getResource();

                            try {
                                // make sure we're looking at a UDF model
                                if ((resource != null) && (resource instanceof IFile)) {
                                    ModelResource modelResource = modelEditor.findModelResource((IFile)resource);

                                    // model resource will be null for non-model files (like .project)
                                    if ((modelResource != null) && (modelResource.getModelType().getValue() == ModelType.FUNCTION)) {
                                        // only update if the UDF model was saved
                                        // DQP throws an exception if you send it a model that has errors.
                                        if ((udfFiles[j].getFlags() & IResourceDelta.CONTENT) != 0) {
                                            registerFunctionModel(modelResource); // inform listeners
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                UdfPlugin.UTIL.log(e);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param listener the listener being removed from receiving an event whenever a UDF model is changed
     * @since 6.0.0
     */
    public void removeChangeListener( UdfModelListener listener ) {
        this.changeListeners.remove(listener);
    }

    /**
     * @param udfModel the model being removed from the Function Library
     * @throws Exception if there is a problem loading the file as a function model
     * @since 6.0.0
     */
    public boolean removeFunctionModel( File udfModel ) throws Exception {
        UDFSource udfSource = findUdfSource(udfModel);

        if (udfSource != null) {
            notifyListeners(udfModel, Type.DELETED);
            return true;
        }

        return false;
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

    public FunctionLibrary getFunctionLibrary() {
        if (functionLibrary == null) {
            Set<URL> urls = UdfManager.INSTANCE.getUDFs();
            Collection<FunctionMethod> methods = new ArrayList<FunctionMethod>();
            if (!urls.isEmpty()) {
                for (URL url : urls) {
                    try {
                    	Collection functionMethods = FunctionMetadataReader.loadFunctionMethods(url.openStream());
                        methods.addAll(functionMethods);
                    } catch (IOException e) {
                        //
                    } catch (JAXBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
            functionLibrary = new FunctionLibrary(SYSTEM_FUNCTION_MANAGER.getSystemFunctions(),
                                                  new FunctionTree(new UDFSource(methods)));
        }
        return functionLibrary;
    }
}
