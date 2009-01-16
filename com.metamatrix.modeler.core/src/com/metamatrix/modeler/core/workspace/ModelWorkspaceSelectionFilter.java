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

package com.metamatrix.modeler.core.workspace;

/**
 * The ModelWorkspaceSelectionFilter interface defines the operations that allow
 * a {@link ModelWorkspaceSelections} to determine whether the selection state 
 * of an object can be explicitly set, or whether the selection state of an object
 * is determined from other objects.
 */
public interface ModelWorkspaceSelectionFilter {
    
    /**
     * Returns whether the given element makes it through this filter.
     *
     * @param element the element
     * @return <code>true</code> if element is selectable, 
     * and <code>false</code> otherwise.
     */
    public boolean isSelectable( Object element );

}
