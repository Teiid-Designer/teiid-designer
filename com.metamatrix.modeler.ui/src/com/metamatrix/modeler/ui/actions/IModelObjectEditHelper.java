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
