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
 * FigureFactory
 */
public interface DiagramFigureFactory {
    
    Figure createFigure(Object modelObject);
    
    Figure createFigure(Object modelObject, String sNotationId);

}
