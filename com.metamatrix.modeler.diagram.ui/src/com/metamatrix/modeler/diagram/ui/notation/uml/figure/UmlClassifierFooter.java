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
