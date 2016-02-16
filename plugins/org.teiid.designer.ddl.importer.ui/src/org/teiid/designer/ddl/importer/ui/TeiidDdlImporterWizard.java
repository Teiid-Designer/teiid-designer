/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl.importer.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.ddl.importer.TeiidDDLConstants;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.ListMessageDialog;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.common.wizard.IPersistentWizardPage;
import org.teiid.designer.ui.common.wizard.NoOpenProjectsWizardPage;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;


/**
 * @author blafond
 *
 */
public class TeiidDdlImporterWizard extends AbstractWizard implements IImportWizard {

    DdlImporter importer;
    private TeiidDdlImporterPage srcPg;
    private DdlImportDifferencesPage diffPage;
    
    private Properties options;
    
    boolean noOpenProjects = false;

    /**
     * DdlImporterWizard constructor
     */
    public TeiidDdlImporterWizard() {
        super(DdlImporterUiPlugin.singleton(), DdlImporterUiI18n.TEIID_WIZARD_TITLE, DdlImporterUiPlugin.imageDescriptor("importWizard75x58.gif"));  //$NON-NLS-1$
        
        final IDialogSettings pluginSettings = DdlImporterUiPlugin.singleton().getDialogSettings();
        final String sectionName = DdlImporterWizard.class.getSimpleName();
        IDialogSettings section = pluginSettings.getSection(sectionName);
        if (section == null) section = pluginSettings.addNewSection(sectionName);
        setDialogSettings(section);
        
        this.options = new Properties();
        this.options.put(TeiidDDLConstants.DDL_IMPORT_FILTER_CONSTRAINTS, Boolean.toString(true));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        
        if( noOpenProjects ) {
        	addPage(NoOpenProjectsWizardPage.getStandardPage());
            return;
        } 
        addPage(srcPg);
        addPage(diffPage);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.0
     */
    @Override
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
        
        IStructuredSelection finalSelection = selection;
        if( (finalSelection == null | finalSelection.isEmpty()) && !ModelerUiViewUtils.workspaceHasOpenModelProjects() ) {
        	IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		finalSelection = new StructuredSelection(newProject);
        	} else {
        		noOpenProjects = true;
        	}
        }
        final Collection<IProject> projects = DotProjectUtils.getOpenModelProjects();
        IProject[] projectArray = projects.toArray(new IProject[0]);

        importer = new DdlImporter(projectArray);
        
        // First Page defines source DDL and target model
        srcPg = new TeiidDdlImporterPage(importer, projectArray, finalSelection, options);
        
        // Second Page for presentation of differences - allows user selection
        diffPage = new DdlImportDifferencesPage(importer, options);
        
    }
    
    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        boolean result = false;
        IWizardPage currentPage = getContainer().getCurrentPage();

        if (currentPage == this.diffPage) {
            result = currentPage.isPageComplete();
        } 
        return result;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        try {
        	// Use the importer to process the difference report, generating the model
            if (importer.getDifferenceReport() == null) return false;

            final Exception[] saveException = new Exception[1];
            new ProgressMonitorDialog(getShell()).run(false, false, new IRunnableWithProgress() {

                @Override
                public void run( final IProgressMonitor monitor ) {
                    monitor.beginTask(DdlImporterUiI18n.IMPORTING_DDL_MSG, 100);
                    monitor.worked(50);
                    try {
                        importer.save(monitor, 50);
                    } catch (Exception ex) {
                        saveException[0] = ex;
                    }
                    monitor.done();
                }
            });

            if (saveException[0] != null)
                throw saveException[0];

            // Show Warning Dialog if the importer finished, but with warnings
            IStatus importStatus = importer.getImportStatus();
            if(importStatus!=null && importStatus.getSeverity()==IStatus.WARNING) {
            	if(importStatus instanceof MultiStatus) {
                	final List<String> msgs = new ArrayList<String>(importStatus.getChildren().length);
                	for( int i=0; i<importStatus.getChildren().length; i++) {
                		msgs.add(importStatus.getChildren()[i].getMessage());
                	}
                	ListMessageDialog.openInformation(getShell(), DdlImporterUiI18n.IMPORT_COMPLETED_WITH_WARNINGS_TITLE, null, DdlImporterUiI18n.IMPORT_COMPLETED_WITH_WARNINGS_MESSAGE, msgs , null);
            	} else {
            		MessageDialog.openWarning(getShell(), DdlImporterUiI18n.IMPORT_COMPLETED_WITH_WARNINGS_TITLE, importStatus.getMessage());
            	}
            	return true;
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

