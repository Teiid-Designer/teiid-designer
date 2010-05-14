/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
