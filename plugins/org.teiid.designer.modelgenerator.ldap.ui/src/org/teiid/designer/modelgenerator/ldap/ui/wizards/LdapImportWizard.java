/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants.Images;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiPlugin;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.columns.LdapColumnsPage;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.definition.LdapDefinitionPage;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.table.LdapTablesPage;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.common.wizard.NoOpenProjectsWizardPage;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.IPropertiesContext;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;

/**
 * Creates a wizard for generating relational entities from an LDAP Service.
 */
public class LdapImportWizard extends AbstractWizard
    implements IImportWizard, IPropertiesContext, ModelGeneratorLdapUiConstants.Images {

    /**
     * Wizard banner image descriptor
     */
    public static final ImageDescriptor BANNER = ModelGeneratorLdapUiPlugin.getDefault().getImageDescriptor(WIZARD_BANNER);

    /**
     * Title image descriptor
     */
    private static final ImageDescriptor LDAP_ICON = ModelGeneratorLdapUiPlugin.getDefault().getImageDescriptor(Images.IMPORT_LDAP_ICON);

    /** This manager interfaces with the relational model from ldap generator */
    private LdapImportWizardManager importManager = new LdapImportWizardManager();

    /* The page where the LDAP parameters are set and the source model is selected. */
    private LdapDefinitionPage ldapDefinitionPage;

    /* The page where the LDAP entries are selected as source model tables */
    private LdapTablesPage ldapTablesPage;

    /* The page where the LDAP attributes are selected as source model table columns */
    private LdapColumnsPage ldapColumnsPage;

    private boolean openProjectExists = true;

    /**
     * Create new instance
     */
    public LdapImportWizard() {
        super(ModelGeneratorLdapUiPlugin.getDefault(), getString("ImportLdapWizard_title"), LDAP_ICON); //$NON-NLS-1$
    }

    private static String getString(String key) {
        return ModelGeneratorLdapUiConstants.UTIL.getString(key);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        super.createPageControls(pageContainer);
        updateForProperties();
    }

    /**
     * Method declared on IWorkbenchWizard.
     */
    @Override
    public void init( IWorkbench workbench, IStructuredSelection currentSelection ) {
        IStructuredSelection selection = currentSelection;
        IProject newProject;

        List selectedResources = IDE.computeSelectedResources(currentSelection);
        if (!selectedResources.isEmpty()) {
            selection = new StructuredSelection(selectedResources);
        }

        openProjectExists = ModelerUiViewUtils.workspaceHasOpenModelProjects();
        if( !openProjectExists ) {
            newProject = ModelerUiViewUtils.queryUserToCreateModelProject();

            if( newProject != null ) {
                selection = new StructuredSelection(newProject);
                openProjectExists = true;
            } else {
                openProjectExists = false;
        		addPage(NoOpenProjectsWizardPage.getStandardPage());
        		return;
            }
        }

        createWizardPages(selection);
        setNeedsProgressMonitor(true);
    }

    /**
     * Create Wizard pages for the wizard
     *
     * @param theSelection the initial workspace selection
     */
    public void createWizardPages( ISelection theSelection ) {

        // construct pages
        this.ldapDefinitionPage = new LdapDefinitionPage(this.importManager);
        this.ldapDefinitionPage.setPageComplete(false);
        addPage(this.ldapDefinitionPage);

        this.ldapTablesPage = new LdapTablesPage(this.importManager);
        this.ldapTablesPage.setPageComplete(false);
        addPage(this.ldapTablesPage);

        this.ldapColumnsPage = new LdapColumnsPage(this.importManager);
        this.ldapColumnsPage.setPageComplete(false);
        addPage(this.ldapColumnsPage);
    }

    @Override
    public void dispose() {
        super.dispose();
        importManager.dispose();
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        boolean result = false;

        // Save object selections from previous page
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(final IProgressMonitor monitor) throws InvocationTargetException {
                // Wrap in transaction so it doesn't result in Significant Undoable
                boolean started = ModelerCore.startTxn(false, false, "Generate Model From LDAP Server Service", new Object()); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    importManager.createModel();
                    succeeded = true;
                } catch (Exception ex) {
                    ModelGeneratorLdapUiConstants.UTIL.log(ex);
                    throw new InvocationTargetException(ex);
                } catch (Throwable t) {
                    throw new InvocationTargetException(t);
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

            }
        };

        try {
            ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
            dlg.run(true, true, op);
            result = true;
        } catch (Throwable err) {
            IStatus status;

            if (err instanceof InvocationTargetException) {
                Throwable t = ((InvocationTargetException) err).getTargetException();
                status = new Status(IStatus.ERROR, ModelGeneratorLdapUiConstants.PLUGIN_ID, IStatus.ERROR, getString("importError_msg"), t); //$NON-NLS-1$
            } else {
                status = new Status(IStatus.ERROR, ModelGeneratorLdapUiConstants.PLUGIN_ID, IStatus.ERROR, getString("importError_msg"), err); //$NON-NLS-1$
            }

            ErrorDialog.openError(this.getShell(),
                                  getString("ImportLdapWizard_importError_title"),  //$NON-NLS-1$
                                  getString("ImportLdapWizard_importError_msg"), status); //$NON-NLS-1$
            ModelGeneratorLdapUiConstants.UTIL.log(err);
        } finally {
            dispose();
        }

        return result;
    }

    @Override
    public void setProperties(Properties props) {
        this.importManager.setDesignerProperties(props);
    }

    protected void updateForProperties() {
        Properties designerProperties = this.importManager.getDesignerProperties();
        if (designerProperties == null) {
            return;
        }

        // Check for sources and views folders in Property Definitions
        if( this.importManager.getSourceModelLocation() == null) {  
            IContainer project = DesignerPropertiesUtil.getProject(designerProperties);
            IContainer srcResource = DesignerPropertiesUtil.getSourcesFolder(designerProperties);
            if (srcResource != null) {
                this.importManager.setSourceModelLocation(srcResource);
            } else if( project != null ) {
                this.importManager.setSourceModelLocation(project);
            }
        }

        if( this.importManager.getConnectionProfile() == null ) {
            // check for Connection Profile in property definitions
            String profileName = DesignerPropertiesUtil.getConnectionProfileName(designerProperties);
            if( profileName != null && !profileName.isEmpty() ) {
                // Select profile
                ldapDefinitionPage.selectConnectionProfile(profileName);
            }
        }

        if( !this.openProjectExists) {
            DesignerPropertiesUtil.setProjectStatus(designerProperties, IPropertiesContext.NO_OPEN_PROJECT);
        }
    }
}
