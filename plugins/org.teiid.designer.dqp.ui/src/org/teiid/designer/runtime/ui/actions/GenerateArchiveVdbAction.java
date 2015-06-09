package org.teiid.designer.runtime.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.wizards.vdbs.GenerateArchiveVdbWizard;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

public class GenerateArchiveVdbAction extends SortableSelectionAction implements DqpUiConstants {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(GenerateArchiveVdbAction.class);
    private static final String label = DqpUiConstants.UTIL.getString("label"); //$NON-NLS-1$

//    private static String getString( final String id ) {
//        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
//    }

//    private static String getString( final String id,
//                                     final Object value ) {
//        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, value);
//    }
    
//    private static String getString( final String id, final Object value, final Object value2) {
//    	return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, value, value2);
//	}

    private ITeiidServer cachedServer;

    /**
     * @since 5.0
     */
    public GenerateArchiveVdbAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.STANDARD_VDB));
    }

    public void setTeiidServer( ITeiidServer teiidServer ) {
        this.cachedServer = teiidServer;
    }

    /**
     * @see org.teiid.designer.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return vdbSelected(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();
        
        Object obj = SelectionUtilities.getSelectedObject(getSelection());
        if (obj instanceof IFile) {
            IFile vdbXmlFile = (IFile)obj;

            GenerateArchiveVdbWizard wizard = new GenerateArchiveVdbWizard(vdbXmlFile);
            
    		WizardDialog wd = new WizardDialog(getShell(), wizard);
    		wd.open();
    		return;
        }

        MessageDialog.openInformation(iww.getShell(), "Nothing Exported", "Dynamic VDB XML file was not selected");

    }
    


    /**
     * @see org.teiid.designer.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return vdbSelected(selection);
    }

    private boolean vdbSelected( ISelection theSelection ) {
        boolean result = false;
        List<Object> allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator<Object> iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                    result = VdbUtil.isDynamicVdb((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }

        return result;
    }

    private Shell getShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }
}
