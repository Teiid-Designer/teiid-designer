/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.ui.actions.AbstractAction;

/**
 * DrawingAction
 */
public class DiagramEditorAction extends AbstractAction {
    protected DiagramEditor editor;
    
    /**
     * Construct an instance of DiagramAction.
     * @param thePlugin
     */
    public DiagramEditorAction() {
        super(DiagramUiPlugin.getDefault());
    }

    /**
     * Construct an instance of DiagramAction.
     * @param thePlugin
     * @param theStyle
     */
    public DiagramEditorAction(int theStyle) {
        super(DiagramUiPlugin.getDefault(), theStyle );
    }

    @Override
    protected void doRun() {
    }
    
    public void setDiagramEditor(DiagramEditor editor) {
        this.editor = editor;
    }
}
