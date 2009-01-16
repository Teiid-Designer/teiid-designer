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
