/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors.sqleditor.actions;

import java.util.EventObject;

import org.teiid.core.event.EventObjectListener;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlEditorInternalEvent;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlEditorPanel;
import org.teiid.designer.ui.common.actions.AbstractAction;


/**
 * The <code>Validate</code> class is the action that handles SQL editor validation
 * @since 4.0
 */
public class Validate extends AbstractAction implements EventObjectListener {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private SqlEditorPanel panel;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public Validate(SqlEditorPanel sqlPanel) {
        super(UiPlugin.getDefault());
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.VALIDATE));
        this.panel = sqlPanel;
        setEnabled(false);
        sqlPanel.addInternalEventListener( this );
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void doRun() {
        panel.validate();
    }
    
    /**
     * handle events from the SqlEditorPanel - allows action to enable/disable on changes in readonly state 
     * @see org.teiid.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    public void processEvent(EventObject e) {
        //------------------------------------------------
        // respond to internal events from SqlEditorPanel
        //------------------------------------------------
        if (e instanceof SqlEditorInternalEvent) {
            int type = ((SqlEditorInternalEvent)e).getType();
            if( type==SqlEditorInternalEvent.TEXT_RESET
             || type==SqlEditorInternalEvent.TEXT_CHANGED
             || type==SqlEditorInternalEvent.READONLY_CHANGED) {
                 boolean isEditable = panel.isEditable();
                 setEnabled(isEditable);
            }
        } 
    }    
    
}
