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
 * FontDownWrapper
 */
public class FontDownWrapper extends AbstractFontWrapper {

    public FontDownWrapper(DiagramEditor editor) {
        super(editor);
    }
    @Override
    protected IAction createAction() {
        return new FontDownAction(getFontManager());
    }
    @Override
    protected void setEnableState() {
        ScaledFont fontMgr = getFontManager();
           
        if ( fontMgr != null ) {
            setEnabled( fontMgr.canDecrease() );
        } else {
            setEnabled( false );                                            
        }
    }
    
    @Override
    protected boolean getEnabledState() {
        return getFontManager().canDecrease();
    }
    
}
