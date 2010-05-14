/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import com.metamatrix.modeler.ui.UiConstants;


/**
 * DelegatableAction
 */
public class DelegatableAction extends Action
                     implements ISelectionListener {
        
                       
    // ===================================================
    //    variables
    // ===================================================
        
    private IActionDelegate delegate;
//    private ISelection selection;
    private IWorkbenchWindow window;
    private static final String MISSING_WINDOW
        = UiConstants.Util.getString("DelegatableAction.WorkbenchWindowIsNullMessage"); //$NON-NLS-1$


    // ===================================================
    //    Constructor
    // ===================================================

    public DelegatableAction( IActionDelegate delegate ) {
                    
        super( null, SWT.NONE ); 
        this.delegate    = delegate;
        init();
    }

    public DelegatableAction( IActionDelegate delegate, IWorkbenchWindow window ) {
                    
        super( null, SWT.NONE ); 
        this.delegate    = delegate;
        this.window      = window;
        init();
    }
    
    // ===================================================
    //    Methods
    // ===================================================
        
    /** 
     * Initialize the delegate
     */
    public void init() {
        if ( delegate instanceof IWorkbenchWindowActionDelegate ) {                            
//            System.out.println("[ModelerActionService$DelegatableAction.init] about to call delegate.init() on delegate: " + delegate.toString() );
            ((IWorkbenchWindowActionDelegate)delegate).init( getWorkbenchWindow() );
        } 
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.ActionService#getWorkbenchWindow()
     */
    public IWorkbenchWindow getWorkbenchWindow() {
        if (window == null) {
            throw new IllegalStateException( MISSING_WINDOW ); 
        }

        return window;
    }

    /**
     * The <code>SelectionChangedEventAction</code> implementation of this 
     * <code>ISelectionListener</code> method calls 
     * <code>selectionChanged(IStructuredSelection)</code> when the selection is
     * a structured one. Subclasses may extend this method to react to the change.
     */
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
        selectionChanged( sel );
    }
        
    /**
     * Handles selection change. If rule-based enabled is
     * defined, it will be first to call it. If the delegate
     * is loaded, it will also be given a chance.
     */
    public void selectionChanged( ISelection selection ) {
        // Update selection.
//        this.selection = selection;
            
        if (selection == null) {            
            selection = StructuredSelection.EMPTY;
        }
            
//        System.out.println("[ModelerActionService$DelegatableAction.init] about to call delegate.selectionChanged() on delegate: " + delegate.toString() );
        delegate.selectionChanged( this, selection );
    }

    /**
     * Handles selection change. If rule-based enabled is
     * defined, it will be first to call it. If the delegate
     * is loaded, it will also be given a chance.
     */
    @Override
    public void run() {
//        System.out.println("[ModelerActionService$DelegatableAction.init] about to call delegate.run() on delegate: " + delegate.toString() );
        delegate.run( this );
    }
}
