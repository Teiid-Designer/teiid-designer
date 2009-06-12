/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
