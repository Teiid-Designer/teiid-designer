/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.part;

public interface ExpandableDiagram {
	
	boolean canExpand();
	
	void collapse(Object child);
	
	void expand(Object child);
	
	void collapseAll();
	
	void expandAll();
}
