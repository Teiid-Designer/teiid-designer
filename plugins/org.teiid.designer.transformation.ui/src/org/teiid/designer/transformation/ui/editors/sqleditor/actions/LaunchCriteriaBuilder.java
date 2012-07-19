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
 * The <code>CopyAction</code> class is the action that handles the global copy.
 * @since 8.0
 */
public class LaunchCriteriaBuilder extends AbstractAction implements EventObjectListener {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private SqlEditorPanel panel;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public LaunchCriteriaBuilder(SqlEditorPanel sqlPanel) {
        super(UiPlugin.getDefault());
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.CRITERIA_BUILDER));
        this.panel = sqlPanel;
        setEnabled(false);
        sqlPanel.addInternalEventListener( this );
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void doRun() {
        // Tell sqlPanel to show the Criteria builder
        panel.showCriteriaBuilder();
    }
    
    @Override
	public void processEvent(EventObject e) {
        //------------------------------------------------
        // respond to internal events from SqlEditorPanel
        //------------------------------------------------
        if (e instanceof SqlEditorInternalEvent) {
            int type = ((SqlEditorInternalEvent)e).getType();
            if( type==SqlEditorInternalEvent.TEXT_RESET
             || type==SqlEditorInternalEvent.TEXT_CHANGED
             || type==SqlEditorInternalEvent.CARET_CHANGED
             || type==SqlEditorInternalEvent.READONLY_CHANGED) {
                 boolean canUseBuilder = panel.canUseCriteriaBuilder();
                 setEnabled(canUseBuilder);
            }
        } 
    }
    
}
