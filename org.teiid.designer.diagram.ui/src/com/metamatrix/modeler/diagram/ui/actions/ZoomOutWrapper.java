/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.ZoomableEditor;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
/**
 * ZoomOutWrapper
 * 
 * 
 */
public class ZoomOutWrapper extends DiagramAction
                  implements DiagramUiConstants,
                             IDiagramActionConstants,
                             IPartListener,
                             ActionWrapper,
                             ZoomListener  {
                             	
	private static final double[] zoomValues =
		{ 0.1, 0.25, 0.50, 0.75, 0.9, 1.0, 1.10, 1.25, 1.50, 2.0, 3.0, 4.0, 10.0 };
	private boolean wasInitialized = false;
	private ZoomManager zoomManager;

    /**
     * Construct an instance of ZoomOutWrapper.
     * 
     */
    public ZoomOutWrapper() {
        super();
    }

    /**
     * Construct an instance of ZoomOutWrapper.
     * @param theStyle
     */
    public ZoomOutWrapper(int theStyle) {
        super(theStyle);
    }
    
    public void initialize() {
		if (getZoomManager() != null) {
			getZoomManager().setZoomLevels(zoomValues);
			if( !wasInitialized ) {
				wasInitialized = true;
				getZoomManager().setZoom(getEditorZoomLevel());
			}  
			getZoomManager().addZoomListener(this);
		}
		setEnableState();
		addAsPartListener();       
    } 
    
    @Override
    protected void doRun() {
        // zoom out
        ZoomManager zoomMgr = getZoomManager();
        
        if ( zoomMgr != null ) {
 
			ZoomOutAction action = new ZoomOutAction(zoomMgr);
            
            action.run();
            
            DiagramEditor de = getDiagramEditor();
            if( de != null ) {
                de.handleZoomChanged();
            }
            
			zoomMgr.removeZoomListener(action);
            
            // after ZoomOutWrapper operation, reset enable state from the current zoommanager
            setEnabled( zoomMgr.canZoomOut() );
        }   
    }
    
    private DiagramEditor getDiagramEditor() {
        ModelEditor editor = getActiveEditor();
        
        if (editor != null && editor.getCurrentPage() instanceof ZoomableEditor) {

            DiagramEditor deEditorPage = ((ZoomableEditor)editor.getCurrentPage()).getDiagramEditor();
            if (deEditorPage != null) {
                return deEditorPage;
            }
        }
        
        return null;
    }
  
	public void closeZoomManager() {
		if( zoomManager != null ) {
			zoomManager.removeZoomListener(this);
			zoomManager = null;
		}
	}

    private double getEditorZoomLevel() {
        double currentZoom = 1.0;
        DiagramEditor de = getDiagramEditor();
        if( de != null ) {
            currentZoom = de.getCurrentZoomFactor();
        }
        return currentZoom;
    }
    
    private ZoomManager getZoomManager() {
    	if( zoomManager == null ) {
	        ModelEditor editor = getActiveEditor();
	        
	        if ( editor != null && editor.getCurrentPage() instanceof ZoomableEditor) {
	
	            DiagramEditor deEditorPage = ((ZoomableEditor)editor.getCurrentPage()).getDiagramEditor();
	            if ( deEditorPage != null ) {             
	                zoomManager = (ZoomManager)deEditorPage.getAdapter(ZoomManager.class);
	                initialize();
	            } 
	        }
		}
        return zoomManager;   
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
//        System.out.println("[ZoomOutWrapper.setEnableState] TOP"); //$NON-NLS-1$

        ModelEditor editor = getActiveEditor();
        
        if ( editor != null && editor.getCurrentPage() instanceof DiagramEditor ) {             
            ZoomManager zoomMgr = getZoomManager();
            
            if ( zoomMgr != null ) {                          
                setEnabled( zoomMgr.canZoomOut() );
            } else {
                setEnabled( false );                                            
            }
        }      }

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
                
    public void zoomChanged( double d ) {
        // reset enable state; a change in zoom IN may affect us here in zoom OUT
//        System.out.println("[ZoomInWrapper.zoomChanged] !!!" ); //$NON-NLS-1$
        setEnableState();
    }
               
}
