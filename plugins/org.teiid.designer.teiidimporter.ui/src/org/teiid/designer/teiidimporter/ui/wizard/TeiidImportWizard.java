/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.teiidimporter.ui.wizard;

import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ddl.importer.ui.DdlImportDifferencesPage;
import org.teiid.designer.teiidimporter.ui.Activator;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.common.wizard.IPersistentWizardPage;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.IPropertiesContext;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;


/**
 * TeiidImportWizard
 * Performs import by deploying a teiid dynamic VDB with a chose DataSource.
 * Once the VDB is deployed, the teiid DDL is retrieved then parsed to determine the source model structure.
 *  
 * @since 8.1
 */
public class TeiidImportWizard extends AbstractWizard implements IImportWizard, IPropertiesContext, UiConstants {

	private static final ImageDescriptor IMAGE = Activator.getDefault().getImageDescriptor(ImageIds.IMPORT_TEIID_METADATA);

    private TeiidImportManager importManager;
    private SelectDataSourcePage selectDataSourcePage;
    private SelectTranslatorPage selectTranslatorPage;
    private SelectTargetPage selectTargetPage;
    private ShowDDLPage showDDLPage;
    private DdlImportDifferencesPage differencesPage;
    
    private Properties options;
    
    IContainer targetContainer = null;
    
    private Properties designerProperties;
    private boolean openProjectExists = true;
    private IProject newProject;

    /**
     * TeiidImportWizard Constructor
     */
    public TeiidImportWizard() {
        super(Activator.getDefault(), Messages.TeiidImportWizard_title, IMAGE);
        
        final IDialogSettings pluginSettings = Activator.getDefault().getDialogSettings();
        final String sectionName = TeiidImportWizard.class.getSimpleName();
        IDialogSettings section = pluginSettings.getSection(sectionName);
        if (section == null) section = pluginSettings.addNewSection(sectionName);
        setDialogSettings(section);
        
        this.options = new Properties();
        this.options.put(FILTER_CONSTAINTS, Boolean.toString(getImportManager().isFilterRedundantUniqueConstraints()));
    }

	@Override
	public void init(IWorkbench workbench, IStructuredSelection inputSelection) {
        IStructuredSelection finalSelection = inputSelection;
        // Request User to Create a Model Project - if none open in the workspace.
    	openProjectExists = ModelerUiViewUtils.workspaceHasOpenModelProjects();
        if( !openProjectExists ) {
        	newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		finalSelection = new StructuredSelection(newProject);
        		openProjectExists = true;
        	} else {
        		openProjectExists = false;
        	}
        }
        
        // Init the DDL Importer
        final Collection<IProject> projects = DotProjectUtils.getOpenModelProjects();
        IProject[] projectArray = projects.toArray(new IProject[0]);
        getImportManager().initDdlImporter(projectArray);
		
        // Get the selected Object - determine if it's a relational source model
        Object selectedObj = finalSelection.getFirstElement();
        targetContainer = null;

        boolean isRelationalSourceModel = false;
        try {
            if (selectedObj instanceof IFile) {
                ModelResource modelResource = ModelUtil.getModelResource((IFile)selectedObj, false);
                isRelationalSourceModel = ModelIdentifier.isRelationalSourceModel(modelResource);
            }
        } catch (Exception e) {
            UTIL.log(e);
        }
        
        // If not null, set targetContainer to current selection if a container or to model object container
        if (selectedObj!=null) {
            targetContainer = ModelUtil.getContainer(selectedObj);
            try {
                if (targetContainer != null && targetContainer.getProject().getNature(ModelerCore.NATURE_ID) == null) {
                    targetContainer = null;
                }
            } catch (final CoreException err) {
                UTIL.log(err);
                WidgetUtil.showError(err);
            }
        }
        
        if(targetContainer!=null) {
            getImportManager().setTargetModelLocation(targetContainer.getFullPath());
        }
        
        if(isRelationalSourceModel) {
            getImportManager().setTargetModelName(((IFile)selectedObj).getName());
        }
        
	}
	
	/*
	 * Get the ImportManager.  If it's not yet created, instantiate it.
	 */
	private TeiidImportManager getImportManager() {
	    if(this.importManager!=null) {
	        return this.importManager;
	    }
	    this.importManager  = new TeiidImportManager();
	    return this.importManager;
	}

	@Override
	public void addPages() {
	    // DataSource Selection Page
		this.selectDataSourcePage = new SelectDataSourcePage(importManager);
        addPage(selectDataSourcePage);
        
        // Translator and TargetModel Selection Page
        this.selectTranslatorPage = new SelectTranslatorPage(importManager);
        addPage(selectTranslatorPage);
        
        // Translator and TargetModel Selection Page
        this.selectTargetPage = new SelectTargetPage(importManager);
        addPage(selectTargetPage);

        // View DDL Page
        this.showDDLPage = new ShowDDLPage(importManager);
        addPage(showDDLPage);
        
        // Differences Page
        this.differencesPage = new DdlImportDifferencesPage(importManager.getDdlImporter(), this.options);
        // DDL differences page
        addPage(differencesPage);  
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		updateForProperties();
	}
	
	@Override
	public boolean performCancel() {
        importManager.undeployDynamicVdb();
	    return true;
	}
	
    @Override
    public boolean finish() {
    	// Get createConnectionProfile flag and provide to manager.  determines if CP is created or not.
    	importManager.setCreateConnectionProfile(this.selectTargetPage.isCreateConnectionProfile());
    	importManager.setFilterRedundantUniqueConstraints(this.selectTargetPage.isFilterRedundantUniqueConstraints());
    	
    	// Saves the model
        importManager.saveUsingDdlDiffReport(getShell());
                
        // Save user settings
        for (final IWizardPage pg : getPages())
            if (pg instanceof IPersistentWizardPage) ((IPersistentWizardPage)pg).saveSettings();

        importManager.undeployDynamicVdb();
        importManager.deleteDdlTempFile();
        
        // Open the created Model in an Editor
        openModelInEditor();

        return true;
    }
    
    /*
     * Opens the created model in an editor
     */
    private void openModelInEditor() {
    	String modelName = importManager.getTargetModelName();
    	IPath modelLocation = importManager.getTargetModelLocation();
    	if(!CoreStringUtil.isEmpty(modelName) && modelLocation!=null) {
    		IPath modelPath = new Path(modelLocation.toOSString()).append(modelName);
    		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
    			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
    		}

    		IResource targetModel = ModelerCore.getWorkspace().getRoot().getFile(modelPath);
    		if(targetModel!=null) {
    			final ModelResource targetModelResc = ModelUtilities.getModelResourceForIFile((IFile)targetModel, false);
    			if(targetModelResc!=null) {
    				UiUtil.runInSwtThread(new Runnable() {
    					@Override
    					public void run() {
    						ModelEditorManager.openInEditMode(targetModelResc,
    								true,
    								org.teiid.designer.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
    					}
    				}, true);
    			}
    		}
    	}
    }
    
	@Override
	public void setProperties(Properties properties) {
    	this.designerProperties = properties;
	}
	
	protected boolean openProjectExists() {
		return this.openProjectExists;
	}
	
    protected void updateForProperties() {
		if( this.designerProperties == null || this.designerProperties.isEmpty() ) {
			return;
		}
		
        if (this.targetContainer == null) {
            // Check for Sources and View Folder from property definitions
    		IContainer project = DesignerPropertiesUtil.getProject(designerProperties);
            IContainer srcFolderResrc = DesignerPropertiesUtil.getSourcesFolder(this.designerProperties);
            if (srcFolderResrc != null) {
                getImportManager().setTargetModelLocation(srcFolderResrc.getFullPath());
            } else if( project != null ) {
            	getImportManager().setTargetModelLocation(project.getFullPath());
            }
        }
    	
    	if( !this.openProjectExists) {
			DesignerPropertiesUtil.setProjectStatus(this.designerProperties, IPropertiesContext.NO_OPEN_PROJECT);
		}
	}
}
