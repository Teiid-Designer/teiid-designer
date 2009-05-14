/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.figure;

import org.eclipse.draw2d.Figure;

/**
 * AbstractDiagramFigureFactory provides a base class for all Diagram Figure Factories This class handles the create figure
 * methods defined in the DiagramFigureFactory interface.
 */

public abstract class AbstractDiagramFigureFactory implements DiagramFigureFactory {

    /**
     * @see com.metamatrix.modeler.diagram.ui.FigureFactory#createFigure(java.lang.Object)
     */
    public Figure createFigure( Object modelObject ) {
        System.err.println("[AbstractDiagramFigureFactory.createFigure(modelObject)]  SHOULDN'T BE HERE!!!!!"); //$NON-NLS-1$
        return null;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.FigureFactory#createFigure(java.lang.Object, String)
     */
    public Figure createFigure( Object modelObject,
                                String sNotationId ) {
        System.err.println("[AbstractDiagramFigureFactory.createFigure(modelObject, notationId)]  SHOULDN'T BE HERE!!!!!"); //$NON-NLS-1$
        return null;
    }

}
