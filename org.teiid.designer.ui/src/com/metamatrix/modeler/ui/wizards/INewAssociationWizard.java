/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards;

import com.metamatrix.modeler.core.association.AssociationDescriptor;

/**
 * INewAssociationWizard
 */
public interface INewAssociationWizard extends INewObjectWizard {

    void setAssociationDescriptor(AssociationDescriptor descriptor);
    
}
