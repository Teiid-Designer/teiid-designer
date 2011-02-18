/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;

/**
 * @author BLaFond
 *
 * This interface allows metamodel specific plugins to play in the global editor
 * action service.  Since some metamodel objects may have certain global edit policies
 * (i.e. can't delete some objects), there needed to be a way to override the actions.
 * This interface defines the methods available.
 */
public interface IModelObjectEditHelper {
	/**
	 * Can rename diagram method
	 * @param diagram
	 * @return canRename
	 */
	boolean canRename(Object obj);

	/**
	 * Can copy diagram method
	 * @param diagram
	 * @return canCopy
	 */
	boolean canCopy(Object obj);

    /**
     * Can copy diagram method
     * @param diagram
     * @return canUndoCopy
     */
    boolean canUndoCopy(Object obj);

    
    /**
	 * Can cut diagram method
	 * @param diagram
	 * @return canCut
	 */
	boolean canCut(Object obj);
	
    /**
     * Can cut diagram method
     * @param diagram
     * @return canUndoCut
     */
    boolean canUndoCut(Object obj);

    
    /**
	 * Can delete diagram method
	 * @param diagram
	 * @return canDelete
	 */
	boolean canDelete(Object obj);

    /**
     * Can delete diagram method
     * @param diagram
     * @return canUndoDelete
     */
    boolean canUndoDelete(Object obj);
    
    
	/**
	 * Can clone diagram method
	 * @param diagram
	 * @return canClone
	 */
	boolean canClone(Object obj);
	
	/**
	 * Can paste diagram method
	 * @param diagram
	 */
	boolean canPaste(Object obj, Object pasteParent);

    /**
     * Can paste diagram method
     * @param diagram
     */
    boolean canUndoPaste(Object obj, Object pasteParent);

}
