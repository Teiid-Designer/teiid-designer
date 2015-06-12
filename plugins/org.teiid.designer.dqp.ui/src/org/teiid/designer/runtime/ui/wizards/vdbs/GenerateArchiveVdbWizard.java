/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import org.eclipse.core.resources.IFile;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.wizard.AbstractWizard;

/**
 * This wizard provides the user interface to generate a VDB archive/zip file from an existing VDB XML file in the workspace
 * 
 * There are 2 pages
 * 	  - page 1 provides raw feedback on the contents and info of the selected input dynamic vdb file
 *    - page 2 provides options to defined the name, location and version of the generated *.vdb file in the user's workspace
 */
public class GenerateArchiveVdbWizard extends AbstractWizard {

    private static final String TITLE = Messages.GenerateArchiveVdbWizard_title;

	GenerateArchiveVdbManager vdbManager;
	
	GenerateArchiveVdbPageOne page1;
	GenerateArchiveVdbPageTwo page2;

	public GenerateArchiveVdbWizard(IFile vdbFile) throws Exception {
		super(DqpUiPlugin.getDefault(), TITLE, null);
		
		vdbManager = new GenerateArchiveVdbManager(vdbFile);
	}
	
	@Override
	public void addPages() {
		page1 = new GenerateArchiveVdbPageOne(vdbManager);
        addPage(page1);
        
		page2 = new GenerateArchiveVdbPageTwo(vdbManager);
        addPage(page2);
	}

	@Override
	public boolean finish() {
		vdbManager.generate();
		return true;
	}

}

