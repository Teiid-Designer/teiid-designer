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
