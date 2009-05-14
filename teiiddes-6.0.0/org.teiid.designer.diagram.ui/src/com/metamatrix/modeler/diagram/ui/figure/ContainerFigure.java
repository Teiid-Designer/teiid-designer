/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.figure;

import org.eclipse.draw2d.IFigure;

/**
 * ContainerFigure
 * 
 * This interface class is designed to provide a generic interface between an container type 
 * edit part and it's content pane.  The ClassifierContainer needs to act like a "canvas" and thus
 * provide it's own content pane to the gef world.
 */
public interface ContainerFigure {
    
    IFigure getContentsPane();

}
