/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd.ui.editor;

import org.eclipse.swt.widgets.Control;
import org.teiid.designer.ui.actions.IModelerActionConstants;
import org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.xsd.ui.ModelerXsdUiConstants;


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
     * @See org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor#createContextMenu()
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
     * @See org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor#pageActivated()
     */
    @Override
    public void pageActivated() {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor#pageDeactivated()
     */
    @Override
    public void pageDeactivated() {

    }

}
