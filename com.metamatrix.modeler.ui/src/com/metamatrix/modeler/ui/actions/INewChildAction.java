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
 * INewChildAction is an interface for extensions of the New Child Action extension point.
 */
public interface INewChildAction extends IAction {

    /**
     * <p>Determine if this action should be displayed in the New Child menu based on the specified
     * parent EObject.  This method is called every time the New Child menu is created.</p>
     * <p> NOTE: this method should not check the read-only status of the specified EObject or it's 
     * IResource.  That check is performed by the ModelerActionService. 
     * @return true if this action should be added to the New Child menu, false if it should not.
     */
    boolean canCreateChild(EObject parent);

}
