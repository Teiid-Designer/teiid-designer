/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;

/**
 * @author BLaFond
 *
 * This interface provides diagram plugin implementations to play in the global editor
 * action service.  Since some diagrams may be canned, some custom, this class is required to 
 * make this work.
 */
public interface IDiagramHelper {
	/**
	 * Can rename diagram method
	 * @param diagram
	 * @return canRename
	 */
	boolean canRename(Diagram diagram);
	
	/**
	 * Rename diagram method
	 * @param diagram
	 */
	void rename(Diagram diagram);

	/**
	 * Can copy diagram method
	 * @param diagram
	 * @return canCopy
	 */
	boolean canCopy(Diagram diagram);
	
	/**
	 * Copy diagram method
	 * @param diagram
	 */
	void copy(Diagram diagram);

	/**
	 * Can cut diagram method
	 * @param diagram
	 * @return canCut
	 */
	boolean canCut(Diagram diagram);
	
	/**
	 * Cut diagram method
	 * @param diagram
	 */
	void cut(Diagram diagram);
	
	/**
	 * Can delete diagram method
	 * @param diagram
	 * @return canDelete
	 */
	boolean canDelete(Diagram diagram);
	
	/**
	 * Delete diagram method
	 * @param diagram
	 */
	void delete(Diagram diagram);
    
    /**
     * Can clone diagram method
     * @param diagram
     * @return canClone
     */
	boolean canClone(Diagram diagram);
	
	/**
	 * Clone diagram method
	 * @param diagram
	 */
	void clone(Diagram diagram);
	
	/**
	 * Can create diagram method
	 * @param diagram
	 */
	boolean canCreate(Diagram diagram);
	
	/**
	 * Can paste diagram method
	 * @param diagram
	 */
	boolean canPaste(Diagram diagram, EObject pasteParent);
	
	
	/**
	 * Paste diagram method
	 * @param diagram
	 * @param pasteParent
	 */
	void paste(Diagram diagram, EObject pasteParent);
}
