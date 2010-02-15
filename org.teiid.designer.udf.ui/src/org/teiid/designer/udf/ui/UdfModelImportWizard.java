/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.teiid.designer.udf.IWorkspaceUdfPublisher;
import org.teiid.designer.udf.UdfManager;
import org.teiid.designer.udf.UdfModelImporter;
import org.teiid.designer.udf.UdfPlugin;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * The UDF model import wizard.
 * 
 * @since 6.0.0
 */
public final class UdfModelImportWizard extends AbstractWizard implements IImportWizard, IWorkspaceUdfPublisher {

    /**
     * The business object.
     * 
     * @since 6.0.0
     */
    private final UdfModelImporter importer;

    /**
     * Wizard page to allow the user to choose which file to import.
     * 
     * @since 6.0.0
     */
    private final SourceSelectionPage selectionPage;

    /**
     * @since 6.0.0
     */
    public UdfModelImportWizard() {
        super(UdfUiPlugin.getInstance(), UdfUiPlugin.UTIL.getString(I18nUtil.getPropertyPrefix(UdfModelImportWizard.class)
                                                                    + "title"), //$NON-NLS-1$
              UdfUiPlugin.getInstance().getImageDescriptor(UdfUiPlugin.UDF_IMPORT_WIZBAN));
        this.importer = new UdfModelImporter(this);
        this.selectionPage = new SourceSelectionPage(this.importer);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     * @since 6.0.0
     */
    @Override
    public void addPages() {
        addPage(this.selectionPage);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#canFinish()
     * @since 6.0.0
     */
    @Override
    public boolean canFinish() {
        return this.importer.canImport().isOK();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#finish()
     * @since 6.0.0
     */
    @Override
    public boolean finish() {
        final UdfModelImporter importer = this.importer;

        final IRunnableWithProgress op = new WorkspaceModifyOperation() {
            @Override
            protected void execute( IProgressMonitor monitor ) throws InvocationTargetException {
                IStatus status = importer.doImport(monitor);

                if (!status.isOK()) {
                    throw new InvocationTargetException(new CoreException(status));
                }
            }
        };

        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());

        try {
            dialog.run(true, true, op);
        } catch (Throwable t) {
            if (t instanceof InterruptedException) {
                // user cancel so nothing to do
            } else {
                // unexpected exception occurred
                if (t instanceof InvocationTargetException) {
                    t = ((InvocationTargetException)t).getTargetException();
                }

                UdfUiPlugin.UTIL.log(t);
                String msg = UdfUiPlugin.UTIL.getString(I18nUtil.getPropertyPrefix(UdfModelImportWizard.class) + "errorMsg"); //$NON-NLS-1$

                // show user error status
                WidgetUtil.show(new Status(IStatus.ERROR, UdfUiPlugin.PLUGIN_ID, IStatus.OK, msg, t));
            }
        } finally {
            dialog.getProgressMonitor().done();
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.udf.IWorkspaceUdfProvider#getUdfModelPath()
     * @since 6.0.0
     */
    @Override
    public String getUdfModelPath() {
        return UdfWorkspaceManager.getUdfModel().getLocation().toOSString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 6.0.0
     */
    @Override
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.udf.IWorkspaceUdfPublisher#replaceUdfModel(java.io.InputStream)
     * @since 6.0.0
     */
    @Override
    public IStatus replaceUdfModel( InputStream stream ) {
        OutputStream out = null;
        File udfModelFile = null;

        try {
            udfModelFile = new File(UdfManager.INSTANCE.getUdfModelPath().toOSString(), ModelerCore.UDF_MODEL_NAME);
            out = new FileOutputStream(udfModelFile);

            // Transfer bytes to the output file
            byte[] buf = new byte[1024];
            int len;

            while ((len = stream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            // need to refresh workspace here
            IProject udfProject = UdfManager.INSTANCE.getUdfProject();
            udfProject.refreshLocal(IResource.DEPTH_INFINITE, null);

            return Status.OK_STATUS;
        } catch (Exception e) {
            UdfUiPlugin.UTIL.log(e);
            return UdfPlugin.createErrorStatus(I18nUtil.getPropertyPrefix(UdfModelImportWizard.class) + "errorReplacingUdfModel"); //$NON-NLS-1$
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    UdfUiPlugin.UTIL.log(e);
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    UdfUiPlugin.UTIL.log(e);
                }
            }
        }
    }

}
