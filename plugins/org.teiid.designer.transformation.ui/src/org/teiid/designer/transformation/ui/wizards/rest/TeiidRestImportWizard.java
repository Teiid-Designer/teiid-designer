/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.rest;

import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportDataSourcePage;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportViewModelPage;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportWizard;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlFileImportProcessor;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlFileInfo;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlImportOptionsPage;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlImportSourcePage;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlImportXmlConfigurationPage;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.IPropertiesContext;


/**
 * @since 8.0
 */
public class TeiidRestImportWizard extends TeiidMetadataImportWizard {
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidRestImportWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final ImageDescriptor IMAGE = UiPlugin.getDefault().getImageDescriptor(Images.IMPORT_TEIID_METADATA);

    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    TeiidXmlImportOptionsPage optionsPage;
    
    TeiidRestImportSourcePage sourcePage;
    
    TeiidRestImporterModelDefinitionPage modelDefPage;
//    
//    private Properties designerProperties;
    
	/**
	 * 
	 */
	public TeiidRestImportWizard() {
        super(UiPlugin.getDefault(), TITLE, IMAGE);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection inputSelection) {
        super.init(workbench, inputSelection);
        
        getFileInfo().setFileMode(TeiidMetadataImportInfo.REST_MODE_URL);
	}
	
	@Override
	public void addPages() {
		
        this.sourcePage = new TeiidRestImportSourcePage(getFileInfo());
        addPage(sourcePage);
        
        this.modelDefPage = new TeiidRestImporterModelDefinitionPage(getFileInfo());
        addPage(modelDefPage);

        addPage(new TeiidMetadataImportDataSourcePage(getFileInfo()));
        
        TeiidXmlImportXmlConfigurationPage sqlPage = new TeiidXmlImportXmlConfigurationPage(getFileInfo());
        addPage(sqlPage);
        
	}

	@Override
	public boolean finish() {
		final TeiidRestImportProcessor processor = new TeiidRestImportProcessor(getFileInfo(), this.getShell());
		
		UiUtil.runInSwtThread(new Runnable() {
			@Override
			public void run() {
				processor.execute();
			}
		}, false);
		
        // Update Properties to include the created source and view Models
		if( getDesignerProperties() != null ) {
            DesignerPropertiesUtil.setSourceModelName(getDesignerProperties(), getFileInfo().getSourceModelName());
            DesignerPropertiesUtil.setViewModelName(getDesignerProperties(), getFileInfo().getViewModelName());
            // Should be one info object
            if( ! getFileInfo().getXmlFileInfos().isEmpty() && getFileInfo().getXmlFileInfos().size() == 1 ) {
	            TeiidXmlFileInfo info = this.getFileInfo().getXmlFileInfos().iterator().next();
	            DesignerPropertiesUtil.setPreviewTargetModelName(getDesignerProperties(), getFileInfo().getViewModelName());
	            DesignerPropertiesUtil.setPreviewTargetObjectName(getDesignerProperties(), info.getViewProcedureName());
            }
		}

		return true;
	}
	
	/**
	 * @param option the file option
	 */
	public void setFileOption(int option) {
		getFileInfo().setFileMode(option);
	}
	
//	@Override
//	public void setProperties(Properties properties) {
//    	this.designerProperties = properties;
//	}
	
	@Override
	protected void updateForProperties() {
		Properties desProps = getDesignerProperties();
		if( desProps == null || desProps.isEmpty() ) {
			return;
		}

        // Check for Sources and View Folder from property definitions
		IContainer project = DesignerPropertiesUtil.getProject(getDesignerProperties());
        IContainer srcFolderResrc = DesignerPropertiesUtil.getSourcesFolder(desProps);
        IContainer viewFolderResrc = DesignerPropertiesUtil.getViewsFolder(desProps);
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
    	
        // Get Connection Profile from property definitions
        String profileName = DesignerPropertiesUtil.getConnectionProfileName(desProps);
		if( profileName != null && !profileName.isEmpty() ) {
            // Set properties - needs later to determine the connection profile
            sourcePage.setDesignerProperties(desProps);
		}
		
		this.optionsPage.setDesignerProperties(desProps);
		
    	if( !openProjectExists()) {
			DesignerPropertiesUtil.setProjectStatus(desProps, IPropertiesContext.NO_OPEN_PROJECT);
		}
	}
}
