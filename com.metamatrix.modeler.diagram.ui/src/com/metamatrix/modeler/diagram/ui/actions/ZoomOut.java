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

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
/**
 * ZoomOut
 * 
 * 
 */
public class ZoomOut extends DiagramAction
                  implements DiagramUiConstants,
                             IDiagramActionConstants,
                             IPartListener  {

    /**
     * Construct an instance of ZoomOut.
     * 
     */
    public ZoomOut() {
        super();
        initialize();
    }

    /**
     * Construct an instance of ZoomOut.
     * @param theStyle
     */
    public ZoomOut(int theStyle) {
        super(theStyle);
        initialize();
    }
    
    private void initialize() {
        addAsPartListener();        
    }
    
    @Override
    protected void doRun() {
        // zoom in
        IEditorPart editor = getActiveEditor();
        
        if ( editor != null ) {
             
            ZoomManager zoomMgr = (ZoomManager)editor.getAdapter(ZoomManager.class);
            IAction action = new ZoomOutAction(zoomMgr);
            action.setId(DiagramGlobalActions.ZOOM_IN);
            action.setText(Util.getString( ForeignActionProperties.ZOOMOUT_TEXTID ) ); 
            
            ((ZoomOutAction)action).run();
            
            // after ZoomOut operation, reset enable state from the current zoommanager
            setEnabled( zoomMgr.canZoomOut() );
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
        // no action here...zooms do not care about selection        
    }    
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    public void setEnableState() {

        IEditorPart editor = getActiveEditor();
        
        if ( editor != null ) {             
            ZoomManager zoomMgr = (ZoomManager)editor.getAdapter(ZoomManager.class);
            setEnabled( zoomMgr.canZoomOut() );
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
