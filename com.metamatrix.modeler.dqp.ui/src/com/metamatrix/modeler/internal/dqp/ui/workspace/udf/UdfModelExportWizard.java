/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.udf;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.workspace.udf.IWorkspaceUdfProvider;
import com.metamatrix.modeler.dqp.workspace.udf.UdfModelExporter;
import com.metamatrix.modeler.transformation.ui.udf.UdfWorkspaceManager;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * The UDF model export wizard.
 * 
 * @since 6.0.0
 */
public final class UdfModelExportWizard extends AbstractWizard implements IExportWizard, IWorkspaceUdfProvider {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The business object.
     * 
     * @since 6.0.0
     */
    private final UdfModelExporter exporter;

    /**
     * The file paths of the jars the UDF model requires.
     * 
     * @since 6.0.0
     */
    private Set<String> udfJarPaths;

    /**
     * Wizard page to allow user to choose the output file and location.
     * 
     * @since 6.0.0
     */
    private final IWizardPage page;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @since 6.0.0
     */
    public UdfModelExportWizard() {
        super(DqpUiPlugin.getDefault(), UTIL.getString(I18nUtil.getPropertyPrefix(UdfModelExportWizard.class) + "title"), //$NON-NLS-1$
              DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.UDF_EXPORT_WIZBAN));
        this.exporter = new UdfModelExporter(this);
        this.page = new TargetSelectionPage(this.exporter);
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     * @since 6.0.0
     */
    @Override
    public void addPages() {
        addPage(this.page);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#canFinish()
     * @since 6.0.0
     */
    @Override
    public boolean canFinish() {
        return this.exporter.canExport().isOK();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#finish()
     * @since 6.0.0
     */
    @Override
    public boolean finish() {
        // give the user a chance to save any dirty editors (one of which might the UDF model)
        UiUtil.getWorkbench().saveAllEditors(true);

        final UdfModelExporter exporter = this.exporter;

        final IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                IStatus status = exporter.doExport(monitor);

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

                UTIL.log(t);
                String msg = UTIL.getString(I18nUtil.getPropertyPrefix(UdfModelExportWizard.class) + "errorMsg"); //$NON-NLS-1$

                // show user error status
                WidgetUtil.show(new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, t));
            }
        } finally {
            dialog.getProgressMonitor().done();
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.workspace.udf.IWorkspaceUdfProvider#getUdfJarFilePaths()
     * @since 6.0.0
     */
    @Override
    public Set<String> getUdfJarFilePaths() {
        if (this.udfJarPaths == null) {
            List<File> jarFiles = DqpPlugin.getInstance().getExtensionsHandler().getUdfJarFiles();
            this.udfJarPaths = new HashSet<String>(jarFiles.size());

            for (File jar : jarFiles) {
                this.udfJarPaths.add(jar.getAbsolutePath());
            }
        }

        return this.udfJarPaths;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.workspace.udf.IWorkspaceUdfProvider#getUdfModelPath()
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
     */
    @Override
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        // nothing to do
    }

}
