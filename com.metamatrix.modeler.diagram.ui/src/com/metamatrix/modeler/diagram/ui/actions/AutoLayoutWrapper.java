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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;

/**
 * AutoLayoutWrapper
 */
public class AutoLayoutWrapper extends DiagramAction
                          implements DiagramUiConstants,
                                     IDiagramActionConstants,
                                     IPartListener,
                                     ActionWrapper {

    /**
     * Construct an instance of AutoLayoutWrapper.
     * 
     */
    public AutoLayoutWrapper() {
        super();
        initialize();
    }

    /**
     * Construct an instance of AutoLayoutWrapper.
     * @param theStyle
     */
    public AutoLayoutWrapper(int theStyle) {
        super(theStyle);
        initialize();
        
    }

    public void initialize() {

        setEnableState();
        addAsPartListener();        
    }

	@Override
    protected void doRun() {
        IEditorPart editor = getActiveEditor();
        
        if ( editor != null ) {
             
            AutoLayout autoLayoutMgr = getAutoLayoutManager();
            IAction action = new AutoLayoutAction(autoLayoutMgr);
                         
            ((AutoLayoutAction)action).run();
            
            // after AutoLayoutWrapper operation, reset enable state from the current autoLayoutMgr
            setEnabled( autoLayoutMgr.canAutoLayout() );             
        }   
    }
    
    private void addAsPartListener() {
        getPlugin().getCurrentWorkbenchWindow().getPartService().addPartListener( this );
    }
    
    /**
     *  
     * @see com.metamatrix.ui.actions.AbstractAction#dispose()
     * @since 5.0
     */
    @Override
    public void dispose() {
        getPlugin().getCurrentWorkbenchWindow().getPartService().removePartListener( this );
        super.dispose();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        // no action here...autolayout does not care about selection        
    }    

    private AutoLayout getAutoLayoutManager() {
        ModelEditor editor = getActiveEditor();

        if ( editor != null && editor.getCurrentPage() instanceof DiagramEditor ) {       
            DiagramEditor deEditorPage = (DiagramEditor)editor.getCurrentPage();
            if ( deEditorPage != null ) {             
                AutoLayout autoLayoutMgr = (AutoLayout)deEditorPage.getAdapter(AutoLayout.class);
                return autoLayoutMgr;
            }
            return null;
        }
        return null;
    }

    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    public void setEnableState() {

        ModelEditor editor = getActiveEditor();
        
        if ( editor != null && editor.getCurrentPage() instanceof DiagramEditor ) {             
            AutoLayout autoLayoutMgr = getAutoLayoutManager();
            
            if ( autoLayoutMgr != null ) {
//                System.out.println("[AutoLayoutWrapper.setEnableState] about to set using canAutoLayout"); //$NON-NLS-1$
                setEnabled( autoLayoutMgr.canAutoLayout() );                             
            } else {
//                System.out.println("[AutoLayoutWrapper.setEnableState] no autolayoutmgr found; about to set to false"); //$NON-NLS-1$
                setEnabled( false );
            }
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
