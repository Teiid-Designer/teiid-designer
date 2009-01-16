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

package com.metamatrix.modeler.diagram.ui.figure;

import org.eclipse.draw2d.Figure;

/**
 * AbstractDiagramFigureFactory provides a base class for all Metamatrix Diagram Figure Factories
 * This class handles the create figure methods defined in the DiagramFigureFactory interface.
 */

public abstract class AbstractDiagramFigureFactory implements DiagramFigureFactory {
    
    // ===============================================
    // Methods
    // ===============================================
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.FigureFactory#createFigure(java.lang.Object)
     */
    public Figure createFigure(Object modelObject) {
        System.err.println("[AbstractDiagramFigureFactory.createFigure(modelObject)]  SHOULDN'T BE HERE!!!!!"); //$NON-NLS-1$
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.FigureFactory#createFigure(java.lang.Object, String)
     */
    public Figure createFigure(Object modelObject, String sNotationId) {
        System.err.println("[AbstractDiagramFigureFactory.createFigure(modelObject, notationId)]  SHOULDN'T BE HERE!!!!!"); //$NON-NLS-1$
        return null;
    }

}
