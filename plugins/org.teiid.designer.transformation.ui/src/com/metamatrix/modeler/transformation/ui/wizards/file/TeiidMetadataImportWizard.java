/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;
import com.metamatrix.modeler.ui.viewsupport.IPropertiesContext;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;


/**
 * Import wizard designed to import metadata from one or more Teiid formatted data files and create a relational
 * model containing the standard/generated File Connector procedures and create view relational tables containing
 * the SQL containing the function call which will return the data from the file in relational table format
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
    
	/**
	 * @since 4.0
	 */
	public TeiidMetadataImportWizard() {
        super(UiPlugin.getDefault(), TITLE, IMAGE);
	}

    public TeiidMetadataImportWizard( final AbstractUIPlugin plugin,
                                      final String title,
                                      final ImageDescriptor image ) {
        super(plugin, title, image);
    }

	@Override
	public void init(IWorkbench workbench, IStructuredSelection inputSelection) {
        
        IStructuredSelection finalSelection = inputSelection;
        // Request User to Create a Model Project - if none open in the workspace.
        if (!ModelerUiViewUtils.workspaceHasOpenModelProjects()) {
            IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();

            if (newProject != null) {
                finalSelection = new StructuredSelection(newProject);
            }
        }
		
        Object seletedObj = finalSelection.getFirstElement();
        folder = null;
        boolean isViewRelationalModel = false;
        
        try {
            if (seletedObj instanceof IFile) {
                ModelResource modelResource = ModelUtil.getModelResource((IFile)seletedObj, false);
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
        this.filesInfo.setFileMode(TeiidMetadataImportInfo.FILE_MODE_ODA_FLAT_FILE);
        
        // Set initial view model and view model location values if present from selection
        if( isViewRelationalModel ) {
        	this.filesInfo.setViewModelName( ((IFile)seletedObj).getName());
        	this.filesInfo.setViewModelLocation(((IFile)seletedObj).getFullPath().removeLastSegments(1));
        	this.filesInfo.setViewModelExists(true);
        	this.filesInfo.setSourceModelLocation(((IFile)seletedObj).getFullPath().removeLastSegments(1));
        }
        if( folder != null ) {
        	this.filesInfo.setSourceModelLocation(folder.getFullPath());
        	this.filesInfo.setViewModelLocation(folder.getFullPath());
        }

	}

	@Override
	public void addPages() {
		this.sourcePage = new TeiidMetadataImportSourcePage(getFileInfo());
        addPage(sourcePage);
        
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

	public TeiidMetadataImportInfo getFileInfo() {
		return this.filesInfo;
	}

	@Override
	public void setProperties(Properties properties) {
    	this.designerProperties = properties;
		
	}
	
    protected void updateForProperties() {
		if( this.designerProperties == null || this.designerProperties.isEmpty() ) {
			return;
		}
		
        if (this.folder == null) {
            // Check for Sources and View Folder from property definitions
    		IContainer project = DesignerPropertiesUtil.getProject(designerProperties);
            IContainer srcFolderResrc = DesignerPropertiesUtil.getSourcesFolder(this.designerProperties);
            IContainer viewFolderResrc = DesignerPropertiesUtil.getViewsFolder(this.designerProperties);
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
	}
}
