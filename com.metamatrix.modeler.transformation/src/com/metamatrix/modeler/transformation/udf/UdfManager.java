package com.metamatrix.modeler.transformation.udf;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.modeler.transformation.udf.UdfModelEvent.Type;
import com.metamatrix.query.function.FunctionLibraryManager;
import com.metamatrix.query.function.UDFSource;

public final class UdfManager implements IResourceChangeListener, UdfModelListener {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    /**
     * The name of the default UDF model.
     * 
     * @since 6.0.0
     */
    public static final String UDF_MODEL_NAME = ModelerCore.UDF_MODEL_NAME;

    /**
     * The name of the project/folder where the default UDF model is contained.
     * 
     * @since 6.0.0
     */
    public static final String UDF_PROJECT_NAME = ModelerCore.UDF_PROJECT_NAME;

    // ===========================================================================================================================
    // Class Fields
    // ===========================================================================================================================

    public static final UdfManager INSTANCE = new UdfManager();

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private ListenerList changeListeners;
    /**
     * A collection of function models.
     * 
     * @since 6.0.0
     */
    private Map<URL, UDFSource> functionModels;

    /**
     * The install location of the <code>modeler.transformation</code> plugin.
     * 
     * @since 6.0.0
     */
    private IPath installPath;

    /**
     * The workspace location of the <code>modeler.transformation</code> plugin.
     * 
     * @since 6.0.0
     */
    private IPath runtimePath;

    /**
     * Used in non-Eclipse environments to identify the install location of the <code>modeler.transformation</code> plugin.
     * <strong>To be used for testing purposes only.</strong>
     * 
     * @since 6.0.0
     */
    public String testInstallPath;

    /**
     * Used in non-Eclipse environments to identify the workspace location of the <code>modeler.transformation</code> plugin.
     * <strong>To be used for testing purposes only.</strong>
     * 
     * @since 6.0.0
     */
    public String testRuntimePath;

    /**
     * The location of the default UDF model being used.
     * 
     * @since 6.0.0
     */
    private IPath udfModelPath;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Don't allow public construction.
     * 
     * @since 6.0.0
     */
    private UdfManager() {
        this.changeListeners = new ListenerList(ListenerList.IDENTITY);
        this.functionModels = new HashMap<URL, UDFSource>();
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

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
            TransformationPlugin.Util.log(e);
        }

        return null;
    }

    private IPath getPluginInstallPath() throws IOException {
        if (this.installPath == null) {
            if (this.testInstallPath == null) {
                URL url = FileLocator.find(TransformationPlugin.getDefault().getBundle(), new Path(""), null); //$NON-NLS-1$
                url = FileLocator.toFileURL(url);
                this.installPath = new Path(url.getFile());
            } else {
                this.installPath = new Path(this.testInstallPath);
            }
        }

        return (IPath)this.installPath.clone();
    }

    private IPath getRuntimePath() {
        if (this.runtimePath == null) {
            if (this.testRuntimePath == null) {
                this.runtimePath = Platform.getStateLocation(TransformationPlugin.getDefault().getBundle());
            } else {
                this.runtimePath = new Path(this.testRuntimePath);
            }
        }

        return (IPath)this.runtimePath.clone();
    }

    private File getUdfModel() {
        return getUdfModelPath().append(UDF_MODEL_NAME).toFile();
    }

    /**
     * @return the location of the default UDF model used at runtime
     * @since 6.0.0
     */
    public IPath getUdfModelPath() {
        if (this.udfModelPath == null) {
            this.udfModelPath = getRuntimePath().append(UDF_PROJECT_NAME);

            // make sure it exists
            File dir = new File(this.udfModelPath.toOSString());

            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        return (IPath)this.udfModelPath.clone();
    }

    /**
     * Ensures the UDF model is located in the workspace and the FunctionLibraryManager has been initialized using the default UDF
     * model. <strong>This must be called before any other method can be used.</strong>
     * 
     * @throws Exception if there was a problem with the UDF model or library manager
     * @since 6.0.0
     */
    public void initialize() throws Exception {
        File udfModel = getUdfModel();

        // if necessary copy over default UDF model from the install directory
        if (!udfModel.exists()) {
            IPath udfDir = getPluginInstallPath().append(UDF_PROJECT_NAME);
            File originalUdfModel = udfDir.append(UDF_MODEL_NAME).toFile();
            FileUtils.copy(originalUdfModel.getAbsolutePath(), udfModel.getAbsolutePath());
        }

        // tell listeners about the default UDF model
        notifyListeners(udfModel, Type.NEW);

        // register to receive resource events so that we can notify listeners when a function model changes
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    private void notifyListeners( File udfModel,
                                  Type type ) throws Exception {
        Object[] listeners = this.changeListeners.getListeners();
        UdfModelEvent event = new UdfModelEvent(udfModel.toURI().toURL(), type);

        for (Object listener : listeners) {
            try {
                ((UdfModelListener)listener).processEvent(event);
            } catch (Exception e) {
                String msg = TransformationPlugin.Util.getString(I18nUtil.getPropertyPrefix(UdfManager.class)
                                                                 + "exceptionInHander", listener.getClass()); //$NON-NLS-1$
                TransformationPlugin.Util.log(IStatus.ERROR, e, msg);
                this.changeListeners.remove(listener);
            }
        }

        // now handle it ourselves (this is a placeholder for when validation is done on the modeler-side and not the
        // federate-side)
//        processEvent(event);
    }

    @Override
    public void processEvent( UdfModelEvent event ) {
        URL url = event.getUrl();
        UDFSource udfSource = this.functionModels.get(url);
        boolean remove = (udfSource != null);
        boolean add = !event.isDeleted();

        if (remove) {
            this.functionModels.remove(url);

            final boolean startedTxn = ModelerCore.startTxn(false, false, null, this);

            try {
                FunctionLibraryManager.deregisterSource(udfSource);
            } finally {
                if (startedTxn) {
                    ModelerCore.commitTxn();
                }
            }
        }

        if (add) {
            final boolean startedTxn = ModelerCore.startTxn(false, false, null, this);

            try {
                udfSource = new UDFSource(url);
                FunctionLibraryManager.registerSource(udfSource);
                this.functionModels.put(url, udfSource);
            } catch (Exception e) {
                TransformationPlugin.Util.log(e);
            } finally {
                if (startedTxn) {
                    ModelerCore.commitTxn();
                }
            }
        }
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
        if (ResourceChangeUtilities.isPostChange(event)) {
            if (ResourceChangeUtilities.isChanged(event.getDelta())) {
                ModelEditor modelEditor = ModelerCore.getModelEditor();
                IResourceDelta[] children = event.getDelta().getAffectedChildren();

                for (int i = 0; i < children.length; i++) {
                    IResource resource = children[i].getResource();

                    // make sure we are in the UDF project
                    if ((resource != null) && (resource instanceof IProject) && (resource.getParent() instanceof IWorkspaceRoot)
                        && resource.getName().equals(UdfManager.UDF_PROJECT_NAME)) {
                        IResourceDelta[] udfFiles = children[i].getAffectedChildren();

                        for (int j = 0; j < udfFiles.length; j++) {
                            resource = udfFiles[j].getResource();

                            try {
                                // make sure we're looking at a UDF model
                                if ((resource != null) && (resource instanceof IFile)) {
                                    ModelResource modelResource = modelEditor.findModelResource((IFile)resource);

                                    // model resource will be null for non-model files (like .project)
                                    if ((modelResource != null) && (modelResource.getItemType() == ModelType.FUNCTION)) {
                                        // only update if the UDF model was saved
                                        if ((udfFiles[j].getFlags() & IResourceDelta.CONTENT) != 0) {
                                            registerFunctionModel(modelResource); // inform listeners
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                TransformationPlugin.Util.log(e);
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
}
