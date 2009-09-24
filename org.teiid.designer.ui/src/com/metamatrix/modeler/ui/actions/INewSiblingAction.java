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
