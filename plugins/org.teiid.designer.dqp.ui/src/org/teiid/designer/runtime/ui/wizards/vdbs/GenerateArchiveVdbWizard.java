package org.teiid.designer.runtime.ui.wizards.vdbs;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.transformation.ui.wizards.file.TeiidFlatFileImportOptionsPage;
import org.teiid.designer.ui.common.wizard.AbstractWizard;

public class GenerateArchiveVdbWizard extends AbstractWizard {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(GenerateArchiveVdbWizard.class);

    private static final String TITLE = "Generate VDB Archive";
    
    private static String getString( final String id ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

//    private static String getString( final String id,
//                                     final Object value ) {
//        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, value);
//    }

	GenerateArchiveVdbManager vdbManager;
	
	GenerateArchiveVdbPageOne page1;
	GenerateArchiveVdbPageTwo page2;

	public GenerateArchiveVdbWizard(IFile vdbFile) {
		super(DqpUiPlugin.getDefault(), TITLE, null);
		
		vdbManager = new GenerateArchiveVdbManager(vdbFile);
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
		page1 = new GenerateArchiveVdbPageOne(vdbManager);
        addPage(page1);
        
		page2 = new GenerateArchiveVdbPageTwo(vdbManager);
        addPage(page2);
	}

	@Override
	public boolean finish() {
		// TODO Auto-generated method stub
		return false;
	}

}

