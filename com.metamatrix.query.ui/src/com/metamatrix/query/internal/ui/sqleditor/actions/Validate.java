/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.actions;

//import org.eclipse.core.runtime.IStatus;
//import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.ui.IWorkbenchPart;

import java.util.EventObject;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.query.internal.ui.sqleditor.SqlEditorInternalEvent;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.query.ui.sqleditor.SqlEditorPanel;
import com.metamatrix.ui.actions.AbstractAction;

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
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
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
