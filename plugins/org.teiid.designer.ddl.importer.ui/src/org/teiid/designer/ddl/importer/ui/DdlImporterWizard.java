package org.teiid.designer.ddl.importer.ui;

import java.lang.reflect.InvocationTargetException;
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
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
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

    private void createDifferenceReport( final int totalWork ) throws InterruptedException, InvocationTargetException {
        if (importer.getChangeReport() != null) return;
        final List<String> msgs = new ArrayList<String>();
        new ProgressMonitorDialog(getShell()).run(true, true, new IRunnableWithProgress() {

            @Override
            public void run( final IProgressMonitor monitor ) {
                monitor.beginTask(DdlImporterUiI18n.IMPORTING_DDL_MSG, 100);
                importer.importDdl(msgs, monitor, totalWork);
                monitor.done();
            }
        });
        if (!msgs.isEmpty()
            && new MessageDialog(getShell(), DdlImporterUiI18n.CONFIRM_DIALOG_TITLE, null, DdlImporterUiI18n.CONTINUE_IMPORT_MSG,
                                 MessageDialog.CONFIRM, new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL},
                                 SWT.NONE) {

                @Override
                protected Control createCustomArea( final Composite parent ) {
                    final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(parent, SWT.BORDER | SWT.V_SCROLL
                                                                                                       | SWT.H_SCROLL);
                    list.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                    list.setItems(msgs.toArray(new String[msgs.size()]));
                    return list;
                }

                @Override
                protected final int getShellStyle() {
                    return SWT.SHEET;
                }
            }.open() != Window.OK) importer.undoImport();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.compare.ui.wizard.IDifferencingWizard#getDifferenceReports()
     */
    @Override
    public List<DifferenceReport> getDifferenceReports() {
        try {
            createDifferenceReport(100);
        } catch (final Exception error) {
            error.printStackTrace();
            WidgetUtil.showError(error);
        }
        return Collections.singletonList(importer.getChangeReport());
    }

    @Override
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
        
        IStructuredSelection finalSelection = selection;
        if( (finalSelection == null | finalSelection.isEmpty()) && !ModelerUiViewUtils.workspaceHasOpenModelProjects() ) {
        	IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		finalSelection = new StructuredSelection(newProject);
        	}
        }
        final IProject[] projects = DotProjectUtils.getOpenModelProjects();
        
        importer = new DdlImporter(projects);
        srcPg = new DdlImporterPage(importer, projects, finalSelection);
    }

    @Override
    public boolean performFinish() {
        try {
            createDifferenceReport(50);
            if (importer.getChangeReport() == null) return false;
            new ProgressMonitorDialog(getShell()).run(false, false, new IRunnableWithProgress() {

                @Override
                public void run( final IProgressMonitor monitor ) {
                    monitor.beginTask(DdlImporterUiI18n.IMPORTING_DDL_MSG, 100);
                    monitor.worked(50);
                    importer.save(monitor, 50);
                    monitor.done();
                }
            });
            // // Select model in workspace
            // UiUtil.getViewPart(UiConstants.Extensions.Explorer.VIEW).getSite().getSelectionProvider().setSelection(new
            // StructuredSelection(
            // importer.modelFile()));
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
