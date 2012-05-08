/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportViewModelPage;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportWizard;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;
import com.metamatrix.ui.internal.util.UiUtil;

public class TeiidXmlImportWizard extends TeiidMetadataImportWizard {
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidXmlImportWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final ImageDescriptor IMAGE = UiPlugin.getDefault().getImageDescriptor(Images.IMPORT_TEIID_METADATA);

    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    TeiidXmlImportOptionsPage optionsPage;
    
    TeiidXmlImportSourcePage sourcePage;
    
    private Properties designerProperties;
    
	public TeiidXmlImportWizard() {
        super(UiPlugin.getDefault(), TITLE, IMAGE);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection inputSelection) {
        super.init(workbench, inputSelection);
        
        getFileInfo().setFileMode(TeiidMetadataImportInfo.FILE_MODE_TEIID_XML_FILE);
	}
	
	@Override
	public void addPages() {
		this.optionsPage = new TeiidXmlImportOptionsPage(getFileInfo());
		addPage(optionsPage);
		
        this.sourcePage = new TeiidXmlImportSourcePage(getFileInfo());
        addPage(sourcePage);
        
        TeiidXmlImportXmlConfigurationPage sqlPage = new TeiidXmlImportXmlConfigurationPage(getFileInfo());
        addPage(sqlPage);
        
        TeiidMetadataImportViewModelPage viewModelPage = new TeiidMetadataImportViewModelPage(getFileInfo());
        addPage(viewModelPage);
	}

	@Override
	public boolean finish() {
		final TeiidXmlFileImportProcessor processor = new TeiidXmlFileImportProcessor(getFileInfo(), this.getShell());
		
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
	
	public void setFileOption(int option) {
		getFileInfo().setFileMode(option);
	}
	
	@Override
	public void setProperties(Properties properties) {
    	this.designerProperties = properties;
	}
	
	protected void updateForProperties() {
		if( this.designerProperties == null || this.designerProperties.isEmpty() ) {
			return;
		}

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
    	
        // Get Connection Profile from property definitions
        String profileName = DesignerPropertiesUtil.getConnectionProfileName(this.designerProperties);
		if( profileName != null && !profileName.isEmpty() ) {
            // Set properties - needs later to determine the connection profile
            sourcePage.setDesignerProperties(this.designerProperties);
		}
		
		this.optionsPage.setDesignerProperties(this.designerProperties);
	}
}
