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
