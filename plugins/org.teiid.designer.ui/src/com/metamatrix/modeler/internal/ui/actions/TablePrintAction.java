/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.AbstractModelerAction;
import com.metamatrix.ui.print.IPrintable;
import com.metamatrix.ui.table.TablePrinter;

/**
 * TablePrintAction
 */
public class TablePrintAction extends AbstractModelerAction
                           implements UiConstants,                                   
                                      IPartListener {

    
    private static final String TOOLTIP   
        = UiConstants.Util.getString("TablePrintAction.toolTip.text"); //$NON-NLS-1$

    /**
     * Construct an instance of TablePrintAction.
     * 
     */
    public TablePrintAction() {
        super(UiPlugin.getDefault());
        initialize();
    }

    /**
     * Construct an instance of TablePrintAction.
     * @param theStyle
     */
    public TablePrintAction(int theStyle) {
        super(UiPlugin.getDefault(), theStyle );
        initialize();
        
    }

    public void initialize() {
        
        addAsPartListener();   
        this.setToolTipText( TOOLTIP );   
    }

 
    @Override
    protected void doRun() {

        /*
         * jh: most of this method was reimplemented from the GEF PrintAction class' run() method.
         */

        IEditorPart editor = getActiveEditor();
        
        if ( editor != null ) {
           
            IPrintable printable = (IPrintable)editor.getAdapter( IPrintable.class );

            if ( printable != null ) {

                Object oTable = printable.getObject();
                
                if ( oTable != null && oTable instanceof Table ) {
                    TablePrinter tpPrinter = new TablePrinter();
                    tpPrinter.printTable( (Table)oTable );
                    setEnabled( true );                     
                }
            }
        }
    }
         
    private void addAsPartListener() {
        getPlugin().getCurrentWorkbenchWindow().getPartService().addPartListener( this );
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {

        boolean bResultState = false;
        
        IEditorPart editor = getActiveEditor();
        
        if ( editor != null ) {
           
            if ( thePart instanceof ModelEditor && editor instanceof ModelEditor ) {
                IPrintable printable = (IPrintable)editor.getAdapter( IPrintable.class );
    
                if ( printable != null ) {
                    Object oViewer = printable.getObject();

                    if ( oViewer != null && oViewer instanceof Table ) {
                        bResultState = true;
                    }
                }
            }
        }
//        System.out.println("[TablePrintAction.selectionChanged] About to set enabled state to: " + bResultState ); //$NON-NLS-1$
        setEnabled( bResultState );
    }    
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    public void setEnableState() {

        IEditorPart editor = getActiveEditor();
        
        if ( editor != null ) {             
            setEnabled( true );             
        } else {
            setEnabled( false );                     
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partActivated(IWorkbenchPart part) {
        setEnableState();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     */
    public void partBroughtToTop(IWorkbenchPart part) {
        setEnableState();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     */
    public void partClosed(IWorkbenchPart part) {
        setEnabled( false );
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partDeactivated(IWorkbenchPart part) {
        setEnabled( false );
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     */
    public void partOpened(IWorkbenchPart part) {
        setEnableState();
    }
}


