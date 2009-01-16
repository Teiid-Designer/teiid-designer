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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;

/**
 * INewSiblingAction is an interface for extensions of the New Sibling Action extension point.
 */
public interface INewSiblingAction extends IAction {

    /**
     * <p>Determine if this action should be displayed in the New Sibling menu based on the specified
     * sibling EObject.  This method is called every time the New Sibling menu is created.</p>
     * <p> NOTE: this method should not check the read-only status of the specified EObject or it's 
     * IResource.  That check is performed by the ModelerActionService. 
     * @return true if this action should be added to the New Sibling menu, false if it should not.
     */
    boolean canCreateSibling(EObject sibling);

}
