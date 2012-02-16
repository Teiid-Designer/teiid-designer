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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportViewModelPage;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportWizard;
import com.metamatrix.modeler.ui.viewsupport.IPropertiesContext;
import com.metamatrix.ui.internal.util.UiUtil;

public class TeiidXmlImportWizard extends TeiidMetadataImportWizard {
    
    IContainer folder = null;
    
    TeiidXmlImportSourcePage sourcePage;
    
    private Properties designerProperties;
    
	public TeiidXmlImportWizard() {
		super();
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection inputSelection) {
        super.init(workbench, inputSelection);
        
        getFileInfo().setFileMode(TeiidMetadataImportInfo.FILE_MODE_TEIID_XML_FILE);
	}
	
	@Override
	public void addPages() {
		TeiidXmlImportOptionsPage optionsPage = new TeiidXmlImportOptionsPage(getFileInfo());
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
    	if( this.folder == null ) {
    		// check for project property and if sources folder property exists
    		String projectName = this.designerProperties.getProperty(IPropertiesContext.KEY_PROJECT_NAME);
    		if( projectName != null && !projectName.isEmpty() ) {
    			String folderName = projectName;
    			String sourcesFolder = this.designerProperties.getProperty(IPropertiesContext.KEY_HAS_SOURCES_FOLDER);
    			if( sourcesFolder != null && !sourcesFolder.isEmpty() ) {
    				folderName = new Path(projectName).append(sourcesFolder).toString();
    			}
    			final IResource resrc = ResourcesPlugin.getWorkspace().getRoot().findMember(folderName);
    			if( resrc != null ) {
    				IContainer folder = (IContainer)resrc;
    				getFileInfo().setSourceModelLocation(folder.getFullPath());
    				getFileInfo().setViewModelLocation(folder.getFullPath());
    			}
    		}
    	}
    	
		// check for project property and if sources folder property exists
		String profileName = this.designerProperties.getProperty(IPropertiesContext.KEY_LAST_CONNECTION_PROFILE_ID);
		if( profileName != null && !profileName.isEmpty() ) {
			// Select profile
			sourcePage.selectConnectionProfile(profileName);
		}
	}
}
