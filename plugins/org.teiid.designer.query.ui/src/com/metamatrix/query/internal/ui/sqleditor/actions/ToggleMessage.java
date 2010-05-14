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
import org.eclipse.swt.SWT;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.query.internal.ui.sqleditor.SqlEditorInternalEvent;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.query.ui.sqleditor.SqlEditorPanel;
import com.metamatrix.ui.actions.AbstractAction;


/**
 * The <code>CopyAction</code> class is the action that handles the global copy.
 * @since 4.0
 */
public class ToggleMessage extends AbstractAction implements EventObjectListener {

    private SqlEditorPanel panel;
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ToggleMessage(SqlEditorPanel sqlPanel) {
        super(UiPlugin.getDefault(), SWT.TOGGLE);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.SHOW_MESSAGES));
        this.panel = sqlPanel;
        
        boolean isVisible = sqlPanel.isMessageAreaVisible();
        setChecked(isVisible);
        sqlPanel.addInternalEventListener( this );
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
        
    @Override
    protected void doRun() {
        boolean isShowing = panel.isMessageAreaVisible();
        panel.showMessageArea(!isShowing);
    }
    
    public void processEvent(EventObject e) {
        //----------------------------------------------------------------------
        // respond to internal events from SqlEditorPanel
        //   - action here is to ensure that the button toggle state is correct
        //----------------------------------------------------------------------
        if (e instanceof SqlEditorInternalEvent) {
            int type = ((SqlEditorInternalEvent)e).getType();
            if(type==SqlEditorInternalEvent.MESSAGE_VISIBILITY_CHANGED ) {
                // Make sure the button state is correct
                boolean isVisible = this.panel.isMessageAreaVisible();
                setChecked(isVisible);
            }
        } 
    }

}
