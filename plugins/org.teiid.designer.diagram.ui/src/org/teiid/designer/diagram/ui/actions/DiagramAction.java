/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.actions;

import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.ui.actions.ModelObjectAction;


/**
 * DiagramAction
 */
public class DiagramAction extends ModelObjectAction {

    /**
     * Construct an instance of DiagramAction.
     * @param thePlugin
     */
    public DiagramAction() {
        super(DiagramUiPlugin.getDefault());
    }

    /**
     * Construct an instance of DiagramAction.
     * @param thePlugin
     * @param theStyle
     */
    public DiagramAction(int theStyle) {
        super(DiagramUiPlugin.getDefault(), theStyle );
    }

	@Override
    protected void doRun() {
	}
    
    /* (non-Javadoc)
     * @see org.teiid.designer.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }

}
