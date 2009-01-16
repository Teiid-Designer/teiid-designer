package com.metamatrix.modeler.transformation.ui.udf;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.function.FunctionPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.NewModelProjectWorker;
import com.metamatrix.modeler.transformation.udf.UdfManager;
import com.metamatrix.modeler.transformation.ui.UiConstants;

public class UdfWorkspaceManager implements UiConstants {

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    /**
     * @param project the project where the UDF model should be created
     * @return the UDF model file
     * @throws IllegalStateException if the file cannot be constructed
     * @since 6.0.0
     */
    private static IFile constructFunctionModel( IProject project ) {
        final String prefix = I18nUtil.getPropertyPrefix(UdfWorkspaceManager.class);

        final IFile[] result = new IFile[] {project.getFile(UdfManager.UDF_MODEL_NAME)};

        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

            @Override
            public void execute( IProgressMonitor monitor ) {
                boolean started = ModelerCore.startTxn(false, false, Util.getString(prefix + "creatUdfModelTransaction"), this); //$NON-NLS-1$
                boolean succeeded = false;

                try {
                    ModelResource udfModelResource = ModelerCore.create(result[0]);
                    udfModelResource.getModelAnnotation().setPrimaryMetamodelUri(FunctionPackage.eNS_URI);
                    udfModelResource.getModelAnnotation().setModelType(ModelType.FUNCTION_LITERAL);
                    ModelUtilities.initializeModelContainers(udfModelResource,
                                                             Util.getString(prefix + "creatModelContainersTransaction"), //$NON-NLS-1$
                                                             UdfWorkspaceManager.class);
                    udfModelResource.save(monitor, true);
                    succeeded = true;
                } catch (Exception e) {
                    final String msg = Util.getString(Util.getString(prefix + "errorCreatingModel")); //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, msg);
                    throw new IllegalStateException(msg, e);
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

                monitor.done();
            }
        };

        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, false, op);
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
            UiConstants.Util.log(e.getTargetException());
            throw new IllegalStateException(e.getTargetException());
        }

        return result[0];
    }

    /**
     * @param create a flag indicating if the functions model should be created if not found
     * @return the workspace UDF model
     * @since 6.0.0
     */
    public static IFile getUdfModel( boolean create ) {
        IFile result = null;
        IProject project = getUdfProject(create);

        if (project != null) {
            result = project.getFile(UdfManager.UDF_MODEL_NAME);

            // in theory this should not be necessary since the model should've been copied over from the install directory
            if ((result == null) || !result.exists()) {
                result = constructFunctionModel(project);
            }
        }

        return result;
    }

    /**
     * @param create a flag indicating if the UDF project should be created if it does not exist
     * @return the workspace project where the UDF model is located (can be <code>null</code> if not wishing to create it)
     * @since 6.0.0
     */
    public static IProject getUdfProject( boolean create ) {
        IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(UdfManager.UDF_PROJECT_NAME);

        if ((proj != null) && !proj.exists()) {
            proj = null;
        }

        if ((proj == null) && create) {
            NewModelProjectWorker worker = new NewModelProjectWorker();
            IPath workspacePath = UdfManager.INSTANCE.getUdfModelPath();
            proj = worker.createNewProject(workspacePath, UdfManager.UDF_PROJECT_NAME, new NullProgressMonitor());
        }
        
        // make sure project is open
        if (proj != null) {
            try {
                proj.open(null);
                
                // make sure project is hidden
                ModelerCore.makeHidden(proj);
            } catch (CoreException e) {
                proj = null;
                UiConstants.Util.log(e);
            }
        }

        return proj;
    }
}
