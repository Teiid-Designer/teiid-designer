/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors.sqleditor.actions;

import java.util.EventObject;
import org.eclipse.swt.SWT;
import org.teiid.core.designer.event.EventObjectListener;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlEditorInternalEvent;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlEditorPanel;
import org.teiid.designer.ui.common.actions.AbstractAction;


/**
 * The <code>ToggleOptimizer</code> class is the action that handles turning the sql optimizer on
 * and off.
 * @since 8.0
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
    
    @Override
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
