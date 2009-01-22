/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.cheatsheets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.cheatsheets.views.CheatSheetView;

import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.UiUtil;


/** 
 * @since 5.0
 */
public class OpenDesignerCheatSheetAction extends Action
                                          implements UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private String cheatSheetId;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public OpenDesignerCheatSheetAction(String theCheatSheetId) {
        this.cheatSheetId = theCheatSheetId;
    }
    
    /** 
     * @see org.eclipse.jface.action.Action#run()
     * @since 5.0
     */
    @Override
    public void run() {
        IWorkbenchWindow window = UiUtil.getWorkbenchWindowOnlyIfUiThread();
        
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            
            if (page != null) {
                Exception error = null;
                String errorMsg = null;
                IStatus status = null;
                CheatSheetView view = (CheatSheetView)page.findView(Extensions.ECLIPSE_CHEAT_SHEET_VIEW);
                
                if (view == null) {
                    try {
                        view = (CheatSheetView)page.showView(Extensions.ECLIPSE_CHEAT_SHEET_VIEW);
                        page.activate(view);
                    } catch (PartInitException theException) {
                        error = theException;
                        Util.log(theException);
                    }
                }
                
                if (view != null) {
                    view.setInput(this.cheatSheetId);
                    page.bringToTop(view);
                } else {
                    String msg = Util.getString("OpenDesignerCheatSheetAction.errorDialog.msg", this.cheatSheetId); //$NON-NLS-1$
                    String title = Util.getString("OpenDesignerCheatSheetAction.errorDialog.title"); //$NON-NLS-1$
                    
                    if (error != null) {
                        errorMsg = error.getLocalizedMessage();
                        
                        if (StringUtil.isEmpty(errorMsg)) {
                            errorMsg = Util.getString("OpenDesignerCheatSheetAction.errorDialog.statusMsg"); //$NON-NLS-1$
                        }
                        
                        status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, errorMsg, error);
                    } else {
                        errorMsg = Util.getString("OpenDesignerCheatSheetAction.errorDialog.statusMsg"); //$NON-NLS-1$
                        status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, errorMsg, null);
                    }
                    
                    ErrorDialog.openError(window.getShell(), title, msg, status);
                }
            }
        }
    }

}
