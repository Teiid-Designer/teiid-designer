/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards;

import org.eclipse.ui.IWorkbenchWizard;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * INewObjectWizard
 */
public interface INewObjectWizard extends IWorkbenchWizard {

    void setModel(ModelResource model);
    
    boolean completedOperation();

}
