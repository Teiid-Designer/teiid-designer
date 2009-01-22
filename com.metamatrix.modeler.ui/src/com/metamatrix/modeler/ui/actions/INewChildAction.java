/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
