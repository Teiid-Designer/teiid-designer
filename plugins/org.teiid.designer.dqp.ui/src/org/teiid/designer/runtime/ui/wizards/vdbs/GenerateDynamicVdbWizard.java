package org.teiid.designer.runtime.ui.wizards.vdbs;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.wizard.AbstractWizard;

public class GenerateDynamicVdbWizard extends AbstractWizard {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(GenerateDynamicVdbWizard.class);

    private static final String TITLE = "Generate Dynamic VDB";
    
    private static String getString( final String id ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

	GenerateDynamicVdbManager vdbManager;
	
	GenerateDynamicVdbPageOne page1;

	public GenerateDynamicVdbWizard(IFile vdbFile) {
		super(DqpUiPlugin.getDefault(), TITLE, null);
		
		vdbManager = new GenerateDynamicVdbManager(vdbFile);
	}
	
    /**
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {

        this.page1.setPageComplete(false);
        this.page1.setMessage(getString("initialMessage")); //$NON-NLS-1$

        addPage(page1);
    }
    
	@Override
	public void addPages() {
		page1 = new GenerateDynamicVdbPageOne(vdbManager);
        addPage(page1);

	}

	@Override
	public boolean finish() {
		// TODO Auto-generated method stub
		return false;
	}

}

