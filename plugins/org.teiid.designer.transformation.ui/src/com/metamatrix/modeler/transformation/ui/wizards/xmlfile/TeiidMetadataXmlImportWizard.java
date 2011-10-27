/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportSourcePage;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportViewModelPage;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportWizard;
import com.metamatrix.ui.internal.util.UiUtil;

public class TeiidMetadataXmlImportWizard extends TeiidMetadataImportWizard {

	public TeiidMetadataXmlImportWizard() {
		super();
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection inputSelection) {
        super.init(workbench, inputSelection);
        
        getFileInfo().setFlatFileMode(false);
	}
	
	@Override
	public void addPages() {
		
		TeiidMetadataImportSourcePage sourcePage = new TeiidMetadataImportSourcePage(getFileInfo());
        addPage(sourcePage);
        
        TeiidMetadataImportXmlOptionsPage sqlPage = new TeiidMetadataImportXmlOptionsPage(getFileInfo());
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
}
