/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.util.ErrorHandler;

/**
 * This wizard provides the user interface to generate a Dynamic VDB XML file from an existing VDB archive/zip file.
 * 
 * There are 2 pages
 * 	  - page 1 provides raw feedback on the contents and info of the selected VDB archive/zip file in the workspace
 *    - page 2 provides options to defined the name, location and version of the generated *-vdb.xml file in the user's workspace
 */
public class GenerateDynamicVdbWizard extends AbstractWizard {

    private static final String TITLE = Messages.GenerateDynamicVdbWizard_title;

	private final GenerateDynamicVdbManager vdbManager;
	
	private GenerateDynamicVdbPageOne page1;
	private GenerateDynamicVdbPageTwo page2;

	/**
	 * @param vdbFile
	 * @throws Exception
	 */
	public GenerateDynamicVdbWizard(IFile vdbFile) throws Exception {
		super(DqpUiPlugin.getDefault(), TITLE, null);
		
		vdbManager = new GenerateDynamicVdbManager(vdbFile);
	}

	@Override
	public void addPages() {
		page1 = new GenerateDynamicVdbPageOne(vdbManager);
        addPage(page1);
		page2 = new GenerateDynamicVdbPageTwo(vdbManager);
        addPage(page2);
	}

	@Override
	public boolean finish() {

	    try {
            vdbManager.write(new NullProgressMonitor());
            return true;
        } catch (Exception ex) {
            DqpPlugin.Util.log(ex);
            ErrorHandler.toExceptionDialog(ex);
            return false;
        }
	}
}

