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
 * The <code>CopyAction</code> class is the action that handles the global copy.
 * @since 8.0
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
    
    @Override
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
