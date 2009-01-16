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

import java.util.EventObject;

import org.eclipse.swt.SWT;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.query.internal.ui.sqleditor.SqlEditorInternalEvent;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.query.ui.sqleditor.SqlEditorPanel;
import com.metamatrix.ui.actions.AbstractAction;

/**
 * The <code>ToggleOptimizer</code> class is the action that handles turning the sql optimizer on
 * and off.
 * @since 4.0
 */
public class ToggleOptimizer extends AbstractAction implements EventObjectListener {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private SqlEditorPanel panel;
    private boolean allowOptimization = true;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ToggleOptimizer(SqlEditorPanel sqlPanel) {
        super(UiPlugin.getDefault(), SWT.TOGGLE);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.SHORT_NAMES));
        this.panel = sqlPanel;
        // Optimizer is alway enabled, even though optimization may not always be possible
        setEnabled(true);
        boolean isOn = sqlPanel.isOptimizerOn();
        setChecked(isOn);
        sqlPanel.addInternalEventListener( this );
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public void processEvent(EventObject e) {
        if( allowOptimization ) {
            //----------------------------------------------------------------------
            // respond to internal events from SqlEditorPanel
            //   - action here is to ensure that the button toggle state is correct
            //----------------------------------------------------------------------
            if (e instanceof SqlEditorInternalEvent) {
                int type = ((SqlEditorInternalEvent)e).getType();
                if(type==SqlEditorInternalEvent.OPTIMIZER_STATE_CHANGED ||
                   type==SqlEditorInternalEvent.TEXT_CHANGED ||
                   type==SqlEditorInternalEvent.TEXT_RESET ) {
                    // Make sure the button state is correct
                    boolean isOn = this.panel.isOptimizerOn();
                    setChecked(isOn);
                    // Disable for pending changes
                    if( this.panel.hasPendingChanges() ) {
                        setEnabled(false);
                    } else {
                        setEnabled(true);
                    }
                }
            }
        }
    }
    
//    private void setEnabledState(int eventType){
//        // Handle Optimizer state changed
//        if(eventType==SqlEditorInternalEvent.OPTIMIZER_STATE_CHANGED) {
//            //boolean isOptimizerOn = this.panel.isOptimizerOn();
//            boolean isOptimizerEnabled = this.panel.isOptimizerEnabled();
//            //boolean isButtonOn = this.isChecked();
//            boolean isButtonEnabled = this.isEnabled();
//            //if(isOptimizerOn!=isButtonOn) {
//            //    setChecked(isOptimizerOn);
//            //}
//            if(isOptimizerEnabled!=isButtonEnabled) {
//                setEnabled(isOptimizerEnabled);
//            }
//        }
//    }
    
    @Override
    protected void doRun() {
        if( allowOptimization ) {
            // Get the current optimizer state
            boolean optimizerOn = this.panel.isOptimizerOn();
            // toggle the state
            this.panel.setOptimizerOn(!optimizerOn);
        }
    }

    
    /** 
     * @param theAllowOptimization The allowOptimization to set.
     * @since 5.0
     */
    public void setAllowOptimization(boolean theAllowOptimization) {
        this.allowOptimization = theAllowOptimization;
        this.setEnabled(allowOptimization);
    }
    
}
