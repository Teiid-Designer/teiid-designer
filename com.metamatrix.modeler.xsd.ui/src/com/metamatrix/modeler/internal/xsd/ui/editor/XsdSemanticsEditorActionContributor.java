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

package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.swt.widgets.Control;

import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;

/**
 * XsdSemanticsEditorActionContributor
 */
public class XsdSemanticsEditorActionContributor extends AbstractModelEditorPageActionBarContributor
                                               implements IModelerActionConstants,
                                                          ModelerXsdUiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    
    public XsdSemanticsEditorActionContributor(ModelEditorPage thePage) {
        super(thePage);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#createContextMenu()
     */
    @Override
    public void createContextMenu() {
        XsdSemanticsEditorPage editor = (XsdSemanticsEditorPage) getEditorPage();
        createContextMenu(getClass().getName() + ContextMenu.MENU_ID_SUFFIX, editor.getControl()); 
    }
    
    void addContextMenu(Control control, String controlName) {
        createContextMenu(controlName + ContextMenu.MENU_ID_SUFFIX, control);
    }
   

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#pageActivated()
     */
    @Override
    public void pageActivated() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#pageDeactivated()
     */
    @Override
    public void pageDeactivated() {

    }

}
