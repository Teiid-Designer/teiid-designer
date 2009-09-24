/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.wizards.NewModelObjectWizardManager;

/**
 * The <code>NewAssociationAction</code> class creates a new association.
 * @since 4.0
 */
public class NewAssociationAction extends ModelObjectAction {
    
    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM = "NewAssociationAction.problem"; //$NON-NLS-1$
    private static final String NONE_ALLOWED = "NewAssociationAction.noneAllowed"; //$NON-NLS-1$

    //============================================================================================================================
    // Fields
    
    /** The child type descriptor. */
    private AssociationDescriptor descriptor;

    //============================================================================================================================
    // Constructors
    
    /**
     * Constructs a <code>NewChildAction</code> where no children are allowed. This action is not
     * enabled.
     */
    public NewAssociationAction() {
        super(UiPlugin.getDefault());
        configureNoneAllowedState();
    }
    
    /**
     * Constructs a <code>NewAssociationAction</code> where a new child is created.
     * @param theDescriptor the descriptor that determines the child type created
     */
    public NewAssociationAction(AssociationDescriptor theDescriptor) {
        super(UiPlugin.getDefault());
        setDescriptor(theDescriptor);
    }
    
    //============================================================================================================================
    // Methods
    
    /**
     * Configures the action by setting text, image, and enabled state.
     */
    private void configureAllowedState() {
        setText(descriptor.getText());
        Object o = descriptor.getImage();
        if ( o instanceof URL ) {
            o = ExtendedImageRegistry.INSTANCE.getImage(o);
        }
        if ( o instanceof Image ) {
            setImage((Image) o);
        }
        setEnabled(descriptor.isComplete());
    }

    /**
     * Configures the action by setting text and disabling it.
     */
    private void configureNoneAllowedState() {
        setText(getPluginUtils().getString(NONE_ALLOWED)); 
        setEnabled(false);
    }

    @Override
    protected void doRun() {
        /*
         * (jh: 08 23 2004: Fix for defect 13436: 
         * Removing code from this method that creates a Txn.
         * The Txn must be created down in the individual wizards (currently
         * Foreign Key and Relationship), so that those wizards can handle
         * a Cancel by doing a ROLLBACK.
         */
        if (descriptor != null) {
            try{
                Shell shell = super.getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell();
                if ( NewModelObjectWizardManager.isAssociationDescriptorValid(shell, descriptor, super.getSelection()) ) {
                    // the descriptor is a RelationshipDescriptor, it will already have done all the work.
                    if( !descriptor.creationComplete() )  {
                        try {
                            EObject newAssociation = ModelerCore.getModelEditor().createNewAssociationFromDescriptor(descriptor);
                            // Let's call ModelWorkspaceManager and force open an editor?
                            ModelEditorManager.open(newAssociation, true);                   
                        } catch (ModelerCoreException theException) {
                            String msg = getPluginUtils().getString(PROBLEM, new Object[] {descriptor});
                            getPluginUtils().log(IStatus.ERROR, theException, msg);
                            setEnabled(false);
                        }
                    } else {
                        EObject newAssociation = ((AbstractAssociationDescriptor)descriptor).getNewAssociation();
                        if( newAssociation != null ) {
                            ModelEditorManager.open(newAssociation, true);
                        }
                    }
                }
            } finally {
                
            }
        }
    }
    

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
    }
    
    /**
     * Sets the child type descriptor used to create the child.
     * @param theDescriptor the child type descriptor or <code>null</code> if no child can be created
     */
    public void setDescriptor(AssociationDescriptor theDescriptor) {
        descriptor = theDescriptor;

        if (descriptor == null) {
            configureNoneAllowedState();
        } else {
            configureAllowedState();
        }
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }
    
}
