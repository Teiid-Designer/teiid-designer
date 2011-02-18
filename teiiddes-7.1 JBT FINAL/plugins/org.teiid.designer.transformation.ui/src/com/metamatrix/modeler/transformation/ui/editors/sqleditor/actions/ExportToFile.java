/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.editors.sqleditor.actions;

import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlEditorPanel;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.actions.AbstractAction;

/**
 * The <code>ExportToFile</code> class is the action that Exports the SqlEditor Text to
 * a User file.
 * @since 4.0
 */
public class ExportToFile extends AbstractAction {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private SqlEditorPanel panel;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ExportToFile(SqlEditorPanel sqlPanel) {
        super(UiPlugin.getDefault());
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.EXPORT_TO_FILE));
        this.panel = sqlPanel;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void doRun() {
        panel.exportToFile();
    }
        
}
