/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;


/**
 * ZoomIn
 */
public class ZoomIn extends DiagramAction
                 implements DiagramUiConstants,
                            IDiagramActionConstants,
                            IPartListener  {

    /**
     * Construct an instance of ZoomIn.
     * 
     */
    public ZoomIn() {
        super();
        initialize();
    }

    /**
     * Construct an instance of ZoomIn.
     * @param theStyle
     */
    public ZoomIn(int theStyle) {
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
            IAction action = new ZoomInAction(zoomMgr);
            action.setId(DiagramGlobalActions.ZOOM_IN);
            action.setText(Util.getString( ForeignActionProperties.ZOOMIN_TEXTID ) ); 
            
            ((ZoomInAction)action).run();
            
            // after ZoomIn operation, reset enable state from the current zoommanager
            setEnabled( zoomMgr.canZoomIn() );
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
            setEnabled( zoomMgr.canZoomIn() );
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
