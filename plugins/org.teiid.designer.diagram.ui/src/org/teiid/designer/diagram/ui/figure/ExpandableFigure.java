/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.figure;

import org.eclipse.draw2d.IFigure;

/**
 * @since 8.0
 */
public interface ExpandableFigure {
	
    IFigure getExpansionFigure();
    
    void expand();
    
    void collapse();
    
    boolean isExpandable();
    
    void setExpandable(boolean expandable);
}
