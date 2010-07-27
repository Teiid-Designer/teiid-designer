package org.teiid.designer.ddl.importer.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.teiid.designer.ddl.importer.DdlImporter;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.ui.wizard.IDifferencingWizard;
import com.metamatrix.modeler.compare.ui.wizard.ShowDifferencesPage;
import com.metamatrix.modeler.internal.core.workspace.DotProjectUtils;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.IPersistentWizardPage;

/**
 * 
 */
public class DdlImporterWizard extends Wizard implements IDifferencingWizard {

    DdlImporter importer;
    private DdlImporterPage srcPg;

    /**
     * 
     */
    public DdlImporterWizard() {
        setWindowTitle(DdlImporterUiI18n.WIZARD_TITLE);
        setDefaultPageImageDescriptor(DdlImporterUiPlugin.imageDescriptor("importWizard75x58.gif")); //$NON-NLS-1$
        final IDialogSettings pluginSettings = DdlImporterUiPlugin.singleton().getDialogSettings();
        final String sectionName = DdlImporterWizard.class.getSimpleName();
        IDialogSettings section = pluginSettings.getSection(sectionName);
        if (section == null) section = pluginSettings.addNewSection(sectionName);
        setDialogSettings(section);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(srcPg);
        addPage(new ShowDifferencesPage(this));
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.compare.ui.wizard.IDifferencingWizard#getDifferenceReports()
     */
    @Override
    public List<DifferenceReport> getDifferenceReports() {
        final List<String> msgs = new ArrayList<String>();
        class Task implements IRunnableWithProgress {

            DifferenceReport report;

            @Override
            public void run( final IProgressMonitor monitor ) {
                monitor.beginTask(DdlImporterUiI18n.IMPORTING_DDL_MSG, 100);
                report = importer.importDdl2(msgs, monitor, 100);
                monitor.done();
            }
        }
        try {
            final Task task = new Task();
            new ProgressMonitorDialog(getShell()).run(true, true, task);
            return Collections.singletonList(task.report);
        } catch (final Exception error) {
            error.printStackTrace();
            WidgetUtil.showError(error);
        }
        return Collections.singletonList(null);
    }

    @Override
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
        final IProject[] projects = DotProjectUtils.getOpenModelProjects();
        importer = new DdlImporter(projects);
        srcPg = new DdlImporterPage(importer, projects, selection);
    }

    @Override
    public boolean performFinish() {
        try {
            final List<String> msgs = new ArrayList<String>();
            new ProgressMonitorDialog(getShell()).run(true, true, new IRunnableWithProgress() {

                @Override
                public void run( final IProgressMonitor monitor ) {
                    importer.importDdl(msgs, monitor);
                }
            });
            if (!msgs.isEmpty()) {
                if (new MessageDialog(getShell(), DdlImporterUiI18n.CONFIRM_DIALOG_TITLE, null,
                                      DdlImporterUiI18n.CONTINUE_IMPORT_MSG, MessageDialog.CONFIRM, new String[] {
                                          IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, SWT.NONE) {

                    @Override
                    protected Control createCustomArea( final Composite parent ) {
                        final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(parent, SWT.BORDER
                                                                                                           | SWT.V_SCROLL
                                                                                                           | SWT.H_SCROLL);
                        list.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                        list.setItems(msgs.toArray(new String[msgs.size()]));
                        return list;
                    }

                    @Override
                    protected final int getShellStyle() {
                        return SWT.SHEET;
                    }
                }.open() != Window.OK) return false;
                new ProgressMonitorDialog(getShell()).run(false, false, new IRunnableWithProgress() {

                    @Override
                    public void run( final IProgressMonitor monitor ) {
                        importer.save(monitor);
                    }
                });
                // Select model in workspace
                UiUtil.getViewPart(UiConstants.Extensions.Explorer.VIEW).getSite().getSelectionProvider().setSelection(new StructuredSelection(
                                                                                                                                               importer.modelFile()));
            }
        } catch (final InterruptedException error) {
            return false;
        } catch (final Exception error) {
            error.printStackTrace();
            WidgetUtil.showError(error);
            return false;
        }
        // Save user settings
        for (final IWizardPage pg : getPages())
            if (pg instanceof IPersistentWizardPage) ((IPersistentWizardPage)pg).saveSettings();
        return true;
    }
}
