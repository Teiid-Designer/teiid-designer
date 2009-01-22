/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.graphics.Color;

/**
 * @author blafond
 *
 * Simple rectangle figure designed to be placed as a footer to the
 * UmlClassifierFigure
 */
public class UmlClassifierFooter extends RectangleFigure {
    /**
     * 
     */
    public UmlClassifierFooter() {
        super();
        init(); 
        createComponent();
   }

    private void init() {
        this.setLineWidth(0);
        this.setForegroundColor(ColorConstants.cyan);
    }
    public void setDefaultBkgdColor(Color bkgdColor) {
        this.setBackgroundColor(bkgdColor);
        this.setForegroundColor(bkgdColor);
    }
    private void createComponent() {
        this.setSize(10, 10);
    }
    
    @Override
    protected boolean useLocalCoordinates(){
        return true;
    }
}
