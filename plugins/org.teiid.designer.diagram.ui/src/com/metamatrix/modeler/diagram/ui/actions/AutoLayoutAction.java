/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

/**
 * AutoLayoutAction
 */
public class AutoLayoutAction extends DiagramAction {
    private AutoLayout autoLayoutManager;
    /**
     * Construct an instance of FontUpAction.
     * 
     */
    public AutoLayoutAction(AutoLayout autoLayoutManager) {
        super();
        this.autoLayoutManager = autoLayoutManager;
    }
    
    @Override
    protected void doRun() {
        autoLayoutManager.autoLayout();
    }
}
