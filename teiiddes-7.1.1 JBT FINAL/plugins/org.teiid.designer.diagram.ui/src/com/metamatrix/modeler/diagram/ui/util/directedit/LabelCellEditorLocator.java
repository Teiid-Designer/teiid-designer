/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util.directedit;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

final public class LabelCellEditorLocator implements CellEditorLocator {

    private Label label;

    public LabelCellEditorLocator(Label label) {
        setLabel(label);
    }

    public void relocate(CellEditor celleditor) {
        Text text = (Text)celleditor.getControl();
        Point sel = text.getSelection();
        Point pref = text.computeSize(-1, -1);
        Rectangle rect = label.getTextBounds().getCopy();
        label.translateToAbsolute(rect);
        text.setBounds(rect.x - 4, rect.y - 1, pref.x + 1, pref.y + 1);
        text.setSelection(0);
        text.setSelection(sel);
    }

    /**
     * Returns the Label figure.
     */
    protected Label getLabel() {
        return label;
    }

    /**
     * Sets the label.
     * @param label The label to set
     */
    protected void setLabel(Label label) {
        this.label = label;
    }

}
