/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.printing.DiagramPageSetupDialog;

/**
 * DiagramPageSetupAction
 */
public class DiagramPageSetupAction extends DiagramAction 
                                 implements DiagramUiConstants {
    
//    private static final String TITLE 
//        = Util.getString("DiagramPageSetupAction.title"); //$NON-NLS-1$

    private DiagramEditor editor;

    /**
     * Construct an instance of SaveDiagramAction.
     * 
     */
    public DiagramPageSetupAction(DiagramEditor editor) {
        super();
//        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.SAVE_DIAGRAM));
        this.editor = editor;
    }

    /**
     * Construct an instance of SaveDiagramAction.
     * @param theStyle
     */
    public DiagramPageSetupAction(int theStyle) {
        super(theStyle);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);

        // do we care about selection? Don't we always enable this action?
        setEnabled(true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        DiagramPageSetupDialog dlg 
            = new DiagramPageSetupDialog( DiagramUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell() );        
        dlg.open();
        
        /*
         * Issue: How do we implement the dialog's Print button?
         *   - We could maintain selection in this class in exactly the
         *   same way PrintWrapper does;
         *   Then, we could instantiate PrintWrapper, pass this Action's selection to it
         *   in pwrapper.selectionChanged( part, selection ), and pass PrintWrapper
         *   directly to the Dialog (in ctor or set method).
         *   
         *   The Dialog would then use its PrintWrapper to 
         *      1) enable its Print button
         *      2) print, if user clicks the Print button
         */
        // Get current DiagramEditor
        if( editor != null ) {
            
          
        }

    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.actions.DiagramAction#requiresEditorForRun()
     * @since 5.0
     */
    @Override
    protected boolean requiresEditorForRun() {
        return false;
    }
    
    
}
