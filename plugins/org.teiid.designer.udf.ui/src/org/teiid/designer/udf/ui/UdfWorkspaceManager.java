/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf.ui;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.teiid.designer.udf.UdfManager;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.function.FunctionPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

public class UdfWorkspaceManager {

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
                boolean started = ModelerCore.startTxn(false,
                                                       false,
                                                       UdfUiPlugin.UTIL.getString(prefix + "creatUdfModelTransaction"), this); //$NON-NLS-1$
                boolean succeeded = false;

                try {
                    ModelResource udfModelResource = ModelerCore.create(result[0]);
                    udfModelResource.getModelAnnotation().setPrimaryMetamodelUri(FunctionPackage.eNS_URI);
                    udfModelResource.getModelAnnotation().setModelType(ModelType.FUNCTION_LITERAL);
                    ModelUtilities.initializeModelContainers(udfModelResource,
                                                             UdfUiPlugin.UTIL.getString(prefix
                                                                                        + "creatModelContainersTransaction"), //$NON-NLS-1$
                                                             UdfWorkspaceManager.class);
                    udfModelResource.save(monitor, true);
                    succeeded = true;
                } catch (Exception e) {
                    final String msg = UdfUiPlugin.UTIL.getString(UdfUiPlugin.UTIL.getString(prefix + "errorCreatingModel")); //$NON-NLS-1$
                    UdfUiPlugin.UTIL.log(IStatus.ERROR, e, msg);
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
            UdfUiPlugin.UTIL.log(e.getTargetException());
            throw new IllegalStateException(e.getTargetException());
        }

        return result[0];
    }

    /**
     * @return the workspace UDF model (never <code>null</code>)
     * @since 6.0.0
     */
    public static IFile getUdfModel() {
        IProject project = UdfManager.INSTANCE.getUdfProject();
        IFile result = project.getFile(UdfManager.UDF_MODEL_NAME);

        // in theory this should not be necessary since the model should've been copied over from the install directory
        if ((result == null) || !result.exists()) {
            result = constructFunctionModel(project);
        }

        return result;
    }

    /**
     * Don't allow construction.
     * 
     * @since 6.0.0
     */
    private UdfWorkspaceManager() {
        // nothing to do
    }
}
