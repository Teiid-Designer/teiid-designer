/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.jface.action.IAction;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;

/**
 * FontUpWrapper
 */
public class FontUpWrapper extends AbstractFontWrapper {

    /**
     * Construct an instance of FontUpWrapper.
     * 
     */
    public FontUpWrapper(DiagramEditor diagramEditor) {
        super(diagramEditor);
    }
    @Override
    protected void setEnableState() {
        ScaledFont fontMgr = getFontManager();
            
        if ( fontMgr != null ) {
            setEnabled( fontMgr.canIncrease() );
        } else {
            setEnabled( false );                                            
        }        
    }
    
    
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#isEnabled()
     */
    @Override
    protected boolean getEnabledState() {
        return getFontManager().canIncrease();
    }

    @Override
    protected IAction createAction() {
        return new FontUpAction(getFontManager());
    }
}
