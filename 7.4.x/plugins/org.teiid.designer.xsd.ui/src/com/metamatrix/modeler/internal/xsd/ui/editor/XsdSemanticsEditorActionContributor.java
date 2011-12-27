/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
