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
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;

/**
 * FontDownWrapper
 */
abstract class AbstractFontWrapper extends DiagramAction
                          implements DiagramUiConstants,
                                     IDiagramActionConstants,
                                     IPartListener,
                                     ActionWrapper,
                                     IFontChangeListener {

    /**
     * Construct an instance of FontDownWrapper.
     * 
     */
    
    protected DiagramEditor diagramEditor;
    
    
    public AbstractFontWrapper(DiagramEditor editor) {
        super();
        this.diagramEditor = editor;
        initialize();
    }

    public void initialize() {
                
        setEnableState();
        addAsPartListener();       
        
        ScaledFont sfFontMgr = getFontManager();
        if ( sfFontMgr != null ) {                
            sfFontMgr.removeFontChangeListener( this );
            sfFontMgr.addFontChangeListener( this );
        }
    }
    
    protected abstract IAction createAction();
    
	@Override
    protected void doRun() {
        // zoom in
        ScaledFont fontMgr = getFontManager();
        
        if ( fontMgr != null ) {             
            
            IAction action = createAction(); 
                         
            action.run();
            
            // after FontDownWrapper operation, reset enable state from the current fontMgr
            setEnabled(getEnabledState());             
        }   
    }
	
	protected abstract boolean getEnabledState();
    
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
        // no action here...font changes do not care about selection        
    }    
    
    protected ScaledFont getFontManager() {
        return (ScaledFont)diagramEditor.getAdapter(ScaledFont.class);
    }

    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    protected abstract void setEnableState();
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partActivated(IWorkbenchPart part) {
        if (isEventForThisPart(part)) {
            setEnableState();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     */
    public void partBroughtToTop(IWorkbenchPart part) {
        if (isEventForThisPart(part)) {
            setEnableState();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     */
    public void partClosed(IWorkbenchPart part) {
        if (isEventForThisPart(part)) {
            getPlugin().getCurrentWorkbenchWindow().getPartService().removePartListener( this );
        	getFontManager().removeFontChangeListener(this);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partDeactivated(IWorkbenchPart part) {
        if (isEventForThisPart(part)) {
            setEnableState();
        }
    }
    
    private boolean isEventForThisPart(IWorkbenchPart part) {
        if (part instanceof ModelEditor) {
            if (((ModelEditor) part).getAllEditors().contains(this.diagramEditor)) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     */
    public void partOpened(IWorkbenchPart part) {
        if (isEventForThisPart(part)) {
            setEnableState();
        }

    }
    
    /**
     * Called whenever the DiagramFontManager's font size changes.
     * 
     */
    public void fontChanged() {
        setEnableState();
    }
}
