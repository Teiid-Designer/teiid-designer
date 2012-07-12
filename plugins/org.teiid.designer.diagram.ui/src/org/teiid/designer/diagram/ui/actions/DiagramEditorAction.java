/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.actions;

import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.ui.common.actions.AbstractAction;


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
