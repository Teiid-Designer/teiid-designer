/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.function.FunctionTree;
import org.teiid.query.function.SystemFunctionManager;
import org.teiid.query.function.UDFSource;
import org.teiid.query.function.metadata.FunctionMetadataReader;
import com.metamatrix.core.modeler.util.FileUtils;
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

    private static class UdfInfo {
        String modelName;
        UDFSource source;
        
        public UdfInfo(String modelname) {
            this.modelName = modelname;
        }
    }
    
    private FunctionLibrary functionLibrary;
   
    /**
     * A collection of function models.
     * 
     * @since 6.0.0
     */
    private Map<URL, UdfInfo> functionModels = new HashMap<URL, UdfInfo>();

    private volatile boolean initialized;

    /**
     * Don't allow public construction.
     * 
     * @since 6.0.0
     */
    private UdfManager() {
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
        Resource r = ModelerCore.getModelContainer().getResource(uri, false);
        if (r instanceof EmfResource) {
            return (EmfResource)r;
        }
        return null;
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
                    registerFunctionModel(mr.getPath(), false);
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

    private synchronized boolean makeModification( File udfModel, String modelName,
                                  boolean deleted ) throws Exception {
        // let query engine know of the change so that functions can be available for modeling
        URL url = udfModel.toURI().toURL();
        boolean result = false;
        if (deleted) {
            result = this.functionModels.remove(url) != null;
        } else {
            result = true;
            this.functionModels.put(url, new UdfInfo(modelName));
        }
        
        if (result) {
            functionLibrary = null;
        }
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
    public void registerFunctionModel( IPath udfModel, boolean delete ) throws Exception {
        File f = new File(udfModel.toOSString());
        
        if (!delete && !isFunctionModelValid(f)) {
            return;
        }
        
        makeModification(f, FileUtils.getFilenameWithoutExtension(udfModel.lastSegment()), delete);
    }

    @Override
    public void resourceChanged( IResourceChangeEvent event ) {
        if (!initialized || !ResourceChangeUtilities.isPostChange(event)) {
            return;
        }

        ModelEditor modelEditor = ModelerCore.getModelEditor();
        IResourceDelta[] children = event.getDelta().getAffectedChildren();

        for (int i = 0; i < children.length; i++) {
            IResource resource = children[i].getResource();

            // Check to see if UDF model
            
            // make sure we are in the UDF project
            if (resource == null || !(resource instanceof IProject) || !(resource.getParent() instanceof IWorkspaceRoot)) {
                continue;
            }
            IResourceDelta[] udfFiles = children[i].getAffectedChildren();

            for (int j = 0; j < udfFiles.length; j++) {
                resource = udfFiles[j].getResource();

                if (!(resource instanceof IFile)) {
                    continue;
                }

                try {
                    // make sure we're looking at a UDF model
                    ModelResource modelResource = modelEditor.findModelResource((IFile)resource);
                    IPath path = ((IFile)resource).getRawLocation();
                    
                    // model resource will be null for non-model files (like .project)
                    if (modelResource == null) {
                        if (path != null && ResourceChangeUtilities.isRemoved(udfFiles[j])) {
                            registerFunctionModel(path, true);
                        }
                    } else if (modelResource.getModelType().getValue() == ModelType.FUNCTION && (ResourceChangeUtilities.isContentChanged(udfFiles[j]) || ResourceChangeUtilities.isAdded(udfFiles[j]))) {
                        registerFunctionModel(path, false);
                    }
                } catch (Exception e) {
                    UdfPlugin.UTIL.log(e);
                }
            }
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

    public synchronized FunctionLibrary getFunctionLibrary() {
        if (functionLibrary == null) {
            FunctionTree[] trees = new FunctionTree[functionModels.size()];
            int i = 0;
            for (Map.Entry<URL, UdfInfo> entry : functionModels.entrySet()) {
                try {
                    UdfInfo value = entry.getValue();
                    if (value.source == null) {
                        Collection functionMethods = FunctionMetadataReader.loadFunctionMethods(entry.getKey().openStream());
                        value.source = new UDFSource(functionMethods);
                    }
                    trees[i++] = new FunctionTree(value.modelName, value.source);
                } catch (Exception e) {
                    UdfPlugin.UTIL.log(e);
				}
            }
            functionLibrary = new FunctionLibrary(SYSTEM_FUNCTION_MANAGER.getSystemFunctions(), trees);
        }
        return functionLibrary;
    }
}
