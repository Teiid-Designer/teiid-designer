/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.file;

import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.common.wizard.NoOpenProjectsWizardPage;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.IPropertiesContext;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;



/**
 * Import wizard designed to import metadata from one or more Teiid formatted data files and create a relational
 * model containing the standard/generated File Connector procedures and create view relational tables containing
 * the SQL containing the function call which will return the data from the file in relational table format
 *
 * @since 8.0
 */
public class TeiidMetadataImportWizard extends AbstractWizard implements
		IPropertiesContext, IImportWizard, UiConstants {

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportWizard.class);

	private static final String TITLE =  getString("title"); //$NON-NLS-1$

	private static final ImageDescriptor IMAGE = UiPlugin.getDefault().getImageDescriptor(Images.IMPORT_TEIID_METADATA);

    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private TeiidMetadataImportInfo filesInfo;
    
    private TeiidMetadataImportSourcePage sourcePage;
    
    IContainer folder = null;
    
    private Properties designerProperties;
    private boolean openProjectExists = true;
    private IProject newProject;
    
	/**
	 * @since 4.0
	 */
	public TeiidMetadataImportWizard() {
        super(UiPlugin.getDefault(), TITLE, IMAGE);
	}

    /**
     * @param plugin the plugin
     * @param title the wizard title
     * @param image the wizard image descriptor
     */
    public TeiidMetadataImportWizard( final AbstractUIPlugin plugin,
                                      final String title,
                                      final ImageDescriptor image ) {
        super(plugin, title, image);
    }

	@Override
	public void init(IWorkbench workbench, IStructuredSelection inputSelection) {
		IProject targetProject = null;
		
        IStructuredSelection finalSelection = inputSelection;
        // Request User to Create a Model Project - if none open in the workspace.
    	openProjectExists = ModelerUiViewUtils.workspaceHasOpenModelProjects();
        if( !openProjectExists ) {
        	newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		finalSelection = new StructuredSelection(newProject);
        		targetProject = newProject;
        		openProjectExists = true;
        	} else {
        		addPage(NoOpenProjectsWizardPage.getStandardPage());
        		return;
        	}
        }
		
        Object selectedObj = finalSelection.getFirstElement();
        
        if( targetProject == null ) {
        	if( selectedObj instanceof IResource ) {
        		targetProject = ((IResource)selectedObj).getProject();
        	}
        }
        
        
        folder = null;
        boolean isViewRelationalModel = false;
        
        try {
            if (selectedObj instanceof IFile) {
                ModelResource modelResource = ModelUtil.getModelResource((IFile)selectedObj, false);
                isViewRelationalModel = ModelIdentifier.isRelationalViewModel(modelResource);
            }
        } catch (Exception e) {
            Util.log(e);
        }
        // If not null, set folder to current selection if a folder or to containing folder if a model object
        if (!finalSelection.isEmpty()) {
            final Object obj = finalSelection.getFirstElement();
            folder = ModelUtil.getContainer(obj);
            try {
                if (folder != null && folder.getProject().getNature(ModelerCore.NATURE_ID) == null) {
                    folder = null;
                }
            } catch (final CoreException err) {
                Util.log(err);
                WidgetUtil.showError(err);
            }
        }
        
        // Construct the business object
        this.filesInfo = new TeiidMetadataImportInfo();
        this.filesInfo.setFileMode(TeiidMetadataImportInfo.FILE_MODE_FLAT_FILE_LOCAL);
        this.filesInfo.setTargetProject(targetProject);
        
        // Set initial view model and view model location values if present from selection
        if( isViewRelationalModel ) {
        	this.filesInfo.setViewModelName( ((IFile)selectedObj).getName());
        	this.filesInfo.setViewModelLocation(((IFile)selectedObj).getFullPath().removeLastSegments(1));
        	this.filesInfo.setViewModelExists(true);
        	this.filesInfo.setSourceModelLocation(((IFile)selectedObj).getFullPath().removeLastSegments(1));
        }
        if( folder != null ) {
        	this.filesInfo.setSourceModelLocation(folder.getFullPath());
        	this.filesInfo.setViewModelLocation(folder.getFullPath());
        }

	}

	@Override
	public void addPages() {
		if( !openProjectExists ) return;
		
        TeiidFlatFileImportOptionsPage flatFileImportOptionsPage = new TeiidFlatFileImportOptionsPage(getFileInfo());
        addPage(flatFileImportOptionsPage);

        this.sourcePage = new TeiidMetadataImportSourcePage(getFileInfo());
        addPage(sourcePage);
        
        
        addPage(new TeiidMetadataImportDataSourcePage(getFileInfo()));
        
        TeiidMetadataImportFormatPage formatSelectionPage = new TeiidMetadataImportFormatPage(getFileInfo());
        addPage(formatSelectionPage);
        
        TeiidMetadataImportOptionsPage optionsPage = new TeiidMetadataImportOptionsPage(getFileInfo());
        addPage(optionsPage);
        
        TeiidMetadataImportViewModelPage viewModelPage = new TeiidMetadataImportViewModelPage(getFileInfo());
        addPage(viewModelPage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		// TODO Auto-generated method stub
		super.createPageControls(pageContainer);
		updateForProperties();
	}

	@Override
	public boolean finish() {
		final TeiidMetadataImportProcessor processor = new TeiidMetadataImportProcessor(this.filesInfo, this.getShell());
		
		UiUtil.runInSwtThread(new Runnable() {
			@Override
			public void run() {
				processor.execute();
			}
		}, false);
		
        // Update Properties to include the created source and view Models
		if( this.designerProperties != null ) {
            DesignerPropertiesUtil.setSourceModelName(this.designerProperties, getFileInfo().getSourceModelName());
            DesignerPropertiesUtil.setViewModelName(this.designerProperties, getFileInfo().getViewModelName());
		}

        return true;
	}

	/**
	 * @return the Teiid metadata import info object
	 */
	public TeiidMetadataImportInfo getFileInfo() {
		return this.filesInfo;
	}

	@Override
	public void setProperties(Properties properties) {
    	this.designerProperties = properties;
		
	}
	
	public Properties getDesignerProperties() {
		return this.designerProperties;
	}
	
	protected boolean openProjectExists() {
		return this.openProjectExists;
	}
	
    protected void updateForProperties() {
		if( this.designerProperties == null || this.designerProperties.isEmpty() ) {
			return;
		}
		
        if (this.folder == null) {
            // Check for Sources and View Folder from property definitions
    		IProject project = DesignerPropertiesUtil.getProject(designerProperties);
            IContainer srcFolderResrc = DesignerPropertiesUtil.getSourcesFolder(this.designerProperties);
            IContainer viewFolderResrc = DesignerPropertiesUtil.getViewsFolder(this.designerProperties);
            if( project != null ) {
            	getFileInfo().setTargetProject(project);
            }
            if (srcFolderResrc != null) {
                getFileInfo().setSourceModelLocation(srcFolderResrc.getFullPath());
            } else if( project != null ) {
            	getFileInfo().setSourceModelLocation(project.getFullPath());
            }
            if (viewFolderResrc != null) {
                getFileInfo().setViewModelLocation(viewFolderResrc.getFullPath());
            } else if( project != null ) {
            	getFileInfo().setViewModelLocation(project.getFullPath());
            }
        }
    	
        // Check for Connection Profile in properties
        String profileName = DesignerPropertiesUtil.getConnectionProfileName(this.designerProperties);
		if( profileName != null && !profileName.isEmpty() ) {
			// Select profile
			sourcePage.selectConnectionProfile(profileName);
		}
		
    	if( !this.openProjectExists) {
			DesignerPropertiesUtil.setProjectStatus(this.designerProperties, IPropertiesContext.NO_OPEN_PROJECT);
		}
	}

	public boolean isOpenProjectExists() {
		return openProjectExists;
	}
}
