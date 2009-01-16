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

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Text;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.*;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;

import com.metamatrix.ui.graphics.GlobalUiFontManager;

public class DirectEditPartManager extends DirectEditManager {

    Font scaledFont;

    public DirectEditPartManager(GraphicalEditPart source, Class<? extends CellEditor> editorType, CellEditorLocator locator) {
        super(source, editorType, locator);
    }

    /**
     * @see org.eclipse.gef.tools.DirectEditManager#bringDown()
     */
    @Override
    protected void bringDown() {
        //This method might be re-entered when super.bringDown() is called.
        Font disposeFont = scaledFont;
        scaledFont = null;
        super.bringDown();
        if (disposeFont != null)
            disposeFont.dispose();
    }

    @Override
    protected void initCellEditor() {
        if( getEditPart() instanceof DirectEditPart ) {
            DirectEditFigure figure = (DirectEditFigure)getEditPart().getFigure();
            
            Label label = figure.getLabelFigure();
            String initialLabelText = ((DirectEditPart)getEditPart()).getEditString();
            getCellEditor().setValue(initialLabelText);
            Text text = (Text)getCellEditor().getControl();

            scaledFont = label.getFont();
            FontData data = scaledFont.getFontData()[0];
            Dimension fontSize = new Dimension(0, data.getHeight());
            label.translateToAbsolute(fontSize);
            data.setHeight(fontSize.height);
            scaledFont = GlobalUiFontManager.getFont(data);
    
            text.setFont(scaledFont);
            text.selectAll();
        }
    }
    
    public void commitAndDispose() {
    	if( getCellEditor() != null && getCellEditor().isActivated() )
    		super.commit();
    }

}
