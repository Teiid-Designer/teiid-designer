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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.modeler.dqp.workspace.udf.IWorkspaceUdfPublisher;
import com.metamatrix.modeler.dqp.workspace.udf.UdfModelImporter;
import com.metamatrix.modeler.transformation.udf.UdfManager;
import com.metamatrix.modeler.transformation.ui.udf.UdfWorkspaceManager;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * The UDF model import wizard.
 * 
 * @since 6.0.0
 */
public final class UdfModelImportWizard extends AbstractWizard implements IImportWizard, IWorkspaceUdfPublisher {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

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
     * The file paths of the jars the workspace UDF model requires.
     * 
     * @since 6.0.0
     */
    private Set<String> udfJarPaths;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @since 6.0.0
     */
    public UdfModelImportWizard() {
        super(DqpUiPlugin.getDefault(), UTIL.getString(I18nUtil.getPropertyPrefix(UdfModelImportWizard.class) + "title"), //$NON-NLS-1$
              DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.UDF_IMPORT_WIZBAN));
        this.importer = new UdfModelImporter(this);
        this.selectionPage = new SourceSelectionPage(this.importer);
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
        addPage(this.selectionPage);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.workspace.udf.IWorkspaceUdfPublisher#addUdfJarFile(java.lang.String, java.io.InputStream)
     * @since 6.0.0
     */
    @Override
    public IStatus addUdfJarFile( String jarName,
                                  InputStream archiveStream ) {
        OutputStream out = null;
        File jarFile = null;

        try {
            jarFile = new File(System.getProperty("java.io.tmpdir"), jarName); //$NON-NLS-1$
            out = new FileOutputStream(jarFile);

            // Transfer bytes to the output file
            byte[] buf = new byte[1024];
            int len;

            while ((len = archiveStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            boolean success = DqpPlugin.getInstance().getExtensionsHandler().addUdfJars(this, new File[] {jarFile});

            if (success) {
                return Status.OK_STATUS;
            }

            return ModelerDqpUtils.createErrorStatus(I18nUtil.getPropertyPrefix(UdfModelImportWizard.class)
                                                     + "errorAddingImportArchive"); //$NON-NLS-1$
        } catch (Exception e) {
            UTIL.log(e);
            return ModelerDqpUtils.createErrorStatus(I18nUtil.getPropertyPrefix(UdfModelImportWizard.class)
                                                     + "errorAddingImportArchive"); //$NON-NLS-1$
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    UTIL.log(e);
                }
            }

            if (archiveStream != null) {
                try {
                    archiveStream.close();
                } catch (IOException e) {
                    UTIL.log(e);
                }
            }
        }
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

                UTIL.log(t);
                String msg = UTIL.getString(I18nUtil.getPropertyPrefix(UdfModelImportWizard.class) + "errorMsg"); //$NON-NLS-1$

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
     * @see com.metamatrix.modeler.dqp.workspace.udf.IWorkspaceUdfPublisher#replaceUdfModel(java.io.InputStream)
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
            UTIL.log(e);
            return ModelerDqpUtils.createErrorStatus(I18nUtil.getPropertyPrefix(UdfModelImportWizard.class)
                                                     + "errorReplacingUdfModel"); //$NON-NLS-1$
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    UTIL.log(e);
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    UTIL.log(e);
                }
            }
        }
    }

}
